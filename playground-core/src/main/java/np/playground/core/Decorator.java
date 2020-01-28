/*
package np.playground.core;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Decorator extends VBox {

    Logger logger = LoggerFactory.getLogger(Decorator.class);
    private Stage primaryStage;
    private final Parent nodeToDecorate;

    private double xOffset = 0;
    private double yOffset = 0;
    private double initX;
    private double initY;
    private double initWidth = -1;
    private double initHeight = -1;
    private double initStageX = -1;
    private double initStageY = -1;

    private boolean allowMove = false;
    private boolean isDragging = false;
    private Timeline windowDecoratorAnimation;
    private StackPane contentPlaceHolder = new StackPane();
    private HBox buttonsContainer;
    private HBox graphicTextContainer;

    private ObjectProperty<Runnable> onCloseButtonAction = new SimpleObjectProperty<>(
            () -> primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST))
    );

    private BooleanProperty customMaximize = new SimpleBooleanProperty(false);
    private boolean maximized = false;
    private BoundingBox originalBox;

    private Button btnMax;
    private Button btnFull;
    private Button btnClose;
    private Button btnMin;

    private StringProperty title = new SimpleStringProperty();
    private Text text;
    private Text graphic;
    private HBox graphicContainer;


    // Aero Snap
    private TransparentWindow transparentWindow;
    private Delta delta = new Delta();
    private Delta eventSource = new Delta();
    private SimpleBooleanProperty snap = new SimpleBooleanProperty(true);

    private boolean snapped;
*
     * The prev size.


    private Delta prevSize;

*
     * The prev pos.


    private Delta prevPos;


    public Decorator(Stage primaryStage, Parent nodeToDecorate) {
        this.primaryStage = primaryStage;
        this.nodeToDecorate = nodeToDecorate;
        prevSize = new Delta();
        prevPos = new Delta();
        initializeSelf();
        initializeParts();
        layoutParts();
        setupBindings();
        setupHandlers();
        setupValueChangeListeners();
        setupAero();
    }


    private void initializeSelf() {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        setId("decorator");
        setPickOnBounds(false);
        setStyle("-fx-background-color: linear-gradient(to bottom right, #facb52 0.0%, #fc76b3 100%)");
        logger.info("self initialization complete");
    }

    private void initializeParts() {
        initializeButtons();
        initializeContainer();
        transparentWindow = new TransparentWindow();
        transparentWindow.getWindow().initOwner(primaryStage);
    }

    private void initializeButtons() {
FontIcon full = FontIcon.of(MaterialDesign.MDI_FULLSCREEN, Color.valueOf("rgba(40,40,40,0.7)"));
          FontIcon close = FontIcon.of(MaterialDesign.MDI_CLOSE, Color.valueOf("rgba(40,40,40,0.7)"));
        FontIcon minus = FontIcon.of(MaterialDesign.MDI_WINDOW_MINIMIZE, Color.valueOf("rgba(40,40,40,0.7)"));
        FontIcon resizeMax = FontIcon.of(MaterialDesign.MDI_WINDOW_MAXIMIZE, Color.valueOf("rgba(40,40,40,0.7)"));
        FontIcon resizeMin = FontIcon.of(MaterialDesign.MDI_WINDOW_RESTORE, Color.valueOf("rgba(40,40,40,0.7)"));

        Text full = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.FULLSCREEN);
        Text close = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.CLOSE);
        Text minus = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.WINDOW_MINIMIZE);
        Text resizeMax = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.WINDOW_MAXIMIZE);
        Text resizeMin = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.WINDOW_RESTORE);


        btnMin = createButton("btnMin", minus, () -> primaryStage.setIconified(true));
        btnFull = createButton("btnFull", full, () -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));
        btnClose = createButton("btnClose", close, () -> onCloseButtonAction.get().run());


        full.setFill(Color.valueOf("rgba(40,40,40,0.7)"));
        close.setFill(Color.valueOf("rgba(40,40,40,0.7)"));
        minus.setFill(Color.valueOf("rgba(40,40,40,0.7)"));
        resizeMax.setFill(Color.valueOf("rgba(40,40,40,0.7)"));
        resizeMin.setFill(Color.valueOf("rgba(40,40,40,0.7)"));

        btnMax = createButton("btnMax", resizeMax, () -> maximize(resizeMin, resizeMax));
        logger.info("button initialization complete");
    }

    private void maximize(Text resizeMin, Text resizeMax) {
        if (!isCustomMaximize()) {
            primaryStage.setMaximized(!primaryStage.isMaximized());
            maximized = primaryStage.isMaximized();
            if (primaryStage.isMaximized()) {
                btnMax.setGraphic(resizeMin);
                btnMax.setTooltip(new Tooltip("Restore Down"));
            } else {
                btnMax.setGraphic(resizeMax);
                btnMax.setTooltip(new Tooltip("Maximize"));
            }
        } else {
            if (!maximized) {
                // store original bounds
                originalBox = new BoundingBox(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight());
                // get the max stage bounds
                Screen screen = Screen.getScreensForRectangle(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight()).get(0);
                Rectangle2D bounds = screen.getVisualBounds();
                BoundingBox maximizedBox = new BoundingBox(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
                // maximized the stage
                primaryStage.setX(maximizedBox.getMinX());
                primaryStage.setY(maximizedBox.getMinY());
                primaryStage.setWidth(maximizedBox.getWidth());
                primaryStage.setHeight(maximizedBox.getHeight());
                btnMax.setGraphic(resizeMin);
                btnMax.setTooltip(new Tooltip("Restore Down"));
            } else {
                // restore stage to its original size
                primaryStage.setX(originalBox.getMinX());
                primaryStage.setY(originalBox.getMinY());
                primaryStage.setWidth(originalBox.getWidth());
                primaryStage.setHeight(originalBox.getHeight());
                originalBox = null;
                btnMax.setGraphic(resizeMax);
                btnMax.setTooltip(new Tooltip("Maximize"));
            }
            maximized = !maximized;
        }
    }

    private Button createButton(String id, Text graphic, Runnable handler) {
        Button b = new Button();
        b.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        b.setId(id);
        b.setGraphic(graphic);
        b.setCursor(Cursor.HAND);
        b.setStyle("-fx-background-color:transparent;");
        b.getStyleClass().add("decorator-button");
        b.setOnAction((event) -> handler.run());
        return b;
    }

    private void initializeContainer() {
        buttonsContainer = new HBox();
        buttonsContainer.getStyleClass().add("decorator-buttons-container");
        buttonsContainer.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonsContainer.setMinWidth(180);

        text = new Text();
        text.getStyleClass().addAll("decorator-text", "title", "decorator-title");
        text.setFill(Color.WHITE);

        graphicContainer = new HBox();
        graphicContainer.setPickOnBounds(false);
        graphicContainer.setAlignment(Pos.CENTER_LEFT);
        graphicContainer.getChildren().setAll(text);

        graphicTextContainer = new HBox();
        graphicTextContainer.getStyleClass().add("decorator-title-container");
        graphicTextContainer.setAlignment(Pos.CENTER_LEFT);
        graphicTextContainer.setPickOnBounds(false);

        contentPlaceHolder.getStyleClass().addAll("decorator-content-container", "resize-border");
        contentPlaceHolder.setMinSize(0, 0);
        contentPlaceHolder.setBorder(new Border(new BorderStroke(Color.valueOf("rgba(40,40,40,0.5)"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 2, 2, 2))));

        ((Region) nodeToDecorate).setMinSize(0, 0);
        logger.info("container initialization complete");
    }

    private void layoutParts() {
        graphicTextContainer.getChildren().addAll(graphicContainer, text);
        buttonsContainer.getChildren().setAll(graphicTextContainer);
        buttonsContainer.getChildren().addAll(btnFull, btnMin, btnMax, btnClose);
        contentPlaceHolder.getChildren().add(nodeToDecorate);
        HBox.setHgrow(graphicTextContainer, Priority.ALWAYS);
        HBox.setMargin(graphicContainer, new Insets(0, 8, 0, 8));
        VBox.setVgrow(contentPlaceHolder, Priority.ALWAYS);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(((Region) nodeToDecorate).widthProperty());
        clip.heightProperty().bind(((Region) nodeToDecorate).heightProperty());
        nodeToDecorate.setClip(clip);
        getChildren().addAll(buttonsContainer, contentPlaceHolder);
        logger.info("part layout complete");
    }

    private void setupBindings() {
        text.textProperty().bind(title); //binds the Text's text to title
        title.bind(primaryStage.titleProperty()); //binds title to the primaryStage's title
        logger.info("binding setup complete");
    }

    private void setupHandlers() {

        // maximize/restore the window on header double click
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
            if (mouseEvent.getClickCount() == 2) {
                btnMax.fire();
            }
        });
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, (enter) -> allowMove = true);
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_EXITED, (enter) -> {
            if (!isDragging) {
                allowMove = false;
            }
        });
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, this::updateInitMouseValues);

        contentPlaceHolder.addEventHandler(MouseEvent.MOUSE_PRESSED, this::updateInitMouseValues);

        // show the drag cursor on the borders
        addEventFilter(MouseEvent.MOUSE_MOVED, this::showDragCursorOnBorders);

        // handle drag events on the decorator pane
        addEventFilter(MouseEvent.MOUSE_RELEASED, (mouseEvent) -> isDragging = false);
        this.setOnMouseDragged(this::handleDragEventOnDecoratorPane);
        logger.info("handler setup complete");
    }

    private void setupValueChangeListeners() {
        primaryStage.fullScreenProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                // remove border
                contentPlaceHolder.getStyleClass().remove("resize-border");
                 *  note the border property MUST NOT be bound to another property
                 *  when going full screen mode, thus the binding will be lost if exisited


                contentPlaceHolder.borderProperty().unbind();
                contentPlaceHolder.setBorder(Border.EMPTY);
                if (windowDecoratorAnimation != null) {
                    windowDecoratorAnimation.stop();
                }
                windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                        new KeyValue(this.translateYProperty(), -buttonsContainer.getHeight(), Interpolator.EASE_BOTH)));

                windowDecoratorAnimation.setOnFinished((finish) -> {
                    this.getChildren().remove(buttonsContainer);
                    this.setTranslateY(0);
                });

                windowDecoratorAnimation.play();
            } else {
                // add border
                if (windowDecoratorAnimation != null) {
                    if (windowDecoratorAnimation.getStatus() == Animation.Status.RUNNING) {
                        windowDecoratorAnimation.stop();
                    } else {
                        this.getChildren().add(0, buttonsContainer);
                    }
                }
                this.setTranslateY(-buttonsContainer.getHeight());
                windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                        new KeyValue(this.translateYProperty(), 0, Interpolator.EASE_BOTH)));

                windowDecoratorAnimation.setOnFinished((finish) -> {
                    contentPlaceHolder.setBorder(new Border(new BorderStroke(Color.valueOf("rgba(40,40,40,0.5)"),
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 2, 2, 2))));
                    contentPlaceHolder.getStyleClass().add("resize-border");
                });
                windowDecoratorAnimation.play();
            }
        });
    }

    private void updateInitMouseValues(MouseEvent mouseEvent) {
        initStageX = primaryStage.getX();
        initStageY = primaryStage.getY();
        initWidth = primaryStage.getWidth();
        initHeight = primaryStage.getHeight();
        initX = mouseEvent.getScreenX();
        initY = mouseEvent.getScreenY();
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }

    private void showDragCursorOnBorders(MouseEvent mouseEvent) {
        if (primaryStage.isMaximized() || primaryStage.isFullScreen() || maximized) {
            this.setCursor(Cursor.DEFAULT);
            return; // maximized mode does not support resize
        }
        if (!primaryStage.isResizable()) {
            return;
        }
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        if (contentPlaceHolder.getBorder() != null && contentPlaceHolder.getBorder().getStrokes().size() > 0) {
            double borderWidth = contentPlaceHolder.snappedLeftInset();
            if (isRightEdge(x)) {
                if (y < borderWidth) {
                    this.setCursor(Cursor.NE_RESIZE);
                } else if (y > this.getHeight() - borderWidth) {
                    this.setCursor(Cursor.SE_RESIZE);
                } else {
                    this.setCursor(Cursor.E_RESIZE);
                }
            } else if (isLeftEdge(x)) {
                if (y < borderWidth) {
                    this.setCursor(Cursor.NW_RESIZE);
                } else if (y > this.getHeight() - borderWidth) {
                    this.setCursor(Cursor.SW_RESIZE);
                } else {
                    this.setCursor(Cursor.W_RESIZE);
                }
            } else if (isTopEdge(y)) {
                this.setCursor(Cursor.N_RESIZE);
            } else if (isBottomEdge(y)) {
                this.setCursor(Cursor.S_RESIZE);
            } else {
                this.setCursor(Cursor.DEFAULT);
            }
        }
    }

    private void handleDragEventOnDecoratorPane(MouseEvent mouseEvent) {
        isDragging = true;
        if (!mouseEvent.isPrimaryButtonDown() || (xOffset == -1 && yOffset == -1)) {
            return;
        }
         * Long press generates drag event!


        if (primaryStage.isFullScreen() || mouseEvent.isStillSincePress() || primaryStage.isMaximized() || maximized) {
            return;
        }

        double newX = mouseEvent.getScreenX();
        double newY = mouseEvent.getScreenY();


        double deltaX = newX - initX;
        double deltaY = newY - initY;
        Cursor cursor = this.getCursor();

        if (Cursor.E_RESIZE.equals(cursor)) {
            setStageWidth(initWidth + deltaX);
            mouseEvent.consume();
        } else if (Cursor.NE_RESIZE.equals(cursor)) {
            if (setStageHeight(initHeight - deltaY)) {
                primaryStage.setY(initStageY + deltaY);
            }
            setStageWidth(initWidth + deltaX);
            mouseEvent.consume();
        } else if (Cursor.SE_RESIZE.equals(cursor)) {
            setStageWidth(initWidth + deltaX);
            setStageHeight(initHeight + deltaY);
            mouseEvent.consume();
        } else if (Cursor.S_RESIZE.equals(cursor)) {
            setStageHeight(initHeight + deltaY);
            mouseEvent.consume();
        } else if (Cursor.W_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltaX)) {
                primaryStage.setX(initStageX + deltaX);
            }
            mouseEvent.consume();
        } else if (Cursor.SW_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltaX)) {
                primaryStage.setX(initStageX + deltaX);
            }
            setStageHeight(initHeight + deltaY);
            mouseEvent.consume();
        } else if (Cursor.NW_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltaX)) {
                primaryStage.setX(initStageX + deltaX);
            }
            if (setStageHeight(initHeight - deltaY)) {
                primaryStage.setY(initStageY + deltaY);
            }
            mouseEvent.consume();
        } else if (Cursor.N_RESIZE.equals(cursor)) {
            if (setStageHeight(initHeight - deltaY)) {
                primaryStage.setY(initStageY + deltaY);
            }
            mouseEvent.consume();
        } else if (allowMove) {
            primaryStage.setX(mouseEvent.getScreenX() - xOffset);
            primaryStage.setY(mouseEvent.getScreenY() - yOffset);
            mouseEvent.consume();
        }
    }

    private boolean isRightEdge(double x) {
        final double width = this.getWidth();
        return x < width && x > width - contentPlaceHolder.snappedLeftInset();
    }

    private boolean isTopEdge(double y) {
        return y >= 0 && y < contentPlaceHolder.snappedLeftInset();
    }

    private boolean isBottomEdge(double y) {
        final double height = this.getHeight();
        return y < height && y > height - contentPlaceHolder.snappedLeftInset();
    }

    private boolean isLeftEdge(double x) {
        return x >= 0 && x < contentPlaceHolder.snappedLeftInset();
    }

    private boolean setStageWidth(double width) {
        if (width >= primaryStage.getMinWidth() && width >= buttonsContainer.getMinWidth()) {
            primaryStage.setWidth(width);
            return true;
        } else if (width >= primaryStage.getMinWidth() && width <= buttonsContainer.getMinWidth()) {
            width = buttonsContainer.getMinWidth();
            primaryStage.setWidth(width);
        }
        return false;
    }

    private boolean setStageHeight(double height) {
        if (height >= primaryStage.getMinHeight() && height >= buttonsContainer.getHeight()) {
            primaryStage.setHeight(height);
            return true;
        } else if (height >= primaryStage.getMinHeight() && height <= buttonsContainer.getHeight()) {
            height = buttonsContainer.getHeight();
            primaryStage.setHeight(height);
        }
        return false;
    }

*
     * set a speficed runnable when clicking on the close button
     *
     * @param onCloseButtonAction runnable to be executed


    public void setOnCloseButtonAction(Runnable onCloseButtonAction) {
        this.onCloseButtonAction.set(onCloseButtonAction);
    }

*
     * this property is used to replace JavaFX maximization
     * with a custom one that prevents hiding windows taskbar when
     * the JFXDecorator is maximized.
     *
     * @return customMaximizeProperty whether to use custom maximization or not.


    public final BooleanProperty customMaximizeProperty() {
        return this.customMaximize;
    }

*
     * @return whether customMaximizeProperty is active or not


    public final boolean isCustomMaximize() {
        return this.customMaximizeProperty().get();
    }

*
     * set customMaximize property
     *
     * @param customMaximize


    public final void setCustomMaximize(final boolean customMaximize) {
        this.customMaximizeProperty().set(customMaximize);
    }

*
     * @param maximized


    public void setMaximized(boolean maximized) {
        if (this.maximized != maximized) {
            Platform.runLater(() -> btnMax.fire());
        }
    }

*
     * will change the decorator content
     *
     * @param content


    public void setContent(Parent content) {
        this.contentPlaceHolder.getChildren().setAll(content);
    }

*
     * will set the title
     *
     * @param text
     * @deprecated Use {@link Decorator#setTitle(java.lang.String)} instead.


    public void setText(String text) {
        setTitle(text);
    }

*
     * will get the title
     *
     * @deprecated Use {@link Decorator#setTitle(java.lang.String)} instead.


    public String getText() {
        return getTitle();
    }

    public String getTitle() {
        return title.get();
    }

*
     * By default this title property is bound to the primaryStage's title property.
     * <p>
     * To change it to something else, use <pre>
     *     {@code jfxDecorator.titleProperty().unbind();}</pre> first.


    public StringProperty titleProperty() {
        return title;
    }

*
     * If you want the {@code primaryStage}'s title and the {@code JFXDecorator}'s title to be different, then
     * go ahead and use this method.
     * <p>
     * By default, this title property is bound to the {@code primaryStage}'s title property-so merely setting the
     * {@code primaryStage}'s title, will set the {@code JFXDecorator}'s title.


    public void setTitle(String title) {
        this.title.unbind();
        this.title.set(title);
    }

    public void setGraphic(Text node) {
        if (graphic != null) {
            graphicContainer.getChildren().remove(graphic);
        }
        if (node != null) {
            graphicContainer.getChildren().add(0, node);
        }
        graphic = node;
    }

    public Text getGraphic(Text node) {
        return graphic;
    }

    public void setSnap(boolean snap) {
        this.snap.set(snap);
    }


    private class Delta {

*
         * The x.


        Double x;

*
         * The y.


        Double y;
    }

    private class TransparentWindow extends StackPane {
*
         * The logger.


        private Logger logger = LoggerFactory.getLogger(getClass().getName());

*
         * The Window


        private Stage window = new Stage();

*
         * Constructor


        public TransparentWindow() {
#4deeea	(77,238,234)
                #74ee15	(116,238,21)
                #ffe700	(255,231,0)
                #f000ff	(240,0,255)
                #001eff	(0,30,255)

            setStyle("-fx-background-color: rgba(255,231,0,0.3); -fx-border-color: #74ee15; -fx-border-width: 2;");
            //Window
            window.setTitle("Transparent Window");
            window.initStyle(StageStyle.TRANSPARENT);
            window.initModality(Modality.NONE);
            window.setScene(new Scene(this, Color.TRANSPARENT));
        }


*
         * @return the window


        public Stage getWindow() {
            return window;
        }

*
         * Close the Window


        public void close() {
            logger.info("closing transparent window");
            window.close();
        }

*
         * Show the Window


        public void show() {
            if (!window.isShowing()) {
                logger.info("showing transparent window");
                window.show();
            } else {
                logger.info("requested focus on transparent window");
                window.requestFocus();
            }
        }

    }

    private void setupAero() {
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, this::aeroMousePressed);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::aeroMouseDragged);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::aeroMouseReleased);
        logger.info("aero setup complete");
    }

    private void aeroMousePressed(MouseEvent m) {

        if (m.isPrimaryButtonDown()) {
            delta.x = m.getSceneX(); //getX()
            delta.y = m.getSceneY(); //getY()

            if (maximized || snapped) {
                delta.x = prevSize.x * (m.getSceneX() / primaryStage.getWidth());//(m.getX() / stage.getWidth())
                delta.y = prevSize.y * (m.getSceneY() / primaryStage.getHeight());//(m.getY() / stage.getHeight())
            } else {
                prevSize.x = primaryStage.getWidth();
                prevSize.y = primaryStage.getHeight();
                prevPos.x = primaryStage.getX();
                prevPos.y = primaryStage.getY();
            }

            eventSource.x = m.getScreenX();
            eventSource.y = prefHeight(primaryStage.getHeight());
        }

    }

    private void aeroMouseDragged(MouseEvent m) {
        if (m.isPrimaryButtonDown()) {

            // Move x axis.
            //primaryStage.setX(m.getScreenX() - delta.x);

            if (snapped) {
                if (m.getScreenY() > eventSource.y) {
                    snapped = false;
                }
else {
                    Rectangle2D screen = Screen.getScreensForRectangle(m.getScreenX(), m.getScreenY(), 1, 1).get(0).getVisualBounds();
                    primaryStage.setHeight(screen.getHeight());
                }

            }
else {
                // Move y axis.
                primaryStage.setY(m.getScreenY() - delta.y);
            }


            // Aero Snap off.
if (maximized) {
                primaryStage.setWidth(prevSize.x);
                primaryStage.setHeight(prevSize.y);
                setMaximized(false);
            }


            boolean toCloseWindow = false;
            if (!snap.get()) {
                toCloseWindow = true;
            } else {
                //--------------------------Check here for Transparent Window--------------------------
                Rectangle2D screen = Screen.getScreensForRectangle(m.getScreenX(), m.getScreenY(), 1, 1).get(0).getVisualBounds();
                // Aero Snap Left.
                if (m.getScreenX() <= screen.getMinX()) {
                    transparentWindow.getWindow().setY(screen.getMinY());
                    transparentWindow.getWindow().setHeight(screen.getHeight());

                    transparentWindow.getWindow().setX(screen.getMinX());
                    if (screen.getWidth() / 2 < transparentWindow.getWindow().getMinWidth()) {
                        transparentWindow.getWindow().setWidth(transparentWindow.getWindow().getMinWidth());
                    } else {
                        transparentWindow.getWindow().setWidth(screen.getWidth() / 2);
                    }
                    logger.info("snapped left");
                    transparentWindow.show();
                }

                // Aero Snap Right.
                else if (m.getScreenX() >= screen.getMaxX() - 1) {
                    transparentWindow.getWindow().setY(screen.getMinY());
                    transparentWindow.getWindow().setHeight(screen.getHeight());

                    if (screen.getWidth() / 2 < transparentWindow.getWindow().getMinWidth()) {
                        transparentWindow.getWindow().setWidth(transparentWindow.getWindow().getMinWidth());
                    } else {
                        transparentWindow.getWindow().setWidth(screen.getWidth() / 2);
                    }
                    transparentWindow.getWindow().setX(screen.getMaxX() - transparentWindow.getWindow().getWidth());
                    logger.info("snapped right");
                    transparentWindow.show();
                }

                // Aero Snap Top. || Aero Snap Bottom.
                else if (m.getScreenY() <= screen.getMinY()
|| m.getScreenY() >= screen.getMaxY() - 1
) {
                    logger.info("snapped top");
                    transparentWindow.getWindow().setX(screen.getMinX());
                    transparentWindow.getWindow().setY(screen.getMinY());
                    transparentWindow.getWindow().setWidth(screen.getWidth());
                    transparentWindow.getWindow().setHeight(screen.getHeight());

                    transparentWindow.show();
                } else {
                    toCloseWindow = true;
                }

                //				System.out.println("Mouse Position [ " + m.getScreenX() + "," + m.getScreenY() + " ]")
                //				System.out.println(" " + screen.getMinX() + "," + screen.getMinY() + " ," + screen.getMaxX() + " ," + screen.getMaxY())
                //				System.out.println()
            }

            if (toCloseWindow) {
                transparentWindow.close();
            }
        }
    }

    private void aeroMouseReleased(MouseEvent m) {
        try {
            if (!snap.get()) {
                return;
            }

            if ((MouseButton.PRIMARY.equals(m.getButton())) && (m.getScreenX() != eventSource.x)) {
                Rectangle2D screen = Screen.getScreensForRectangle(m.getScreenX(), m.getScreenY(), 1, 1).get(0).getVisualBounds();

                // Aero Snap Left.
                if (m.getScreenX() <= screen.getMinX()) {
                    logger.info("snapping stage to Left");
                    primaryStage.setY(screen.getMinY());
                    primaryStage.setHeight(screen.getHeight());

                    primaryStage.setX(screen.getMinX());
                    if (screen.getWidth() / 2 < primaryStage.getMinWidth()) {
                        primaryStage.setWidth(primaryStage.getMinWidth());
                    } else {
                        primaryStage.setWidth(screen.getWidth() / 2);
                    }

                    snapped = true;
                }

                // Aero Snap Right.
                else if (m.getScreenX() >= screen.getMaxX() - 1) {
                    logger.info("snapping stage to Right");
                    primaryStage.setY(screen.getMinY());
                    primaryStage.setHeight(screen.getHeight());

                    if (screen.getWidth() / 2 < primaryStage.getMinWidth()) {
                        primaryStage.setWidth(primaryStage.getMinWidth());
                    } else {
                        primaryStage.setWidth(screen.getWidth() / 2);
                    }
                    primaryStage.setX(screen.getMaxX() - primaryStage.getWidth());

                    snapped = true;
                }

                // Aero Snap Top ||  Aero Snap Bottom
                else if (m.getScreenY() <= screen.getMinY()
|| m.getScreenY() >= screen.getMaxY() - 1
) {
                    logger.info("snapping stage to top  - maximizing window");
                    if (!screen.contains(prevPos.x, prevPos.y)) {
                        if (prevSize.x > screen.getWidth())
                            prevSize.x = screen.getWidth() - 20;

                        if (prevSize.y > screen.getHeight())
                            prevSize.y = screen.getHeight() - 20;

                        prevPos.x = screen.getMinX() + (screen.getWidth() - prevSize.x) / 2;
                        prevPos.y = screen.getMinY() + (screen.getHeight() - prevSize.y) / 2;
                    }

                    primaryStage.setX(screen.getMinX());
                    primaryStage.setY(screen.getMinY());
                    primaryStage.setWidth(screen.getWidth());
                    primaryStage.setHeight(screen.getHeight());
                    setMaximized(true);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Hide the transparent window -- close this window no matter what
        transparentWindow.close();
    }


}
*/
