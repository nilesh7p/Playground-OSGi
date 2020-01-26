package np.playground.testapp;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import np.playground.core.App;

public class TestApp extends App {

    @Override
    protected Parent getView(Stage primaryStage) {
        return new BorderPane() {{
            setCenter(new Button("Default Scene"));
        }};
    }

}
