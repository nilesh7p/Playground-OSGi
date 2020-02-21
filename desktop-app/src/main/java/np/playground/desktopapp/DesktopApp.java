package np.playground.desktopapp;

import javafx.scene.Parent;
import javafx.stage.Stage;
import np.playground.core.App;

public class DesktopApp extends App {

    @Override
    protected Parent getView(Stage primaryStage) {
        return null;
    }

    @Override
    public String getAppName() {
        return "Desktop App";
    }
}
