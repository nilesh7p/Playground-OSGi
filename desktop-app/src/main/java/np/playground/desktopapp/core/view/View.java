package np.playground.desktopapp.core.view;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public interface View {

    /**
     * Holds a list of stylesheets.
     *
     * @return list of stylesheets.
     */
    List<String> getStylesheets();

    /**
     * Calls all the other methods for easier initialization.
     */
    default void init() {
        initializeSelf();
        initializeComponents();
        layoutComponents();
    }

    /**
     * Initializes the view.
     */
    void initializeSelf();

    /**
     * Initializes all parts of the view.
     */
    void initializeComponents();

    /**
     * Defines the layout of all parts in the view.
     */
    void layoutComponents();

    /**
     * Adds the stylesheet files to the getStylesheets method.
     *
     * @param stylesheetFile list of stylesheet files
     */
    default void addStylesheetFiles(String... stylesheetFile) {
        if (Objects.nonNull(stylesheetFile)) {
            Arrays.asList(stylesheetFile).forEach(stylesheet ->
                    getStylesheets().add(getClass().getResource(stylesheet).toExternalForm()));
        }

    }
}
