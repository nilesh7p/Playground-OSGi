package patels.tools.desktop.view;

public interface Presenter {
    /**
     * Performs tasks which need to be done before initializing
     */
    public void preInit() ;

    /**
     * Calls all the other methods for easier initialization.
     */
    default void init() {
        initializeViewParts();
        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();
    }

    /**
     * Initializes parts of the view which require more logic.
     */
    public void initializeViewParts() ;

    /**
     * Sets up event handlers of the view.
     */
    public abstract void setupEventHandlers();

    /**
     * Adds all listeners to view elements and model properties.
     */
    public abstract void setupValueChangedListeners();

    /**
     * Sets up bindings of the view.
     */
    public abstract void setupBindings();

    /**
     * Performs tasks which need to be done after initializing
     */
    public void postInit() ;
}
