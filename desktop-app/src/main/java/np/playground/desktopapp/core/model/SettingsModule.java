package np.playground.desktopapp.core.model;

import javafx.scene.Node;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

public abstract class SettingsModule extends DesktopModule{
    public SettingsModule() {
        super("Settings", MaterialDesign.MDI_SETTINGS);
    }

}
