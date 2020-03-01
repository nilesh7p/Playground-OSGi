package np.playground.desktopapp.core.view;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import np.playground.desktopapp.core.FXDesktop;

import java.util.Objects;

public class FXDesktopPresenter extends AbstractPresenter<FXDesktopView> {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Constructor for the AbstractPresenter
     *
     * @param desktop : reference of Jarvis {@link FXDesktop}
     * @param view    :    the view for this presenter {@link View}
     */
    public FXDesktopPresenter(FXDesktop desktop, FXDesktopView view) {
        super(desktop, view);
    }

    @Override
    public void initializeViewParts() {
        view.getAddModuleOverlay().getGlassPane().setMouseTransparent(false);
    }

    /**
     * Sets up event handlers of the view.
     */
    @Override
    public void setupEventHandlers() {
        view.getAddModuleOverlay().getGlassPane().setOnMouseClicked(event -> {
            LOGGER.trace("AddModuleOverlay GlassPane MouseClicked - {}", event);
            desktop.addModuleVisibleProperty().set(false);
        });
        view.getTaskBarView().getTileButton().setOnAction(this::tileWindows);
        view.getTaskBarView().getGlassButton().setOnAction(this::hideWindows);
    }

    private void hideWindows(ActionEvent actionEvent) {
        view.minimizeAllWindows();
        desktop.addModuleVisibleProperty().set(false);
        desktop.setWallpaper(FXDesktop.DEFAULT_WALLPAPER);
    }

    private void tileWindows(ActionEvent actionEvent) {
        desktop.addModuleVisibleProperty().set(false);
        view.tileAllWindows();
    }

    /**
     * Adds all listeners to view elements and model properties.
     */
    @Override
    public void setupValueChangedListeners() {

        desktop.activeModuleProperty().addListener((observable, oldValue, newValue) -> {
            LOGGER.trace("Active Module Listener - Display module - " + newValue);
            Node activeModuleView = desktop.getActiveModuleView();
            if (Objects.nonNull(activeModuleView)) view.show(newValue, activeModuleView);
        });
        desktop.activeModuleViewProperty().addListener((observable, oldValue, newValue) -> {
            LOGGER.trace("Active Module View Listener - Activate View - " + newValue);
        });

        desktop.addModuleVisibleProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) {
                view.getInternalStackPane().getChildren().addAll(view.getAddModuleOverlay().getGlassPane(), view.getAddModuleOverlay().getOverlay());
                view.getAddModuleOverlay().getOverlay().setVisible(true);
            } else {
                view.getInternalStackPane().getChildren().removeAll(view.getAddModuleOverlay().getGlassPane(), view.getAddModuleOverlay().getOverlay());
            }
        });

        desktop.wallpaperProperty().addListener((observable, oldValue, newValue) -> {
            view.setBackground(newValue);
        });
    }

    /**
     * Sets up bindings of the view.
     */
    @Override
    public void setupBindings() {
        desktop.internalWindowsProperty().bindBidirectional(view.internalWindowsProperty());
    }
}
