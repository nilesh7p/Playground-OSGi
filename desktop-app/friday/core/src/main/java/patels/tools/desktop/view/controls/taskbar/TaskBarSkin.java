package patels.tools.desktop.view.controls.taskbar;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskBarSkin<T> extends SkinBase<TaskBar<T>> {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String SCROLL_TO_KEY = "scroll.to";

    private final HBox content;
    private final Button leftBtn;
    private final Region leftBtnImg;
    private final Button rightBtn;
    private final Region rightBtnImg;
    private final Region leftFader;
    private final Region rightFader;

    private final Map<T, Node> nodeMap = new HashMap<>();

    /**
     * Constructor for all SkinBase instances.
     *
     * @param strip The strip for which this Skin should attach to.
     */
    public TaskBarSkin(TaskBar<T> strip) {
        super(strip);

        content = new HBox();
        content.setMinWidth(Region.USE_PREF_SIZE);
        content.setMaxWidth(Double.MAX_VALUE);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(2);


        leftBtnImg = new Region();
        leftBtnImg.getStyleClass().addAll("scroller", "left");
        leftBtn = new Button("", leftBtnImg);
        leftBtn.getStyleClass().addAll("reset","scroller-btn");
        leftBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        // leftBtn.setOpacity(0);
        leftBtn.setVisible(false);

        rightBtnImg = new Region();
        rightBtnImg.getStyleClass().addAll("scroller", "right");
        //rightBtn.setOpacity(0);
        rightBtn = new Button("", rightBtnImg);
        rightBtn.getStyleClass().addAll("reset","scroller-btn");
        rightBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        rightBtn.setVisible(false);

        leftFader = new Region();
        leftFader.setMouseTransparent(true);
        leftFader.getStyleClass().addAll("fader", "left");
        leftFader.setOpacity(0);

        rightFader = new Region();
        rightFader.setMouseTransparent(true);
        rightFader.getStyleClass().addAll("fader", "right");
        rightFader.setOpacity(0);

        getChildren().addAll(content, leftFader, rightFader, leftBtn, rightBtn);
        getChildren().forEach(child -> child.setManaged(false));

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(strip.widthProperty());
        clip.heightProperty().bind(strip.heightProperty());
        getSkinnable().setClip(clip);

        setupListeners();
        setupBindings();
        setupEventHandlers();

        strip.itemsProperty().addListener((Observable it) -> buildContent());
        buildContent();
    }

    private void scrollTo(T item) {
        Node node = nodeMap.get(item);

        if (node != null) {
            TaskBar strip = getSkinnable();

            strip.getProperties().remove(SCROLL_TO_KEY);

            final Bounds nodeBounds = node.localToParent(node.getLayoutBounds());

            final double x = -nodeBounds.getMinX() + strip.getWidth() / 2 - nodeBounds.getWidth() / 2;

            final double x1 = -translateX.get();
            final double x2 = x1 + strip.getLayoutBounds().getWidth();

            if (x1 > nodeBounds.getMinX() || x2 < nodeBounds.getMaxX()) {
                if (strip.isAnimateScrolling()) {
                    KeyValue keyValue = new KeyValue(translateX, x);
                    KeyFrame keyFrame = new KeyFrame(strip.getAnimationDuration(), keyValue);

                    Timeline timeline = new Timeline(keyFrame);
                    timeline.play();
                } else {
                    translateX.set(x);
                }
            }
        }
    }

    private void buildContent() {
        nodeMap.clear();
        final TaskBar<T> strip = getSkinnable();
        content.getChildren().setAll(strip.getItems().stream().map(item -> {
            final TaskBarCell<T> cell = strip.getCellFactory().call(strip);
            nodeMap.put(item, cell);
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> strip.setSelectedItem(item));
            cell.setSelectionStrip(strip);
            cell.setItem(item);
            return cell;
        }).collect(Collectors.toList()));
        strip.requestLayout();
    }

    private void setupListeners() {
        translateX.addListener(it -> content.setTranslateX(translateX.get()));
        getSkinnable().widthProperty().addListener(it -> fixTranslate());
        translateX.addListener(it -> fixTranslate());

        showLeftScroll.addListener((it, oldShow, newShow) -> fadeSupport(newShow, leftFader, leftBtn));
        showRightScroll
                .addListener((it, oldShow, newShow) -> fadeSupport(newShow, rightFader, rightBtn));
    }

    private void fadeSupport(Boolean newShow, Region fader, Region button) {
        if (getSkinnable().isAnimateScrolling()) {
            if (newShow) {
                button.setVisible(true);
                ParallelTransition parallelTransition = new ParallelTransition(
                        createFadeTransition(fader, 0, 1),
                        createFadeTransition(button, 0, 1));
                parallelTransition.play();
            } else {
                ParallelTransition parallelTransition = new ParallelTransition(
                        createFadeTransition(fader, 1, 0),
                        createFadeTransition(button, 1, 0));
                parallelTransition.setOnFinished(event -> button.setVisible(false));
                parallelTransition.play();
            }
        } else {
            fader.setVisible(newShow);
            button.setVisible(newShow);
        }
    }

    private FadeTransition createFadeTransition(Node node, double from, double to) {
        node.setOpacity(from);
        FadeTransition faderTransition = new FadeTransition();
        faderTransition.setNode(node);
        faderTransition.setFromValue(from);
        faderTransition.setToValue(to);
        faderTransition.setDuration(Duration.millis(200));
        return faderTransition;
    }

    private final BooleanProperty showLeftScroll =
            new SimpleBooleanProperty(this, "showLeftScroll", false);

    private final BooleanProperty showRightScroll =
            new SimpleBooleanProperty(this, "showRightScroll", false);

    private void setupBindings() {
        showLeftScroll.bind(translateX.lessThan(0));
        showRightScroll
                .bind(translateX.add(content.widthProperty()).greaterThan(getSkinnable().widthProperty()));
    }

    private void setupEventHandlers() {
        leftBtn.setOnMouseClicked(event -> scroll(true));
        rightBtn.setOnMouseClicked(event -> scroll(false));

        getSkinnable().addEventHandler(ScrollEvent.SCROLL,
                evt -> {
                    double delta = evt.getDeltaX() == 0 ? evt.getDeltaY() : evt.getDeltaX();
                    translateX.set(translateX.get() + delta);
                });
    }

    private void fixTranslate() {
        if (content.getWidth() < getSkinnable().getWidth()) {
            translateX.set(0);
        } else {
            double newValue = translateX.get();
            newValue = Math.min(newValue, 0);
            newValue = Math.max(newValue, -(content.getWidth() - getSkinnable().getWidth()));
            translateX.set(newValue);
        }
    }

    private Timeline timeline;
    private final DoubleProperty translateX = new SimpleDoubleProperty(this, "translateX");

    private void scroll(boolean scrollToRight) {
        // In case of the timeline is already playing the animation must first finish.
        if (timeline != null && timeline.getStatus().equals(Animation.Status.RUNNING)) {
            timeline.stop();
        }

        double scrollDistance = getSkinnable().getWidth() / 2;
        double dist = scrollToRight ? scrollDistance : -scrollDistance;

        if (getSkinnable().isAnimateScrolling()) {
            KeyValue keyValue = new KeyValue(translateX, translateX.get() + dist, Interpolator.EASE_BOTH);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(500), keyValue);

            timeline = new Timeline(keyFrame);
            timeline.play();
        } else {
            translateX.set(translateX.get() + dist);
        }
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth,
                                  double contentHeight) {
        content.resizeRelocate(contentX, contentY, content.prefWidth(-1), contentHeight);

        leftBtn.resizeRelocate(contentX, contentY + (contentHeight - leftBtn.prefHeight(-1)) / 2,
                leftBtn.prefWidth(-1), leftBtn.prefHeight(-1));
        rightBtn.resizeRelocate(contentX + contentWidth - rightBtn.prefWidth(-1),
                contentY + (contentHeight - rightBtn.prefHeight(-1)) / 2, rightBtn.prefWidth(-1),
                rightBtn.prefHeight(-1));

        leftFader.resizeRelocate(contentX, contentY, leftFader.prefWidth(-1), contentHeight);
        rightFader.resizeRelocate(contentX + contentWidth - rightFader.prefWidth(-1), contentY,
                rightFader.prefWidth(-1), contentHeight);

        @SuppressWarnings("unchecked")
        T item = (T) getSkinnable().getProperties().get(SCROLL_TO_KEY);
        if (item != null) {
            Platform.runLater(() -> scrollTo(item));
        }
    }
}
