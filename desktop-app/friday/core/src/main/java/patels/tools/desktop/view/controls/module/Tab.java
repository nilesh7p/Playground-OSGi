package patels.tools.desktop.view.controls.module;


import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.FXDesktop;
import patels.tools.desktop.model.DesktopModule;

import static patels.tools.desktop.utils.FXDesktopUtil.convertToId;

public class Tab extends Control {
    private static final Logger logger = LoggerFactory.getLogger(Tab.class);
    private final FXDesktop fxDesktop;
    private final ObjectProperty<DesktopModule> module;
    private final StringProperty name;
    private final ObjectProperty<Node> icon;
    private final BooleanProperty activeTab;
    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");

    public Tab(FXDesktop fxDesktop) {
        this.fxDesktop = fxDesktop;
        module = new SimpleObjectProperty<>(this, "module");
        name = new SimpleStringProperty(this, "name");
        icon = new SimpleObjectProperty<>(this, "icon");
        activeTab = new SimpleBooleanProperty(this, "activeTab");
        setupModuleListeners();
        setupActiveTabListener();
        setupEventHandlers();
        getStyleClass().add("tab-control");
    }

    private void setupModuleListeners() {
        module.addListener(observable -> {
            DesktopModule current = getModule();
            // Replace any occurence of \n with space
            name.setValue(current.getName().replace("\n", " "));
            icon.setValue(current.getIcon());

            // Sets the id with toString of module.
            // Adds 'tab-', replaces spaces with hyphens and sets letters to lowercase.
            // eg. Customer Management converts to tab-customer-management
            String tabId = convertToId("tab-" + current.getName());
            logger.debug("Set Tab-ID of '" + getModule() + "' to: '" + tabId + "'");
            setId(tabId);
        });
    }

    private void setupActiveTabListener() {
// whenever the module of this tab changes, re-initialize the binding which determines whether
        // this tab is the currently active tab or not
        moduleProperty().addListener(observable -> {
            activeTab.unbind();
            activeTab.bind(Bindings.equal(getModule(), fxDesktop.activeModuleProperty()));
        });
        activeTab.addListener((observable, oldValue, newValue) ->
                pseudoClassStateChanged(SELECTED, newValue)
        );
    }

    private void setupEventHandlers() {
        setOnMouseClicked(e -> open());
    }

    public FXDesktop getFxDesktop() {
        return fxDesktop;
    }

    public DesktopModule getModule() {
        return module.get();
    }

    public ObjectProperty<DesktopModule> moduleProperty() {
        return module;
    }

    public void setModule(DesktopModule module) {
        this.module.set(module);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Node getIcon() {
        return icon.get();
    }

    public ObjectProperty<Node> iconProperty() {
        return icon;
    }

    public void setIcon(Node icon) {
        this.icon.set(icon);
    }

    public boolean isActiveTab() {
        return activeTab.get();
    }

    public BooleanProperty activeTabProperty() {
        return activeTab;
    }

    public void setActiveTab(boolean activeTab) {
        this.activeTab.set(activeTab);
    }

    public static PseudoClass getSELECTED() {
        return SELECTED;
    }

    /**
     * Closes the {@link DesktopModule} along with this {@link Tab}.
     */
    public final void close() {
        fxDesktop.closeModuleAndWindow(getModule().getName());
    }

    /**
     * Opens the {@link DesktopModule} belonging to this {@link Tab}.
     */
    public final void open() {
        fxDesktop.showWindow(getModule());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TabSkin(this);
    }
}
