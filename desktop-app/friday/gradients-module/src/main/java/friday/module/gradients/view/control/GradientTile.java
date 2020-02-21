package friday.module.gradients.view.control;

import friday.module.gradients.view.LinearGradient;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;

public class GradientTile extends Control {
    private StringProperty name;
    private StringProperty css;
    private ObjectProperty<Paint> gradient;

    public GradientTile(LinearGradient gradEnum) {
        this.name = new SimpleStringProperty(gradEnum.name());
        this.css = new SimpleStringProperty(gradEnum.getGrad());
        this.gradient = new SimpleObjectProperty<>(gradEnum.get());
        setupEventHandlers();
        getStyleClass().add("tile-control");
    }

    private void setupEventHandlers() {
        setOnMouseClicked(this::copyToClipBoard);
    }

    private void copyToClipBoard(MouseEvent mouseEvent) {
        Clipboard systemClipboard = Clipboard.getSystemClipboard();
        systemClipboard.clear();
        final ClipboardContent content = new ClipboardContent();
        content.putString(css.get());
        systemClipboard.setContent(content);
    }

    private void setupListeners() {

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

    public String getCss() {
        return css.get();
    }

    public StringProperty cssProperty() {
        return css;
    }

    public void setCss(String css) {
        this.css.set(css);
    }

    public Paint getGradient() {
        return gradient.get();
    }

    public ObjectProperty<Paint> gradientProperty() {
        return gradient;
    }

    public void setGradient(Paint gradient) {
        this.gradient.set(gradient);
    }
    @Override
    protected Skin<?> createDefaultSkin() {
        return new GradientTileSkin(this);
    }
}
