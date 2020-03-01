package np.playground.desktopapp.core.view.controls.pagination;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CustomPaginationSkin extends SkinBase<CustomPagination> {
    private HBox baseLayout;
    private StackPane paneStack;
    private StackPane currentPane;
    private StackPane nextPane;

    private NavigationControl navigationControl;

    public CustomPaginationSkin(CustomPagination pagination) {
        super(pagination);
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        currentPane = new StackPane();
        nextPane = new StackPane();
        nextPane.setVisible(false);
        paneStack = new StackPane(nextPane, currentPane);

        navigationControl = new NavigationControl();
        baseLayout = new HBox(paneStack, navigationControl);
    }

    private void layoutComponents() {
        HBox.setHgrow(paneStack, Priority.ALWAYS);
        getChildren().add(baseLayout);
    }

    private class NavigationControl extends StackPane {
        private VBox mainContainer;
        private Button topScollButton;
        private Button downScollButton;
        private VBox pageNumbers;

        public NavigationControl() {
            this.initializeComponents();
            this.layoutComponents();
        }

        private void initializeComponents() {
            pageNumbers = new VBox();
            topScollButton = new Button("A");
            downScollButton = new Button("V");
            mainContainer = new VBox(topScollButton, pageNumbers, downScollButton);
            mainContainer.setSpacing(3);
            pageNumbers.setSpacing(2);
            pageNumbers.getChildren().addAll(
                    new Button("1"),
                    new Button("2"),
                    new Button("3"),
                    new Button("4"),
                    new Button("5"),
                    new Button("6"),
                    new Button("7"),
                    new Button("8"),
                    new Button("9"),
                    new Button("10"),
                    new Button("11"),
                    new Button("12"),
                    new Button("13"),
                    new Button("14"),
                    new Button("15")
            );
        }

        private void layoutComponents() {
            setAlignment(Pos.CENTER_RIGHT);
            VBox.setVgrow(pageNumbers, Priority.ALWAYS);
            getChildren().add(mainContainer);
        }
    }
}
