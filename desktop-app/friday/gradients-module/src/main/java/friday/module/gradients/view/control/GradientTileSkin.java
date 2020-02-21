package friday.module.gradients.view.control;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradientTileSkin extends SkinBase<GradientTile> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradientTileSkin.class.getName());
    private VBox contentBox;
    private StackPane gradientView;


    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GradientTileSkin(GradientTile control) {
        super(control);
        initializeParts();
        layoutParts();
        setupBindings();
    }

    private void initializeParts() {
        contentBox = new VBox();
        contentBox.getStyleClass().add("tile-box");
        gradientView = new StackPane();
        gradientView.setBackground(new Background(new BackgroundFill(getSkinnable().getGradient(), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void setSize(VBox contentBox, int width, int height) {
        contentBox.setPrefSize(width,height);
        contentBox.setMaxSize(width,height);
        contentBox.setMinSize(width,height);
    }

    private void layoutParts() {
        contentBox.getChildren().add(gradientView);
        VBox.setVgrow(gradientView,Priority.ALWAYS);
        getChildren().add(contentBox);
    }

    private void setupBindings() {
        /*getSkinnable().gradientProperty().addListener((observable, oldValue, newValue) ->
                gradientView.setBackground(new Background(new BackgroundFill(newValue, CornerRadii.EMPTY, Insets.EMPTY))));*/
    }
}
