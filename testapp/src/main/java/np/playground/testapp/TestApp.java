package np.playground.testapp;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import np.playground.core.App;
import org.osgi.framework.BundleContext;

public class TestApp extends App {
    private final String appName;

    public TestApp(BundleContext bundleContext) {
        this.appName = bundleContext.getBundle().getSymbolicName();
    }

    @Override
    protected Parent getView(Stage primaryStage) {
        BorderPane bp = new BorderPane();
        bp.setCenter(new Button("Test App Loaded"));
        return bp;
    }

    @Override
    public String getAppName() {
        return appName;
    }
}
