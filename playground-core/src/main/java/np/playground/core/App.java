package np.playground.core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class App extends Application {


    protected abstract Parent getView(Stage primaryStage);

    public abstract String getAppName();
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(getView(primaryStage), 800, 600));
        primaryStage.show();
    }
}
