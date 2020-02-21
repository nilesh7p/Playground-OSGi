package patels.tools.desktop.model;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.FXDesktop;

import java.util.Objects;

public abstract class DesktopModule {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final String name;
    private final Ikon icon;
    private Image imgIcon;
    private FXDesktop desktop;

    public DesktopModule(String name, Ikon icon) {
        this.name = name;
        this.icon = icon;
    }

    /**
     * Gets called when there is no tab of a module and the module gets opened.
     * This can either be because the module is being opened for the first time, or the module's
     * Tab has been closed and the module is being re-opened.
     *
     * @param desktop the calling workbench object
     * @implNote Clicking on the Tile of a module in the add module view will cause the
     * corresponding module to get opened. When this module is being opened, with
     * <b>no</b> Tabs of this module existing yet, the desktop will first
     * call this method. Then, it will create a new Tab for this module in the
     * tab bar and open it, which causes {@link #activate()} to get called.<br>
     * When there is an open Tab of a module, opening the module will <b>not</b>
     * cause init(FXDesktop) to be called again. It will only be called if the
     * module's Tab was closed and the module is opened again.
     * @implSpec the implementor of this method <b>must</b> call {@code super(Workbench)} to ensure
     * correct working order.
     */
    public void init(FXDesktop desktop) {
        this.desktop = desktop;
    }

    /**
     * Gets called whenever the currently displayed content is being switched to this module.
     *
     * @return content to be displayed in this module
     * @implNote if a module is being opened from the overview for the first time, it will get
     * initialized first by calling init(), afterwards activate() will be called.
     */
    public abstract Node activate();

    /**
     * Gets called whenever this module is the currently displayed content and the content is being
     * switched to another module.
     *
     * @implNote Assuming Module 1 and Module 2, with both being already initialized and Module 1
     * being the currently displayed content.
     * When switching the content to Module 2, deactivate() gets called on Module 1,
     * followed by a call of activate() on Module 2.
     */
    public void deactivate() {
    }

    /**
     * Gets called when this module is explicitly being closed by the user in the toolbar.
     *
     * @return true if the module should be closed, false if the module should not be closed and the
     * closing process should be interrupted
     * @implNote <b>Lifecycle:</b> When {@link FXDesktop#closeModule(DesktopModule)} is being called
     * on an active module, {@link #deactivate()} will be called before {@link #destroy()}
     * is called. In case of an inactive module, only {@link #destroy()} will be called.
     * <br>
     * <b>Return behavior:</b> Assuming Module 1 and Module 2, with both being already
     * initialized and Module 2 being the currently active and displayed module.
     * When calling destroy() on Module 1: If true is returned, Module 2 will be removed
     * and Module 1 will be set as the active module. If false is returned, Module 2 will
     * not be removed and Module 1 will be set as the new active module, to enable the
     * user to react to the interrupted closing of the module.
     * @implSpec To implement an asynchronous, controlled closing of a module, execute the immediate
     * action (e.g. open a dialog) and define the asynchronous behavior in advance to call
     * {@link #close()} (e.g. define pressing "Yes" on the dialog to call {@link #close()}).
     * Then <b>return {@code false}</b>, which prevents this module from immediately getting
     * closed and causes this {@link DesktopModule} to get opened, so the user can react.
     * <br>
     * Example:
     * <pre class="code"><code class="java">
     *           getWorkbench().showDialog(WorkbenchDialog.builder("Confirmation", "Close Module?",
     *                                     WorkbenchDialog.Type.CONFIRMATION)
     *                         .blocking(true)
     *                         .onResult(buttonType -&lt; {
     *                           if (ButtonType.YES.equals(buttonType)) {
     *                             // yes was pressed
     *                             close();
     *                           }
     *                         }).build()
     *           );
     *           return false;
     *           </code></pre>
     */
    public boolean destroy() {
        return true;
    }

    public final FXDesktop getDesktop() {
        return desktop;
    }

    /**
     * Closes this module.
     *
     * @implNote Warning! This will <b>definitely</b> close this module!
     * It will <b>not</b> call {@link #destroy()} before closing it. If you need to clean up
     * before closing the module, call {@link #destroy()} before calling {@link #close()}.
     */
    public final void close() {
        /*getDesktop().completeModuleCloseable(this);*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns the name of this module.
     *
     * @return the name of this module.
     */
    public final String getName() {
        return Objects.isNull(name) ? "" : name;
    }

    /**
     * Returns the icon of this module as a {@link Node}.
     *
     * @return the icon of this module as a {@link Node}.
     */
    public final Node getIcon() {
        if (!Objects.isNull(icon))
            return FontIcon.of(icon);
        return new ImageView(imgIcon);
    }

    public void restoreWindow() {
    }

    @Override
    public boolean equals(Object obj) {
        LOGGER.info("Equals called on DesktopModule with obj {}",obj);
        if (obj instanceof DesktopModule) {
            LOGGER.info("Equals called on DesktopModule:: this.toString {} obj.toString {}", this.toString(), obj.toString());
            return this.toString().equals(obj.toString());
        }
        return false;
    }
}
