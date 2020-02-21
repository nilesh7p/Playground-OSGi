package patels.tools.desktop.view.controls.module;

import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.view.controls.MultilineLabel;

public class TileSkin extends SkinBase<Tile> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Tile.class.getName());
    private VBox contentBox;
    private Label icon;
    private MultilineLabel textLbl;
    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected TileSkin(Tile control) {
        super(control);
        initializeParts();
        layoutParts();
        setupBindings();
    }
    private void initializeParts() {
        icon = new Label();
        icon.getStyleClass().add("icon");
        contentBox = new VBox();
        contentBox.getStyleClass().add("tile-box");
        textLbl = new MultilineLabel(getSkinnable().getName());
        textLbl.getStyleClass().add("text-lbl");
    }

    private void layoutParts() {
        contentBox.getChildren().addAll(icon, textLbl);
        getChildren().add(contentBox);
    }

    private void setupBindings() {
        icon.graphicProperty().bind(getSkinnable().iconProperty());
        textLbl.textProperty().bind(getSkinnable().nameProperty());
    }
}
