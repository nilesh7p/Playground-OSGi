package patels.tools.desktop;

import javafx.scene.control.SkinBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.view.*;

public class FXDesktopSkin extends SkinBase<FXDesktop> {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private TaskBarView taskBarView;
    private TaskBarPresenter taskBarPresenter;

    private AddModuleView addModuleView;
    private AddModulePresenter addModulePresenter;

    private FXDesktopView fxDesktopView;
    private FXDesktopPresenter fxDesktopPresenter;


    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected FXDesktopSkin(FXDesktop control) {
        super(control);
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        taskBarView = new TaskBarView();
        taskBarPresenter = new TaskBarPresenter(getSkinnable(), taskBarView);

        addModuleView = new AddModuleView();
        addModulePresenter = new AddModulePresenter(getSkinnable(), addModuleView);

        fxDesktopView = new FXDesktopView(taskBarView, addModuleView);
        fxDesktopPresenter = new FXDesktopPresenter(getSkinnable(), fxDesktopView);
    }

    private void layoutComponents() {
        getChildren().add(fxDesktopView);
    }

    public TaskBarView getTaskBarView() {
        return taskBarView;
    }

    public TaskBarPresenter getTaskBarPresenter() {
        return taskBarPresenter;
    }

    public AddModuleView getAddModuleView() {
        return addModuleView;
    }

    public AddModulePresenter getAddModulePresenter() {
        return addModulePresenter;
    }

    public FXDesktopView getFxDesktopView() {
        return fxDesktopView;
    }

    public FXDesktopPresenter getFxDesktopPresenter() {
        return fxDesktopPresenter;
    }
}
