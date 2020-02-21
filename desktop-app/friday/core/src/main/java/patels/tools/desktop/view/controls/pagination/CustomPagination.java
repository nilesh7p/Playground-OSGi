package patels.tools.desktop.view.controls.pagination;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class CustomPagination extends Control {

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomPaginationSkin(this);
    }
}
