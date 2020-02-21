
package patels.tools.desktop.view.controls.module;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static patels.tools.desktop.utils.FXDesktopUtil.calculateColumnsPerRow;

public class PageSkin extends SkinBase<Page> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageSkin.class.getName());
    private final ObservableList<Tile> tiles;
    private GridPane tilePane;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected PageSkin(Page control) {
        super(control);
        tiles = control.getTiles();
        initializeParts();

        setupSkin(); // initial setup
        setupListeners(); // setup for changing modules

        getChildren().add(tilePane);
    }

    private void initializeParts() {
        tilePane = new GridPane();
        tilePane.getStyleClass().add("tile-pane");
    }

    private void setupListeners() {
        LOGGER.trace("Add listener");
        tiles.addListener((InvalidationListener) observable -> setupSkin());
    }

    private void setupSkin() {
        // remove any pre-existing tiles
        tilePane.getChildren().clear();

        int column = 0;
        int row = 0;

        final int columnsPerRow = calculateColumnsPerRow(tiles.size());
        for (Tile tile : tiles) {
            tilePane.add(tile, column, row);
            column++;

            if (column == columnsPerRow) {
                column = 0;
                row++;
            }
        }
    }
}
