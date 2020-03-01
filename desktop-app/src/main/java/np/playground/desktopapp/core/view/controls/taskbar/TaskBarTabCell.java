package np.playground.desktopapp.core.view.controls.taskbar;

import np.playground.desktopapp.core.FXDesktop;
import np.playground.desktopapp.core.model.DesktopModule;
import np.playground.desktopapp.core.view.controls.module.Tab;

public class TaskBarTabCell extends TaskBarCell<DesktopModule> {
    private static final String FIRST_CHILD = "first-child";

    /**
     * Constructs a new {@link TaskBarTabCell}.
     */
    public TaskBarTabCell() {
        super();
        itemProperty().addListener(it -> {
            // Remove text which was set in the listener of StripCell
            setText("");
            // Create Tab
            FXDesktop workbench = getItem().getDesktop();
            Tab tab = workbench.getTabFactory().call(workbench);
            tab.setModule(getItem());
            setGraphic(tab);

      /*
        To remove the background-insets from this cell.
        Otherwise the SelectionStrip's end would cut off the side.
       */
            if (getSelectionStrip().getItems().get(0).equals(getItem())) {
                getStyleClass().add(FIRST_CHILD);
            }
        });
    }
}
