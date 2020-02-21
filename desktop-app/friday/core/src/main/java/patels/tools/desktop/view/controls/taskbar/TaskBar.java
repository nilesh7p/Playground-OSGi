package patels.tools.desktop.view.controls.taskbar;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.Callback;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.model.DesktopModule;
import patels.tools.desktop.utils.FXDesktopUtil;

import java.util.Optional;

public class TaskBar<T> extends Control {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Constructs a new {@link TaskBar}.
     */
    public TaskBar() {
        getStyleClass().add("task-bar");

        setPrefWidth(400);
        setPrefHeight(50);

        setCellFactory(strip -> new TaskBarCell<>());

        selectedItemProperty().addListener(it -> {
            if (getSelectedItem() != null && isAutoScrolling()) {
                scrollTo(getSelectedItem());
                requestLayout();
            }
        });
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TaskBarSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return FXDesktopUtil.getStyleSheet("task-bar.css");
    }

    // Autoscrolling.

    private final BooleanProperty autoScrolling = new SimpleBooleanProperty(this, "autoScrolling",
            true);

    public final BooleanProperty autoScrollingProperty() {
        return autoScrolling;
    }

    public final boolean isAutoScrolling() {
        return autoScrolling.get();
    }

    public final void setAutoScrolling(boolean autoScrolling) {
        this.autoScrolling.set(autoScrolling);
    }

    // Animation support.

    private final BooleanProperty animateScrolling = new SimpleBooleanProperty(this,
            "animateScrolling", true);

    public final BooleanProperty animateScrollingProperty() {
        return animateScrolling;
    }

    public final boolean isAnimateScrolling() {
        return animateScrolling.get();
    }

    public final void setAnimateScrolling(boolean animateScrolling) {
        this.animateScrolling.set(animateScrolling);
    }

    // Animation duration support.

    private final ObjectProperty<Duration> animationDuration = new SimpleObjectProperty<>(this,
            "animationDuration", Duration
            .millis(200));

    public final ObjectProperty<Duration> animationDurationProperty() {
        return animationDuration;
    }

    public final Duration getAnimationDuration() {
        return animationDuration.get();
    }

    public final void setAnimationDuration(Duration animationDuration) {
        this.animationDuration.set(animationDuration);
    }

    // Selection model support.

    public final ObjectProperty<T> selectedItem = new SimpleObjectProperty<>(this, "selectedItem");

    public final ObjectProperty<T> selectedItemProperty() {
        return selectedItem;
    }

    public final T getSelectedItem() {
        return selectedItemProperty().get();
    }

    public final void setSelectedItem(T selectedItem) {
        selectedItemProperty().set(selectedItem);
    }

    // items support

    private final ListProperty<T> items = new SimpleListProperty<>(this, "items",
            FXCollections.observableArrayList());

    public final ListProperty<T> itemsProperty() {
        return items;
    }

    public final ObservableList<T> getItems() {
        return items.get();
    }

    public final void setItems(ObservableList<T> items) {
        this.items.set(items);
    }

    // cell factory support

    private final ObjectProperty<Callback<TaskBar, TaskBarCell<T>>> cellFactory
            = new SimpleObjectProperty<>(this, "cellFactory");

    public Callback<TaskBar, TaskBarCell<T>> getCellFactory() {
        return cellFactory.get();
    }

    public ObjectProperty<Callback<TaskBar, TaskBarCell<T>>> cellFactoryProperty() {
        return cellFactory;
    }

    public void setCellFactory(Callback<TaskBar, TaskBarCell<T>> cellFactory) {
        this.cellFactory.set(cellFactory);
    }

    public void scrollTo(T item) {
        getProperties().put("scroll.to", item);
    }

    public Optional<DesktopModule> findTaskBarIcon(String id) {
        /*return getTaskBarIcons().stream()
                .filter(icon -> icon.getId().equals(id))
                .findFirst();*/
        return ((ObservableList<DesktopModule>) getItems()).stream().filter(desktopModule -> desktopModule.getName().contains(id)).findFirst();

    }
}
