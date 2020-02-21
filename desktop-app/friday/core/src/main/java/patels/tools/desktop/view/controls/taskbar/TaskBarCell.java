package patels.tools.desktop.view.controls.taskbar;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.Label;

public class TaskBarCell<T> extends Label {
    private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");

    private final InvalidationListener selectionListener = it -> updateSelection();

    private final WeakInvalidationListener weakSelectionListener = new WeakInvalidationListener(
            selectionListener);

    /**
     * Constructs a new {@link TaskBarCell}.
     */
    public TaskBarCell() {
        getStyleClass().add("task-bar-cell");
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        selectionStripProperty().addListener((it, oldStrip, newStrip) -> {
            if (oldStrip != null) {
                oldStrip.selectedItemProperty().removeListener(weakSelectionListener);
            }

            if (newStrip != null) {
                newStrip.selectedItemProperty().addListener(weakSelectionListener);
                updateSelection();
            }
        });

        itemProperty().addListener(it -> {
            setText(getItem().toString());
            updateSelection();
        });
    }

    private void updateSelection() {
        final T selectedItem = getSelectionStrip().getSelectedItem();
        setSelected(selectedItem == getItem());
    }

    private final ObjectProperty<TaskBar<T>> selectionStrip = new SimpleObjectProperty<>(this,
            "selectionstrip");

    public final ObjectProperty<TaskBar<T>> selectionStripProperty() {
        return selectionStrip;
    }

    public final TaskBar<T> getSelectionStrip() {
        return selectionStrip.get();
    }

    public final void setSelectionStrip(TaskBar<T> selectionStrip) {
        this.selectionStrip.set(selectionStrip);
    }

    private BooleanProperty selected = new BooleanPropertyBase() {
        @Override
        protected void invalidated() {
            final boolean selected = get();
            pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, selected);
            notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTED);
        }

        @Override
        public Object getBean() {
            return TaskBarCell.this;
        }

        @Override
        public String getName() {
            return "selected";
        }
    };

    public final void setSelected(boolean value) {
        selectedProperty().set(value);
    }

    public final boolean isSelected() {
        return selected.get();
    }

    /**
     * Returns the property of the {@link TaskBarCell }s item which defines whether it's selected or
     * not.
     *
     * @return the property which changes the value when the item is selected
     */
    public final BooleanProperty selectedProperty() {
        return selected;
    }

    private final ObjectProperty<T> item = new SimpleObjectProperty<>(this, "item");

    public final T getItem() {
        return item.get();
    }

    public final ObjectProperty<T> itemProperty() {
        return item;
    }

    public final void setItem(T item) {
        this.item.set(item);
    }
}
