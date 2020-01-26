package np.playground.testapp2;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import np.playground.core.App;

public class TestApp extends App {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(defaultScene());
        primaryStage.show();
    }

    private Scene defaultScene() {
        return new Scene(
                new BorderPane() {{
                    setCenter(new Button("Default Scene"));
                }}
        );
    }
}
