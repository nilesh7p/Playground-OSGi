package np.playground.desktopapp.core.view;

import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import np.playground.desktopapp.core.FXDesktop;
import np.playground.desktopapp.core.view.controls.taskbar.TaskBarTabCell;

import java.util.Objects;

public class TaskBarPresenter extends AbstractPresenter<TaskBarView> {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Constructor for the AbstractPresenter
     *
     * @param desktop : reference of Jarvis {@link FXDesktop}
     * @param view    :    the view for this presenter {@link View}
     */
    public TaskBarPresenter(FXDesktop desktop, TaskBarView view) {
        super(desktop, view);
    }

    /**
     * Sets up event handlers of the view.
     */
    @Override
    public void setupEventHandlers() {
        view.getMenuButton().setOnAction(this::toggleAddModuleView);

    }

    private void toggleAddModuleView(ActionEvent actionEvent) {
        desktop.addModuleVisibleProperty().set(!desktop.isAddModuleVisible());
    }

    /**
     * Adds all listeners to view elements and model properties.
     */
    @Override
    public void setupValueChangedListeners() {
        view.getTaskBar().selectedItemProperty().addListener((observable, oldModule, newModule) -> {
            LOGGER.trace("Taskbar selected item listener old {} new {}",oldModule,newModule);
            if (!Objects.isNull(newModule)) {
                desktop.showWindow(newModule);
            }
        });
    }

    /**
     * Sets up bindings of the view.
     */
    @Override
    public void setupBindings() {
        view.getTaskBar().setCellFactory(param -> new TaskBarTabCell());
        view.getTaskBar().itemsProperty().bindContent(desktop.getOpenModules());
    }
}
