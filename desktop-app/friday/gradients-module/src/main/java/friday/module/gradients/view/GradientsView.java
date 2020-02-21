package friday.module.gradients.view;

import friday.module.gradients.GradientsModule;
import friday.module.gradients.view.control.GradientsPage;
import javafx.geometry.Pos;
import javafx.scene.control.Pagination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.view.View;

public class GradientsView extends StackPane implements View {
    private final GradientsModule gradientsModule;
    Pagination pagination;
    Logger logger = LoggerFactory.getLogger(getClass());

    public GradientsView(GradientsModule gradientsModule) {
        this.gradientsModule = gradientsModule;
        init();
    }

    /**
     * Initializes the view.
     */
    @Override
    public void initializeSelf() {
        setId("gradients-view");
        setStyle("-fx-background-color: transparent;");
        setAlignment(Pos.CENTER);
        addStylesheetFiles("/css/gradients.css");
    }


    /**
     * Initializes all parts of the view.
     */
    @Override
    public void initializeComponents() {
        pagination = new Pagination();
        pagination.setPageFactory(param -> {
            logger.trace("Create new page for index {}", param);
            return new GradientsPage(gradientsModule, param);
        });
    }

    /**
     * Defines the layout of all parts in the view.
     */
    @Override
    public void layoutComponents() {
        getChildren().add(pagination);
        pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
    }


}

