package friday.module.gradients;

import friday.module.gradients.view.GradientsView;
import friday.module.gradients.view.LinearGradient;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import patels.tools.desktop.model.DesktopModule;

public class GradientsModule extends DesktopModule {
    private final int DEFAULT_MODULES_PER_PAGE = 20;

    /**
     * Number of Modules (Tiles) per Page on AddModuleView
     */
    private final IntegerProperty modulesPerPage =
            new SimpleIntegerProperty(this, "modulesPerPage", DEFAULT_MODULES_PER_PAGE);


    private final ObservableList<LinearGradient> gradients = FXCollections.observableArrayList();
    /**
     * Total Number of Pages
     */
    private final IntegerProperty amountOfPages = new SimpleIntegerProperty(this, "amountOfPages");

    public GradientsModule() {
        super("LinearGradient", MaterialDesign.MDI_GRADIENT);
        gradients.addAll(LinearGradient.values());
        initBindings();
    }

    private void initBindings() {
        amountOfPages.bind(
                Bindings.createIntegerBinding(
                        this::calculateAmountOfPages, modulesPerPageProperty(), getGradients()
                )
        );
    }

    private int calculateAmountOfPages() {
        int amountOfModules = getGradients().size();
        int modulesPerPage = getModulesPerPage();
        // if all pages are completely full
        if (amountOfModules % modulesPerPage == 0) {
            return amountOfModules / modulesPerPage;
        } else {
            // if the last page is not full, round up to the next page
            return amountOfModules / modulesPerPage + 1;
        }
    }

    /**
     * Gets called whenever the currently displayed content is being switched to this module.
     *
     * @return content to be displayed in this module
     * @implNote if a module is being opened from the overview for the first time, it will get
     * initialized first by calling init(), afterwards activate() will be called.
     */
    @Override
    public Node activate() {
        return new GradientsView(this);
    }

    public int getModulesPerPage() {
        return modulesPerPage.get();
    }

    public IntegerProperty modulesPerPageProperty() {
        return modulesPerPage;
    }

    public void setModulesPerPage(int modulesPerPage) {
        this.modulesPerPage.set(modulesPerPage);
    }

    public int getAmountOfPages() {
        return amountOfPages.get();
    }

    public IntegerProperty amountOfPagesProperty() {
        return amountOfPages;
    }

    public void setAmountOfPages(int amountOfPages) {
        this.amountOfPages.set(amountOfPages);
    }

    public ObservableList<LinearGradient> getGradients() {
        return gradients;
    }
}
