package np.playground.core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class App extends Application {

    ObjectProperty<Runnable> onCloseHandler = new SimpleObjectProperty<>(() -> System.out.println("Default Stage Closing Handler. Doing nothing. "));
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(getView(primaryStage)));
        primaryStage.setOnCloseRequest(event -> Platform.runLater(onCloseHandler.get()));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        Platform.runLater(onCloseHandler.get());
    }

    protected abstract Parent getView(Stage primaryStage);

    public Runnable getOnCloseHandler() {
        return onCloseHandler.get();
    }

    public ObjectProperty<Runnable> onCloseHandlerProperty() {
        return onCloseHandler;
    }

    public void setOnCloseHandler(Runnable onCloseHandler) {
        this.onCloseHandler.set(onCloseHandler);
    }
}
