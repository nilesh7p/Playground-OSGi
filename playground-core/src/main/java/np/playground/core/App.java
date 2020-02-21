package np.playground.core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import np.playground.core.util.Decorator;

import static np.playground.core.util.PlaygroundUtil.toFXImage;

@SuppressWarnings("all")
public abstract class App extends Application {

    private static final String DEFAULT_ICON = "data:image/png;base64,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";
    private Stage primaryStage;

    protected abstract Parent getView(Stage primaryStage);

    public abstract String getAppName();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Decorator decorator = new Decorator(primaryStage, getView(primaryStage));
        decorator.setGraphic(new ImageView(getIcon()));

        Scene scene = new Scene(decorator, 800, 600);
        scene.getStylesheets().add(App.class.getResource("/scrollbar.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.getIcons().add(getIcon());
        primaryStage.setTitle(getAppName());
        primaryStage.show();
    }

    public Image getIcon() {
        return toFXImage(DEFAULT_ICON);
    }

    @Override
    public void stop() throws Exception {
        Platform.runLater(() -> primaryStage.close());
    }
}
