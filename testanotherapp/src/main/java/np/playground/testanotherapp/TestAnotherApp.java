package np.playground.testanotherapp;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import np.playground.core.App;
import org.osgi.framework.BundleContext;

public class TestAnotherApp extends App {
    private final String appName;
    public TestAnotherApp(BundleContext bundleContext) {
        super();
        this.appName = bundleContext.getBundle().getSymbolicName();
    }

    @Override
    protected Parent getView(Stage primaryStage) {
        BorderPane bp = new BorderPane();
        bp.setCenter(new Button("Test another app Loaded"));
        return bp;
    }

    @Override
    public String getAppName() {
        return appName;
    }
}
