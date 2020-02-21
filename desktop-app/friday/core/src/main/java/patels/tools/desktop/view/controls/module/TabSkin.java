package patels.tools.desktop.view.controls.module;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabSkin extends SkinBase<Tab> {

    private static final Logger logger = LoggerFactory.getLogger(TabSkin.class);
    private HBox controlBox;
    private StackPane closeIconShape;
    private Button closeBtn;

    private Label nameLbl;

    private final ReadOnlyStringProperty name;
    private final ReadOnlyObjectProperty<Node> icon;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected TabSkin(Tab control) {
        super(control);
        this.name = control.nameProperty();
        this.icon = control.iconProperty();
        initializeParts();
        layoutParts();
        setupBindings();
        setupEventHandlers();
        setupValueChangedListeners();

        updateIcon();

        getChildren().add(controlBox);
    }

    private void initializeParts() {
        closeIconShape = new StackPane();
        closeIconShape.getStyleClass().add("shape");
        closeBtn = new Button("", closeIconShape);
        closeBtn.getStyleClass().addAll("icon", "close-icon");

        nameLbl = new Label();
        nameLbl.getStyleClass().add("tab-name-lbl");

        controlBox = new HBox();
        controlBox.getStyleClass().add("tab-box");
    }

    private void layoutParts() {
        Label iconPlaceholder = new Label(); // Will be replaced in the listener
        controlBox.getChildren().addAll(iconPlaceholder, nameLbl, closeBtn);
    }

    private void setupBindings() {
        nameLbl.textProperty().bind(name);
    }

    private void setupEventHandlers() {
        closeBtn.setOnAction(e -> getSkinnable().close());
    }

    private void setupValueChangedListeners() {
        // handle icon changes
        icon.addListener((observable, oldIcon, newIcon) -> {
            if (oldIcon != newIcon) {
                updateIcon();
            }
        });
    }

    /**
     * Replaces the Icon when calling setModule().
     */
    private void updateIcon() {
        Node iconNode = icon.get();
        ObservableList<Node> children = controlBox.getChildren();
        children.remove(0);
        children.add(0, iconNode);
        iconNode.getStyleClass().add("tab-icon");
    }
}
