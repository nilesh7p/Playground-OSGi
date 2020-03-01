package np.playground.desktopapp.core.view;

import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import np.playground.desktopapp.core.model.DesktopModule;
import np.playground.desktopapp.core.view.controls.taskbar.TaskBar;

public class TaskBarView extends BorderPane implements View {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private Button menuButton;
    private Button tileButton;
    private Button glassButton;
    private TaskBar<DesktopModule> taskBar;

    public TaskBarView() {
        init();
    }

    /**
     * Initializes the view.
     */
    @Override
    public void initializeSelf() {
        setId("taskbar-view");
        //setHeights(30);
    }

    private void setHeights(double i) {
        setPrefHeight(i);
        setMaxHeight(i);
        setMinHeight(i);
    }

    /**
     * Initializes all parts of the view.
     */
    @Override
    public void initializeComponents() {
        MaterialDesignIconView iconView = new MaterialDesignIconView(MaterialDesignIcon.APPS);
        iconView.setGlyphSize(20);
        iconView.setGlyphStyle("-fx-text-fill:black;-fx-fill:black;");
        iconView.setId("menu-icon");
        menuButton = new Button("", iconView);
        menuButton.setId("menu-button");
        menuButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        MaterialDesignIconView tileIcon = new MaterialDesignIconView(MaterialDesignIcon.ANCHOR);
        tileIcon.setGlyphSize(20);
        tileIcon.setGlyphStyle("-fx-text-fill:black;-fx-fill:black;");
        tileIcon.setId("tile-icon");
        tileButton = new Button("", tileIcon);
        tileButton.setId("tile-button");
        taskBar = new TaskBar<>();
        glassButton = new Button("");
        glassButton.setMaxWidth(10);
        glassButton.setMinWidth(10);
        glassButton.setPrefWidth(10);
        glassButton.setId("glass-button");

        glassButton.setTooltip(new Tooltip("Show Desktop"));
        tileButton.setTooltip(new Tooltip("Tile all Windows"));
        menuButton.setTooltip(new Tooltip("Show/Hide App Menu"));
    }

    /**
     * Defines the layout of all parts in the view.
     */
    @Override
    public void layoutComponents() {
        setMargin(taskBar, new Insets(2));
        setMargin(menuButton, new Insets(2));
        setLeft(menuButton);
        setCenter(taskBar);
        setRight(new HBox(2, tileButton, glassButton));

    }

    public Button getMenuButton() {
        return menuButton;
    }

    public Button getTileButton() {
        return tileButton;
    }public Button getGlassButton() {
        return glassButton;
    }

    public TaskBar<DesktopModule> getTaskBar() {
        return taskBar;
    }
}
