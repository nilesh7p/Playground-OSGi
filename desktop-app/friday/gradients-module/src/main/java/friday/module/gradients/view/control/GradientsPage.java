package friday.module.gradients.view.control;


import friday.module.gradients.GradientsModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.FXDesktop;

import static patels.tools.desktop.utils.FXDesktopUtil.calculateColumnsPerRow;


public class GradientsPage extends StackPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradientsPage.class.getName());
    private final GradientsModule gradientsModule;
    private final Integer pageIndex;
    ObservableList<GradientTile> tiles = FXCollections.observableArrayList();
    private GridPane tilePane;

    public GradientsPage(GradientsModule gradientsModule, Integer pageIndex) {
        getStyleClass().add("page-control");
        this.gradientsModule = gradientsModule;
        this.pageIndex = pageIndex;
        tilePane = new GridPane();
        tilePane.getStyleClass().add("tile-pane");
        updateTiles();
        layoutTiles();
        getChildren().add(tilePane);
        setAlignment(Pos.CENTER);
    }

    private void layoutTiles() {
        LOGGER.trace("Setup GradientsPageSkin");
        // remove any pre-existing tiles
        tilePane.getChildren().clear();
        LOGGER.trace("Cleared children");
        int column = 0;
        int row = 0;

        final int columnsPerRow = calculateColumnsPerRow(tiles.size());
        LOGGER.trace("Calculated Columns per Row= {}", columnsPerRow);
        for (GradientTile tile : tiles) {
            tile.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                LOGGER.trace("Tile clicked changing wall");
                Platform.runLater(()->gradientsModule.getDesktop().setWallpaper(new Background(new BackgroundFill(tile.getGradient(), CornerRadii.EMPTY, Insets.EMPTY))));

            });
            /*tile.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
                LOGGER.trace("Tile clicked changing wall");
                Platform.runLater(()->gradientsModule.getDesktop().setWallpaper(FXDesktop.DEFAULT_WALLPAPER));

            });*/
            tilePane.add(tile, column, row);
            column++;

            if (column == columnsPerRow) {
                column = 0;
                row++;
            }
        }
    }

    private void updateTiles() {
        LOGGER.trace("Updating tiles for page index {}", getPageIndex());
        // remove any preexisting tiles in the list
        LOGGER.debug(String.format("Tiles in page %s are being updated", getPageIndex()));
        LOGGER.trace(String.format("Page Index: %s, Modules Per Page: %s", getPageIndex(), gradientsModule.getModulesPerPage()));
        int position = getPageIndex() * gradientsModule.getModulesPerPage();
        // create tile
        gradientsModule.getGradients().stream()
                .skip(position) // skip all tiles from previous pages
                .limit(gradientsModule.getModulesPerPage()) // only take as many tiles as there are per page
                .map(GradientTile::new)
                .map(GradientTile.class::cast)
                .forEachOrdered(tiles::add);
    }

    public final int getPageIndex() {
        return pageIndex;
    }


    public final ObservableList<GradientTile> getTiles() {
        return FXCollections.unmodifiableObservableList(tiles);
    }

}
