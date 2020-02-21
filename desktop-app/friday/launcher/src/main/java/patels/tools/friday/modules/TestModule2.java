package patels.tools.friday.modules;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import patels.tools.desktop.model.DesktopModule;

public class TestModule2 extends DesktopModule {
    public TestModule2() {
        super("Test Module2", MaterialDesign.MDI_ALARM_CHECK);
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
        return new StackPane(new Button("Test"));
    }
}
