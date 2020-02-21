package patels.tools.desktop.view;

import javafx.css.PseudoClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.FXDesktop;
import patels.tools.desktop.view.controls.module.Page;

public class AddModulePresenter extends AbstractPresenter<AddModuleView> {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final PseudoClass ONE_PAGE_STATE = PseudoClass.getPseudoClass("one-page");

    /**
     * Constructor for the AbstractPresenter
     *
     * @param desktop : reference of Jarvis {@link FXDesktop}
     * @param view    :    the view for this presenter {@link View}
     */
    public AddModulePresenter(FXDesktop desktop, AddModuleView view) {
        super(desktop, view);
    }

    @Override
    public void initializeViewParts() {
        updatePageCount(desktop.getAmountOfPages());
        view.setPageFactory(param -> {
            Page page = desktop.getPageFactory().call(desktop);
            page.setPageIndex(param);
            return page;
        });
        view.setMaxPageIndicatorCount(Integer.MAX_VALUE);

    }

    /**
     * Sets up event handlers of the view.
     */
    @Override
    public void setupEventHandlers() {

    }

    /**
     * Adds all listeners to view elements and model properties.
     */
    @Override
    public void setupValueChangedListeners() {
        desktop.amountOfPagesProperty().addListener(
                (observable, oldPageCount, newPageCount) -> updatePageCount(newPageCount.intValue()));

    }

    /**
     * Sets up bindings of the view.
     */
    @Override
    public void setupBindings() {

    }

    private void updatePageCount(int amountOfPages) {
        view.setPageCount(amountOfPages);
        view.pseudoClassStateChanged(ONE_PAGE_STATE, amountOfPages == 1);
    }
}
