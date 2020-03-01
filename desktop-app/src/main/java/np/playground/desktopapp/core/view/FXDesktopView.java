package np.playground.desktopapp.core.view;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import np.playground.desktopapp.core.model.DesktopModule;
import np.playground.desktopapp.core.model.DesktopOverlay;
import np.playground.desktopapp.core.view.controls.GlassPane;
import np.playground.desktopapp.core.view.controls.mdi.InternalWindow;
import np.playground.desktopapp.core.view.controls.mdi.InternalWindowEvent;
import np.playground.desktopapp.core.view.controls.mdi.PositionOutOfBoundsException;
import np.playground.desktopapp.core.view.controls.taskbar.TaskBar;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static patels.tools.desktop.utils.FXDesktopUtil.createBackground;

public class FXDesktopView extends StackPane implements View {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final TaskBarView taskBarView;
    private final TaskBar<DesktopModule> taskBar;
    private final AddModuleView addModuleView;
    private StackPane internalStackPane;
    private AnchorPane internalWindowContainer;
    private BorderPane layout;
    private DesktopOverlay addModuleOverlay;

    private final ListProperty<InternalWindow> internalWindows = new SimpleListProperty<>(this, "internalWindows",
            FXCollections.observableArrayList());

    private final ObjectProperty<InternalWindow> activeWindow = new SimpleObjectProperty<>(this, "activeWindow");

    public FXDesktopView(TaskBarView taskBarView, AddModuleView addModuleView) {
        this.taskBarView = taskBarView;
        this.addModuleView = addModuleView;
        this.taskBar = taskBarView.getTaskBar();
        init();
    }

    /**
     * Initializes the view.
     */
    @Override
    public void initializeSelf() {
        setBackground(createBackground("Flow2.jpg"));
    }

    /**
     * Initializes all parts of the view.
     */
    @Override
    public void initializeComponents() {
        internalWindowContainer = new AnchorPane();
        internalStackPane = new StackPane(internalWindowContainer);
        layout = new BorderPane();
        addModuleOverlay = new DesktopOverlay(addModuleView, new GlassPane());
        Region overlay = addModuleOverlay.getOverlay();
        overlay.setVisible(false);
        GlassPane glassPane = addModuleOverlay.getGlassPane();
        overlay.visibleProperty().addListener(observable -> glassPane.setHide(!overlay.isVisible()));
    }

    /**
     * Defines the layout of all parts in the view.
     */
    @Override
    public void layoutComponents() {
        layout.setCenter(internalStackPane);
        layout.setBottom(taskBarView);

        getChildren().add(layout);
    }

    public void snapTo(InternalWindow internalWindow, InternalWindow.AlignPosition alignPosition) {
        double canvasH = internalWindowContainer.getLayoutBounds().getHeight();
        double canvasW = internalWindowContainer.getLayoutBounds().getWidth();
        double mdiH = internalWindow.getLayoutBounds().getHeight();
        double mdiW = internalWindow.getLayoutBounds().getWidth();

        switch (alignPosition) {
            case CENTER:
                centerInternalWindow(internalWindow);
                break;
            case CENTER_LEFT:
                placeInternalWindow(internalWindow, new Point2D(0, (int) (canvasH / 2) - (int) (mdiH / 2)));
                break;
            case CENTER_RIGHT:
                placeInternalWindow(internalWindow, new Point2D((int) canvasW - (int) mdiW, (int) (canvasH / 2) - (int) (mdiH / 2)));
                break;
            case TOP_CENTER:
                placeInternalWindow(internalWindow, new Point2D((int) (canvasW / 2) - (int) (mdiW / 2), 0));
                break;
            case TOP_LEFT:
                placeInternalWindow(internalWindow, Point2D.ZERO);
                break;
            case TOP_RIGHT:
                placeInternalWindow(internalWindow, new Point2D((int) canvasW - (int) mdiW, 0));
                break;
            case BOTTOM_LEFT:
                placeInternalWindow(internalWindow, new Point2D(0, (int) canvasH - (int) mdiH));
                break;
            case BOTTOM_RIGHT:
                placeInternalWindow(internalWindow, new Point2D((int) canvasW - (int) mdiW, (int) canvasH - (int) mdiH));
                break;
            case BOTTOM_CENTER:
                placeInternalWindow(internalWindow, new Point2D((int) (canvasW / 2) - (int) (mdiW / 2), (int) canvasH - (int) mdiH));
                break;
        }
    }

    public void placeInternalWindow(InternalWindow internalWindow, Point2D point) {
        double windowsWidth = internalWindow.getLayoutBounds().getWidth();
        double windowsHeight = internalWindow.getLayoutBounds().getHeight();
        internalWindow.setPrefSize(windowsWidth, windowsHeight);

        double containerWidth = internalWindowContainer.getLayoutBounds().getWidth();
        double containerHeight = internalWindowContainer.getLayoutBounds().getHeight();
        if (containerWidth <= point.getX() || containerHeight <= point.getY()) {
            throw new PositionOutOfBoundsException(
                    "Tried to snapTo MDI Window with ID " + internalWindow.getId() +
                            " at a coordinate " + point.toString() +
                            " that is beyond current size of the MDI container " +
                            containerWidth + "px x " + containerHeight + "px."
            );
        }

        if ((containerWidth - point.getX() < 40) ||
                (containerHeight - point.getY() < 40)) {
            throw new PositionOutOfBoundsException(
                    "Tried to snapTo MDI Window with ID " + internalWindow.getId() +
                            " at a coordinate " + point.toString() +
                            " that is too close to the edge of the parent of size " +
                            containerWidth + "px x " + containerHeight + "px " +
                            " for user to comfortably grab the title bar with the mouse."
            );
        }

        internalWindow.setLayoutX((int) point.getX());
        internalWindow.setLayoutY((int) point.getY());
    }

    public void centerInternalWindow(InternalWindow internalWindow) {
        double w = internalWindowContainer.getLayoutBounds().getWidth();
        double h = internalWindowContainer.getLayoutBounds().getHeight();

        Platform.runLater(() -> {
            double windowsWidth = internalWindow.getLayoutBounds().getWidth();
            double windowsHeight = internalWindow.getLayoutBounds().getHeight();

            Point2D centerCoordinate = new Point2D(
                    (int) (w / 2) - (int) (windowsWidth / 2),
                    (int) (h / 2) - (int) (windowsHeight / 2)
            );
            this.placeInternalWindow(internalWindow, centerCoordinate);
        });
    }

    public static InternalWindow resolveInternalWindow(Node node) {
        if (node == null) {
            return null;
        }

        Node candidate = node;
        while (candidate != null) {
            if (candidate instanceof InternalWindow) {
                return (InternalWindow) candidate;
            }
            candidate = candidate.getParent();
        }

        return null;
    }

    public FXDesktopView removeInternalWindow(InternalWindow internalWindow) {
        ObservableList<Node> windows = internalWindowContainer.getChildren();
        if (internalWindow != null && windows.contains(internalWindow)) {
            internalWindows.remove(internalWindow);
            windows.remove(internalWindow);
            internalWindow.setDesktopPane(null);
        }

        return this;
    }

    public FXDesktopView removeInternalWindow(String windowId) {
        findInternalWindow(windowId).ifPresent(internalWindow -> {
            internalWindow.setClosed(true);
            internalWindows.remove(internalWindow);
            internalWindowContainer.getChildren().remove(internalWindow);
        });

        // getTaskBar().findTaskBarIcon(windowId).ifPresent(taskBar::removeTaskBarIcon);
        // TODO
        taskBarView.getTaskBar().findTaskBarIcon(windowId);
        return this;
    }

    public Optional<InternalWindow> findInternalWindow(String id) {
        return internalWindows.stream()
                .filter(window -> window.getId().equals(id))
                .findFirst();
    }

    public FXDesktopView addInternalWindow(InternalWindow internalWindow, Point2D position) {
        if (internalWindow != null) {
            if (findInternalWindow(internalWindow.getId()).isPresent()) {
                restoreExisting(internalWindow);
            } else {
                addNew(internalWindow, position);
            }
            internalWindow.setDesktopPane(this);
        }
        return this;
    }

    private void restoreExisting(InternalWindow internalWindow) {
        if (internalWindows.contains(internalWindow)) {
            internalWindow.toFront();
            internalWindow.setVisible(true);
        }
    }

    public FXDesktopView addInternalWindow(InternalWindow internalWindow) {
        LOGGER.trace("Trying to add Internal Window {}", internalWindow);
        if (internalWindow != null) {
            if (findInternalWindow(internalWindow.getId()).isPresent()) {
                LOGGER.trace("Existing Internal Window {} found. Restoring it.", internalWindow);
                restoreExisting(internalWindow);
            } else {
                LOGGER.trace("Adding New Internal Window {}", internalWindow);
                addNew(internalWindow, null);
            }
            internalWindow.setDesktopPane(this);
        }
        return this;
    }

    private void addNew(InternalWindow internalWindow, Point2D position) {
        internalWindows.add(internalWindow);
        internalWindowContainer.getChildren().add(internalWindow);
        if (position == null) {
            internalWindow.layoutBoundsProperty().addListener(new WidthChangeListener(this, internalWindow));
        } else {
            placeInternalWindow(internalWindow, position);
        }
        fireEvent(new InternalWindowEvent(internalWindow, InternalWindowEvent.WINDOW_SHOWING));
        internalWindow.toFront();
        fireEvent(new InternalWindowEvent(internalWindow, InternalWindowEvent.WINDOW_SHOWN));
    }

    public void show(DesktopModule module, Node activeModuleView) {
        addInternalWindow(new InternalWindow(module, activeModuleView));
    }

    public TaskBar<DesktopModule> getTaskBar() {
        return taskBar;
    }

    public TaskBarView getTaskBarView() {
        return taskBarView;
    }

    public AddModuleView getAddModuleView() {
        return addModuleView;
    }

    public DesktopOverlay getAddModuleOverlay() {
        return addModuleOverlay;
    }

    public void setAddModuleOverlay(DesktopOverlay addModuleOverlay) {
        this.addModuleOverlay = addModuleOverlay;
    }

    public StackPane getInternalStackPane() {
        return internalStackPane;
    }

    public void setInternalStackPane(StackPane internalStackPane) {
        this.internalStackPane = internalStackPane;
    }


    public ObservableList<InternalWindow> getInternalWindows() {
        return internalWindows.get();
    }

    public ListProperty<InternalWindow> internalWindowsProperty() {
        return internalWindows;
    }

    public ObservableList<InternalWindow> getUnmodifiableInternalWindows() {
        return FXCollections.unmodifiableObservableList(internalWindows);
    }

    public InternalWindow getActiveWindow() {
        return activeWindow.get();
    }

    public ObjectProperty<InternalWindow> activeWindowProperty() {
        return activeWindow;
    }

    public void setActiveWindow(InternalWindow activeWindow) {
        this.activeWindow.set(activeWindow);
    }

    private static class WidthChangeListener implements ChangeListener<Bounds> {
        private FXDesktopView desktopPane;
        private InternalWindow window;

        public WidthChangeListener(FXDesktopView desktopPane, InternalWindow window) {
            this.desktopPane = desktopPane;
            this.window = window;
        }

        @Override
        public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
            desktopPane.centerInternalWindow(this.window);
            observable.removeListener(this);
        }
    }
// ------------------------------------------------------------------

    public void minimizeAllWindows() {
        getInternalWindows().forEach(InternalWindow::minimizeWindow);
        setActiveWindow(null);
    }

    public void minimizeOtherWindows() {
        InternalWindow currentWindow = getActiveWindow();

        getInternalWindows().stream()
                .filter(window -> !window.equals(currentWindow))
                .forEach(InternalWindow::minimizeWindow);

        setActiveWindow(currentWindow);
    }

    public void maximizeAllWindows() {
        InternalWindow currentWindow = getActiveWindow();

        restoreMinimizedWindows();
        maximizeVisibleWindows();

        setActiveWindow(currentWindow);
    }

    public void maximizeVisibleWindows() {
        InternalWindow currentWindow = getActiveWindow();
        InternalWindow[] iteration = new InternalWindow[1];

        getInternalWindows().stream()
                .filter(window -> !window.isMinimized())
                .filter(window -> !window.isMaximized())
                .forEach(window -> {
                    iteration[0] = window;
                    window.maximizeOrRestoreWindow();
                });

        if (currentWindow != null) {
            setActiveWindow(currentWindow);
        } else {
            setActiveWindow(iteration[0]);
        }
    }

    public void restoreMinimizedWindows() {
        InternalWindow currentWindow = getActiveWindow();
        InternalWindow[] iteration = new InternalWindow[1];

        getInternalWindows().stream()
                .filter(InternalWindow::isMinimized)
                .forEach(window ->
                        getTaskBar().findTaskBarIcon(window.getId()).ifPresent(icon -> {
                            iteration[0] = window;
                            icon.restoreWindow();
                        }));

        if (currentWindow != null) {
            setActiveWindow(currentWindow);
        } else {
            setActiveWindow(iteration[0]);
        }
    }

    public void restoreVisibleWindows() {
        InternalWindow currentWindow = getActiveWindow();
        InternalWindow[] iteration = new InternalWindow[1];

        getInternalWindows().stream()
                .filter(window -> !window.isMinimized())
                .filter(InternalWindow::isMaximized)
                .forEach(window -> {
                    iteration[0] = window;
                    window.maximizeOrRestoreWindow();
                });

        if (currentWindow != null) {
            setActiveWindow(currentWindow);
        } else {
            setActiveWindow(iteration[0]);
        }
    }

    public void closeAllWindows() {
        /*List<InternalWindow> windows = new ArrayList<>(getInternalWindows());

        windows.forEach(window ->
                getTaskBar().findTaskBarIcon(window.getId()).ifPresent(TaskBarIcon::closeWindow));

        windows.forEach(InternalWindow::closeWindow);

        setActiveWindow(null);*/
    }

    public void closeOtherWindows() {
       /* InternalWindow currentWindow = getActiveWindow();

        List<InternalWindow> windows = new ArrayList<>(getInternalWindows());

        windows.stream()
                .filter(window -> !window.equals(currentWindow))
                .forEach(window ->
                        getTaskBar().findTaskBarIcon(window.getId()).ifPresent(TaskBarIcon::closeWindow));

        windows.stream()
                .filter(window -> !window.equals(currentWindow))
                .forEach(InternalWindow::closeWindow);

        setActiveWindow(currentWindow);*/
    }

    public void tileAllWindows() {
        tileAllWindows(-1, -1);
    }

    public void tileAllWindows(double windowWidth, double windowHeight) {
        tileWindows(getInternalWindows(), windowWidth, windowHeight);
    }

    public void tileVisibleWindows() {
        tileVisibleWindows(-1, -1);
    }

    public void tileVisibleWindows(double windowWidth, double windowHeight) {
        tileWindows(getInternalWindows().stream()
                        .filter(window -> !window.isMinimized())
                        .collect(toList()),
                windowWidth, windowHeight);
    }

    public void tileHorizontally() {
        List<InternalWindow> windows = getInternalWindows().stream()
                .filter(window -> !window.isMinimized())
                .collect(toList());

        double containerWidth = internalWindowContainer.getLayoutBounds().getWidth();
        double containerHeight = internalWindowContainer.getLayoutBounds().getHeight();
        double windowHeight = containerHeight / windows.size();

        for (int i = 0; i < windows.size(); i++) {
            InternalWindow window = windows.get(i);
            if (window.isMaximized()) {
                window.maximizeOrRestoreWindow();
            }
            window.setMinHeight(Math.min(window.getMinHeight(), windowHeight));
            window.setPrefSize(containerWidth, windowHeight);
            window.setLayoutX(0);
            window.setLayoutY(windowHeight * i);
            setActiveWindow(window);
        }
    }

    public void tileVertically() {
        List<InternalWindow> windows = getInternalWindows().stream()
                .filter(window -> !window.isMinimized())
                .collect(toList());

        double containerWidth = internalWindowContainer.getLayoutBounds().getWidth();
        double containerHeight = internalWindowContainer.getLayoutBounds().getHeight();
        double windowWidth = containerWidth / windows.size();

        for (int i = 0; i < windows.size(); i++) {
            InternalWindow window = windows.get(i);
            if (window.isMaximized()) {
                window.maximizeOrRestoreWindow();
            }
            window.setMinWidth(Math.min(window.getMinWidth(), windowWidth));
            window.setPrefSize(windowWidth, containerHeight);
            window.setLayoutX(windowWidth * i);
            window.setLayoutY(0);
            setActiveWindow(window);
        }
    }

    private void tileWindows(Collection<InternalWindow> windows, double windowWidth, double windowHeight) {
        int count = 0;
        double x = 0;
        double y = 0;
        int offset = 40;

        for (InternalWindow window : windows) {

            if (window.isMaximized() || window.isMinimized()) {
                window.maximizeOrRestoreWindow();
            }

            if (windowWidth > 0 && windowHeight > 0) {
                window.setPrefSize(windowWidth, windowHeight);
            }

            try {
                placeInternalWindowNoResize(window, new Point2D(x, y));
            } catch (PositionOutOfBoundsException e) {
                x = (++count) * offset;
                y = 0;
                placeInternalWindowNoResize(window, new Point2D(x, y));
            }
            setActiveWindow(window);

            x += offset;
            y += offset;
        }
    }

    public void placeInternalWindowNoResize(InternalWindow internalWindow, Point2D point) {
        double containerWidth = internalWindowContainer.getLayoutBounds().getWidth();
        double containerHeight = internalWindowContainer.getLayoutBounds().getHeight();

        if (containerWidth > point.getX() && containerHeight > point.getY()) {
            if (containerWidth - point.getX() >= 40 && containerHeight - point.getY() >= 40) {
                internalWindow.setLayoutX(point.getX());
                internalWindow.setLayoutY(point.getY());
            } else {
                throw new PositionOutOfBoundsException(
                        "Tried to snapTo MDI Window with ID " + internalWindow.getId() +
                                " at a coordinate " + point.toString() +
                                " that is too close to the edge of the parent of size " +
                                containerWidth + "px x " + containerHeight + "px " +
                                " for user to comfortably grab the title bar with the mouse.");
            }
        } else {
            throw new PositionOutOfBoundsException(
                    "Tried to snapTo MDI Window with ID " + internalWindow.getId() +
                            " at a coordinate " + point.toString() +
                            " that is beyond current size of the MDI container " +
                            containerWidth + "px x " + containerHeight + "px.");
        }

    }


}
