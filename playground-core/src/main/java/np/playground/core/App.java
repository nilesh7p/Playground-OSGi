package np.playground.core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import np.playground.core.util.Decorator;

@SuppressWarnings("all")
public abstract class App extends Application {


    private Stage primaryStage;

    protected abstract Parent getView(Stage primaryStage);

    public abstract String getAppName();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(new Decorator(primaryStage, getView(primaryStage)), 800, 600));
        primaryStage.getScene().getStylesheets()
                .add(App.class.getResource("/scrollbar.css").toExternalForm());

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        Platform.runLater(() -> primaryStage.close());
    }
}
