package patels.tools.desktop.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.model.DesktopModule;
import patels.tools.desktop.view.controls.taskbar.TaskBar;

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
        FontIcon icon = FontIcon.of(MaterialDesign.MDI_APPS, 20, Color.BLACK);
        icon.setId("menu-icon");
        menuButton = new Button("", icon);
        menuButton.setId("menu-button");
        menuButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        FontIcon tileIcon = FontIcon.of(MaterialDesign.MDI_ANIMATION, 20, Color.BLACK);
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
