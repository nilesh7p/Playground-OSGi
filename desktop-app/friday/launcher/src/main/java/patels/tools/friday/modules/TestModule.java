package patels.tools.friday.modules;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import patels.tools.desktop.model.DesktopModule;

public class TestModule extends DesktopModule {
    public TestModule() {
        super("Test Module",MaterialDesign.MDI_ALARM_OFF);
    }

    /**
     * Gets called whenever the currently displayed content is being switched to this module.
     *
     * @return content to be displayed in this module
     * @implNote if a module is being opened from the overview for the first time, it will get
     * initialized first by calling init(), afterwards activate() will be called.
     */
    @Override
    public Node activate() {
        StackPane test = new StackPane();
        //test.setStyle("-fx-background-color: rgba(255,103,0,0.78);");
        test.setBackground(new Background(new BackgroundFill(Color.valueOf("rgba(255,103,0,0.78)"), CornerRadii.EMPTY, Insets.EMPTY)));

        return test;
    }
}
