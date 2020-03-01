package np.playground.desktopapp.core;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import np.playground.desktopapp.core.model.DesktopModule;
import np.playground.desktopapp.core.utils.FXDesktopUtil;
import np.playground.desktopapp.core.view.controls.mdi.InternalWindow;
import np.playground.desktopapp.core.view.controls.module.Page;
import np.playground.desktopapp.core.view.controls.module.Tab;
import np.playground.desktopapp.core.view.controls.module.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static np.playground.desktopapp.core.utils.FXDesktopUtil.convertToId;

public class FXDesktop extends Control {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    // Default values
    private static final Callback<FXDesktop, Tab> DEFAULT_TAB_FACTORY = Tab::new;
    private static final Callback<FXDesktop, Tile> DEFAULT_TILE_FACTORY = Tile::new;
    private static final Callback<FXDesktop, Page> DEFAULT_PAGE_FACTORY = Page::new;
    private static final int DEFAULT_MODULES_PER_PAGE = 9;
    public static final Background DEFAULT_WALLPAPER = FXDesktopUtil.createBackground("Flow2.jpg");

    /**
     * List of Modules, currently open modules
     */
    private final ListProperty<DesktopModule> modules =
            new SimpleListProperty<>(this, "modules", FXCollections.observableArrayList());
    private final ListProperty<DesktopModule> openModules =
            new SimpleListProperty<>(this, "modules", FXCollections.observableArrayList());
    /**
     * Currently active module. Active module is the module, which is currently being displayed in the
     * view. When the home screen is being displayed, {@code activeModule} and {@code
     * activeModuleView} are null.
     */
    private final ObjectProperty<DesktopModule> activeModule =
            new SimpleObjectProperty<>(this, "activeModule");
    private final ObjectProperty<Node> activeModuleView =
            new SimpleObjectProperty<>(this, "activeModuleView");
    /**
     * Visibility of the App Menu (AddModuleView)
     */
    private final BooleanProperty addModuleVisible = new SimpleBooleanProperty(this, "addModuleVisible", false);

    /**
     * Number of Modules (Tiles) per Page on AddModuleView
     */
    private final IntegerProperty modulesPerPage =
            new SimpleIntegerProperty(this, "modulesPerPage", DEFAULT_MODULES_PER_PAGE);

    /**
     * Total Number of Pages
     */
    private final IntegerProperty amountOfPages = new SimpleIntegerProperty(this, "amountOfPages");


    private final Map<DesktopModule, CompletableFuture<Boolean>> moduleCloseableMap =
            new HashMap<>();

    /**
     * Holds reference of internalWindows
     */
    private final ListProperty<InternalWindow> internalWindows = new SimpleListProperty<>(this, "internalWindows",
            FXCollections.observableArrayList());

    private final ObjectProperty<Background> wallpaper = new SimpleObjectProperty<>(this, "wallpaper", DEFAULT_WALLPAPER);
    // Factories
    /**
     * The factories which are called when creating Tabs, Tiles and Pages of Tiles for the Views. They
     * require a module whose attributes are used to create the Nodes.
     */
    private final ObjectProperty<Callback<FXDesktop, Tab>> tabFactory =
            new SimpleObjectProperty<>(this, "tabFactory", DEFAULT_TAB_FACTORY);
    private final ObjectProperty<Callback<FXDesktop, Tile>> tileFactory =
            new SimpleObjectProperty<>(this, "tileFactory", DEFAULT_TILE_FACTORY);
    private final ObjectProperty<Callback<FXDesktop, Page>> pageFactory =
            new SimpleObjectProperty<>(this, "pageFactory", DEFAULT_PAGE_FACTORY);


    /**
     * Default Constructor
     */
    public FXDesktop() {
        initBindings();
        initListeners();
        // initNavigationDrawer(getNavigationDrawer());
        setupCleanup();
        addStyleSheets();
        getStyleClass().add("fx-desktop");
    }

    /**
     * Constructor using builder
     *
     * @param builder
     */
    public FXDesktop(FXDesktopBuilder builder) {
        this();
        setModulesPerPage(builder.modulesPerPage);
        initFactories(builder);
        initModules(builder);
    }

    private void initBindings() {
        amountOfPages.bind(
                Bindings.createIntegerBinding(
                        this::calculateAmountOfPages, modulesPerPageProperty(), getModules()
                )
        );
    }

    private void initListeners() {
        activeModule.addListener((observable, oldModule, newModule) -> {
            LOGGER.trace("Module Listener - Old Module: " + oldModule);
            LOGGER.trace("Module Listener - New Module: " + newModule);
            if (oldModule != newModule) {
                boolean fromHomeScreen = oldModule == null;
                LOGGER.trace("Active Module Listener - Previous view home screen: " + fromHomeScreen);
                boolean fromDestroyed = !openModules.contains(oldModule);
                LOGGER.trace("Active Module Listener - Previous module destroyed: " + fromDestroyed);
                if (!fromHomeScreen && !fromDestroyed) {
                    // switch from one module to another
                    LOGGER.trace("Active Module Listener - Deactivating old module - " + oldModule);
                    oldModule.deactivate();
                }
                boolean toHomeScreen = newModule == null;
                if (toHomeScreen) {
                    // switch to home screen
                    LOGGER.trace("Active Module Listener - Switched to home screen");
                    activeModuleView.setValue(null);
                    return;
                }
                if (!openModules.contains(newModule)) {
                    // module has not been loaded yet
                    LOGGER.trace("Active Module Listener - Initializing module - " + newModule);
                    newModule.init(this);
                    resetModuleCloseable(newModule); // initialize closing on call to #close()
                    openModules.add(newModule);
                }
                LOGGER.trace("Active Module Listener - Activating module - " + newModule);
                activeModuleView.setValue(newModule.activate());
            }
        });
    }

    private void setupCleanup() {
        Platform.runLater(() -> {
            Scene scene = getScene();
            // if there is no scene, don't cause NPE by calling "getWindow()" on null
            if (Objects.isNull(scene)) {
                // should only be thrown in tests with mocked views
                LOGGER.error("setupCleanup - Scene could not be found! setOnCloseRequest was not set");
                return;
            }

            Stage stage = (Stage) getScene().getWindow();
            // when application is closed, destroy all modules
            stage.setOnCloseRequest(event -> {
                LOGGER.trace("Stage was requested to be closed");
                event.consume(); // we need to perform some cleanup actions first

                // close all open modules until one returns false
                while (!getOpenModules().isEmpty()) {
                    DesktopModule openModule = getOpenModules().get(0);
                    if (!closeModule(openModule)) {
                        LOGGER.trace("Module " + openModule + " could not be closed yet");

                        // once module is ready to be closed, start stage closing process over again
                        getModuleCloseable(openModule).thenRun(() -> {
                            LOGGER.trace("moduleCloseable - Stage - thenRun triggered: " + openModule);
                            LOGGER.trace(openModule + " restarted stage closing process");
                            // re-start closing process, in case other modules are blocking the closing process
                            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                        });
                        LOGGER.trace("moduleCloseable - Stage - thenRun set: " + openModule);

                        break; // interrupt closing until the interrupting module has been safely closed
                    }
                }

                if (getOpenModules().isEmpty()) {
                    LOGGER.trace("All modules could be closed successfully, closing stage");
                    stage.close();
                }
            });
        });
    }

    private void addStyleSheets() {
    }

    private void setModulesPerPage(int modulesPerPage) {
        LOGGER.trace("Modules per page {}", modulesPerPage);
        this.modulesPerPage.set(modulesPerPage);
    }

    private void initFactories(FXDesktopBuilder builder) {
        tabFactory.set(builder.tabFactory);
        tileFactory.set(builder.tileFactory);
        pageFactory.set(builder.pageFactory);
    }

    private void initModules(FXDesktopBuilder builder) {
        Set<DesktopModule> uniqueModules = new HashSet<>();
        if (Objects.nonNull(builder.modules)) {
            LOGGER.trace("Builder received {} modules", builder.modules.length);
            FXDesktopUtil.logListContents(Arrays.asList(builder.modules), LOGGER);
            uniqueModules.addAll(Arrays.asList(builder.modules));
            LOGGER.trace("Adding {} unique modules", uniqueModules.size());
            FXDesktopUtil.logListContents(Arrays.asList(uniqueModules.toArray()), LOGGER);
        }
        this.modules.addAll(uniqueModules);
    }

    private void resetModuleCloseable(DesktopModule module) {
        LOGGER.trace("moduleCloseable - Cleared future: " + this);
        CompletableFuture<Boolean> moduleCloseable = new CompletableFuture<>();
        moduleCloseableMap.put(module, moduleCloseable);
        LOGGER.trace("moduleCloseable - thenRun set: " + this);
        moduleCloseable.thenRun(() -> {
            LOGGER.trace("moduleCloseable -  thenRun triggered: " + this);
            closeModule(module);
        });
    }

    public void closeModuleAndWindow(InternalWindow internalWindow) {
        internalWindow.closeWindow();
        closeModule(internalWindow.getModule());
    }

    public void closeModuleAndWindow(String id) {
        internalWindows
                .stream()
                .filter(window -> window.getId().equals(convertToId("window-" + id)))
                .findFirst().
                ifPresent(this::closeModuleAndWindow);
    }

    public final boolean closeModule(DesktopModule module) {
        LOGGER.trace("closeModule - " + module);
        LOGGER.trace("closeModule - List of open modules: " + openModules);
        Objects.requireNonNull(module);
        int i = openModules.indexOf(module);
        if (i == -1) {
            throw new IllegalArgumentException("Module has not been opened yet.");
        }
        // set new active module
        DesktopModule oldActive = getActiveModule();
        DesktopModule newActive;
        if (oldActive != module) {
            // if we are not closing the currently active module, stay at the current
            newActive = oldActive;
        } else if (openModules.size() == 1) {
            // go to home screen
            newActive = null;
            LOGGER.trace("closeModule - Next active: Home Screen");
        } else if (i == 0) {
            // multiple modules open, leftmost is active
            newActive = openModules.get(i + 1);
            LOGGER.trace("closeModule - Next active: Next Module - " + newActive);
        } else {
            newActive = openModules.get(i - 1);
            LOGGER.trace("closeModule - Next active: Previous Module - " + newActive);
        }
        // if the currently active module is the one that is being closed, deactivate first
        if (oldActive == module) {
            LOGGER.trace("closeModule - " + module + " was deactivated");
            module.deactivate();
        }
    /*
      If module has previously been closed and can now safely be closed, calling destroy() is not
      necessary anymore, simply remove the module
      If this module is being closed the first time or cannot be safely closed yet, attempt to
      destroy module.
      Note: destroy() will not be called if moduleCloseable was completed with true!
     */
        if (getModuleCloseable(module).getNow(false) || module.destroy()) {
            LOGGER.trace("closeModule - Destroy: Success - " + module);
            boolean removal = openModules.remove(module);
            moduleCloseableMap.remove(module);
            LOGGER.trace("closeModule - Destroy, Removal successful: " + removal + " - " + module);
            if (oldActive != newActive) {
                // only log if the active module has been changed
                LOGGER.trace("closeModule - Set active module to: " + newActive);
            }
            activeModule.setValue(newActive);
            return removal;
        } else {
      /*
        If moduleCloseable wasn't completed yet but closeModule was called, there are two cases:
        1. The stage is calling closeModule() => since thenRun will be set on moduleCloseable after
           closeModule() returns "false", we need to reset moduleCloseable so that repeating stage
           closes without completing moduleClosable won't lead to multiple thenRun actions being
           layered with each stage close.
        2. The tab is being closed, calling closeModule() => if there was a stage close beforehand
           (and thus a thenRun from the stage closing process is still active) we need to
           reset moduleCloseable so that the stage closing process will not be triggered again.
       */
            resetModuleCloseable(module);
            // module should or could not be destroyed
            LOGGER.trace("closeModule - Destroy: Fail - " + module);
            // if the module that has failed to be destroyed is already open, activate it again
            if (getActiveModule() == module) {
                module.activate();
            }
            openModule(module); // set focus to new module
            return false;
        }
    }

    private CompletableFuture<Boolean> getModuleCloseable(DesktopModule module) {
        return moduleCloseableMap.get(module);
    }

    public void openModule(DesktopModule module) {
        if (!modules.contains(module)) {
            throw new IllegalArgumentException(
                    "Module has not been loaded yet");
        }

        LOGGER.trace("openModule - set active module to " + module);
        activeModule.setValue(module);

    }

    /**
     * Utility method to get Builder of FXDesktop
     *
     * @param modules
     * @return
     */
    public static FXDesktopBuilder builder(DesktopModule... modules) {
        return new FXDesktopBuilder(modules);
    }

    public void showWindow(DesktopModule newModule) {
        LOGGER.trace("Finding internalWindow");
        Optional<InternalWindow> first = internalWindows.stream().filter(internalWindow -> internalWindow.getId().equals(convertToId("window-" + newModule.getName()))).findFirst();
        if (first.isPresent()) {
            InternalWindow internalWindow = first.get();
            LOGGER.trace("Found internalWindow {}", internalWindow);
            if (internalWindow.isMinimized()) {
                addModuleVisibleProperty().set(false);
                internalWindow.maximizeOrRestoreWindow();
            } else if (internalWindow.isShowing() && !internalWindow.isActive()) {
                addModuleVisibleProperty().set(false);
                internalWindow.toFront();
            } else if (internalWindow.isActive()) {
                if (isAddModuleVisible())
                    addModuleVisibleProperty().set(false);
                else
                    internalWindow.minimizeWindow();
            } else {
                LOGGER.trace("Window is closed");
            }

        }
    }

    public void setWallpaper(Background background) {
        wallpaper.setValue(background);
    }

    public Background getWallpaper() {
        return wallpaper.get();
    }

    public ObjectProperty<Background> wallpaperProperty() {
        return wallpaper;
    }


    /**
     * Builder
     */
    public static final class FXDesktopBuilder {

        private final DesktopModule[] modules;
        private int modulesPerPage = DEFAULT_MODULES_PER_PAGE;
        private Callback<FXDesktop, Tab> tabFactory = DEFAULT_TAB_FACTORY;

        private Callback<FXDesktop, Tile> tileFactory = DEFAULT_TILE_FACTORY;

        private Callback<FXDesktop, Page> pageFactory = DEFAULT_PAGE_FACTORY;

        FXDesktopBuilder(DesktopModule... modules) {
            this.modules = modules;
        }

        public final FXDesktopBuilder tabFactory(Callback<FXDesktop, Tab> tabFactory) {
            this.tabFactory = tabFactory;
            return this;
        }


        public final FXDesktopBuilder tileFactory(Callback<FXDesktop, Tile> tileFactory) {
            this.tileFactory = tileFactory;
            return this;
        }


        public final FXDesktopBuilder pageFactory(Callback<FXDesktop, Page> pageFactory) {
            this.pageFactory = pageFactory;
            return this;
        }

        public final FXDesktop build() {
            return new FXDesktop(this);
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new FXDesktopSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return FXDesktopUtil.getStyleSheet("fx-desktop.css");
    }

    //accessors
    public boolean isAddModuleVisible() {
        return addModuleVisible.get();
    }

    public BooleanProperty addModuleVisibleProperty() {
        return addModuleVisible;
    }

    public Callback<FXDesktop, Tab> getTabFactory() {
        return tabFactory.get();
    }

    public Callback<FXDesktop, Page> getPageFactory() {
        return pageFactory.get();
    }

    public Callback<FXDesktop, Tile> getTileFactory() {
        return tileFactory.get();
    }

    public ObservableList<DesktopModule> getModules() {
        return FXCollections.unmodifiableObservableList(modules.get());
    }

    public ListProperty<DesktopModule> modulesProperty() {
        return modules;
    }

    public void setModules(ObservableList<DesktopModule> modules) {
        this.modules.set(modules);
    }

    public ObservableList<DesktopModule> getOpenModules() {
        return openModules.get();
    }

    public ListProperty<DesktopModule> openModulesProperty() {
        return openModules;
    }

    public int getModulesPerPage() {
        return modulesPerPage.get();
    }

    public IntegerProperty modulesPerPageProperty() {
        return modulesPerPage;
    }

    public final int getAmountOfPages() {
        return amountOfPages.get();
    }

    public final ReadOnlyIntegerProperty amountOfPagesProperty() {
        return amountOfPages;
    }

    public DesktopModule getActiveModule() {
        return activeModule.get();
    }

    public ObjectProperty<DesktopModule> activeModuleProperty() {
        return activeModule;
    }

    public void setActiveModule(DesktopModule activeModule) {
        this.activeModule.set(activeModule);
    }

    public final Node getActiveModuleView() {
        return activeModuleView.get();
    }

    public final ReadOnlyObjectProperty<Node> activeModuleViewProperty() {
        return activeModuleView;
    }

    public ObservableList<InternalWindow> getInternalWindows() {
        return internalWindows.get();
    }

    public ListProperty<InternalWindow> internalWindowsProperty() {
        return internalWindows;
    }

    /**
     * Calculates the amount of pages of modules (rendered as tiles).
     *
     * @return amount of pages
     * @implNote Each page is filled up until there are as many tiles as {@code modulesPerPage}.
     * This is repeated until all modules are rendered as tiles.
     */
    private int calculateAmountOfPages() {
        int amountOfModules = getModules().size();
        int modulesPerPage = getModulesPerPage();
        // if all pages are completely full
        if (amountOfModules % modulesPerPage == 0) {
            return amountOfModules / modulesPerPage;
        } else {
            // if the last page is not full, round up to the next page
            return amountOfModules / modulesPerPage + 1;
        }
    }
}
