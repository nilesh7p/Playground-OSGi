package patels.tools.desktop.view;

import javafx.scene.control.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddModuleView extends Pagination implements View {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public AddModuleView() {
        init();
    }

    /**
     * Initializes the view.
     */
    @Override
    public void initializeSelf() {
        setId("add-module-view");
    }

    /**
     * Initializes all parts of the view.
     */
    @Override
    public void initializeComponents() {

    }

    /**
     * Defines the layout of all parts in the view.
     */
    @Override
    public void layoutComponents() {
        getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
    }
}
