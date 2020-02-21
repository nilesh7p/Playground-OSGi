package patels.tools.desktop.view.controls.mdi;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Lincoln Minto
 * @author Andres Almiray
 */
public class InternalWindowEvent extends Event {
    public static final EventType<InternalWindowEvent> WINDOW_SHOWING = new EventType<>(ANY, "WINDOW_SHOWING");
    public static final EventType<InternalWindowEvent> WINDOW_SHOWN = new EventType<>(ANY, "WINDOW_SHOWN");
    public static final EventType<InternalWindowEvent> WINDOW_HIDING = new EventType<>(ANY, "WINDOW_HIDING");
    public static final EventType<InternalWindowEvent> WINDOW_HIDDEN = new EventType<>(ANY, "WINDOW_HIDDEN");
    public static final EventType<InternalWindowEvent> WINDOW_CLOSE_REQUEST = new EventType<>(ANY, "WINDOW_CLOSE_REQUEST");

    public static final EventType<InternalWindowEvent> WINDOW_MINIMIZING = new EventType<>(ANY, "WINDOW_MINIMIZING");
    public static final EventType<InternalWindowEvent> WINDOW_MINIMIZED = new EventType<>(ANY, "WINDOW_MINIMIZED");
    public static final EventType<InternalWindowEvent> WINDOW_MAXIMIZING = new EventType<>(ANY, "WINDOW_MAXIMIZING");
    public static final EventType<InternalWindowEvent> WINDOW_MAXIMIZED = new EventType<>(ANY, "WINDOW_MAXIMIZED");
    public static final EventType<InternalWindowEvent> WINDOW_RESTORING = new EventType<>(ANY, "WINDOW_RESTORING");
    public static final EventType<InternalWindowEvent> WINDOW_RESTORED = new EventType<>(ANY, "WINDOW_RESTORED");
    public static final EventType<InternalWindowEvent> WINDOW_DETACHING = new EventType<>(ANY, "WINDOW_DETACHING");
    public static final EventType<InternalWindowEvent> WINDOW_DETACHED = new EventType<>(ANY, "WINDOW_DETACHED");
    public static final EventType<InternalWindowEvent> WINDOW_ATTACHING = new EventType<>(ANY, "WINDOW_ATTACHING");
    public static final EventType<InternalWindowEvent> WINDOW_ATTACHED = new EventType<>(ANY, "WINDOW_ATTACHED");

    private final InternalWindow internalWindow;

    public InternalWindowEvent(InternalWindow internalWindow, EventType<? extends Event> eventType) {
        super(internalWindow, internalWindow, eventType);
        this.internalWindow = internalWindow;
    }

    public InternalWindow getInternalWindow() {
        return internalWindow;
    }
}
