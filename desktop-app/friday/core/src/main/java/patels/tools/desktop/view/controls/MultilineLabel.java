package patels.tools.desktop.view.controls;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MultilineLabel extends Label {

    private final String message;

    /**
     * Creates a label whose content is wrapping around when it's text is too long.
     *
     * @param message of the {@link MultilineLabel}
     */
    public MultilineLabel(String message) {
        this.message = message;
        setText(message);
        setWrapText(true); // Makes sure long text doesn't get cut off at the end of the label
        VBox.setVgrow(this, Priority.ALWAYS); // Makes sure long text can grow in the content
        HBox.setHgrow(this, Priority.ALWAYS);
        setMaxWidth(Double.MAX_VALUE);
    }

    public final String getMessage() {
        return message;
    }
}
