package patels.tools.friday.launcher;

import friday.module.gradients.GradientsModule;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.scenicview.ScenicView;
import patels.tools.desktop.FXDesktop;
import patels.tools.friday.modules.TestModule;
import patels.tools.friday.modules.TestModule2;

public class Launcher extends Application {
    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXDesktop desktop = buildDesktop();
        Scene scene = new Scene(desktop, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.F11)) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
        ScenicView.show(scene);
    }

    private FXDesktop buildDesktop() {
        return FXDesktop.builder(
                new TestModule(),
                /*new TestModule2(), new TestModule(),
                new TestModule2(), new TestModule(),
                new TestModule2(), new TestModule(),
                new TestModule2(), new TestModule(),
                new TestModule2(), new TestModule(),
                new TestModule2(), new TestModule(),
                new TestModule2(), new TestModule(),
                new TestModule2(), new TestModule(),*/
                new TestModule2(),
                new GradientsModule()
        ).build();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
