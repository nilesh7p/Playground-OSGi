package patels.tools.desktop.view;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.FXDesktop;

/**
 * Defines AbstractPresenter of jarvis.
 *
 * @param <V> the view for which this is a presenter of
 */
public abstract class AbstractPresenter<V extends View> implements Presenter {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final FXDesktop desktop;
    protected final V view;

    /**
     * Constructor for the AbstractPresenter
     *
     * @param desktop: reference of Jarvis {@link FXDesktop}
     * @param view:    the view for this presenter {@link View}
     */
    public AbstractPresenter(FXDesktop desktop, V view) {
        this.desktop = desktop;
        this.view = view;
        preInit();
        init();
        postInit();
    }
    /**
     * Performs tasks which need to be done before initializing
     */
    public void preInit() {

    }

    /**
     * Calls all the other methods for easier initialization.
     */
    public void init() {
        initializeViewParts();
        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();
    }

    /**
     * Initializes parts of the view which require more logic.
     */
    public void initializeViewParts() {

    }

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
    public void postInit() {

    }

    /**
     * Getter for Jarvis
     *
     * @return {@link FXDesktop}
     */
    public FXDesktop getJarvis() {
        return desktop;
    }

    /**
     * Getter for the View of the AbstractPresenter
     *
     * @return the view {@link View}
     */
    public V getView() {
        return view;
    }

    public View getIView() {
        return view;
    }
}
