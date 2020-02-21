package patels.tools.desktop.view.controls.module;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patels.tools.desktop.FXDesktop;
import patels.tools.desktop.model.DesktopModule;

import static patels.tools.desktop.utils.FXDesktopUtil.convertToId;

public class Tile extends Control {
    private static final Logger LOGGER = LoggerFactory.getLogger(Tile.class.getName());
    private final FXDesktop fxDesktop;
    private ObjectProperty<DesktopModule> module;

    private final StringProperty name;
    private final ObjectProperty<Node> icon;

    public Tile(FXDesktop fxDesktop) {
        this.fxDesktop = fxDesktop;
        module = new SimpleObjectProperty<>(this, "module");
        name = new SimpleStringProperty(this, "name");
        icon = new SimpleObjectProperty<>(this, "icon");
        setupModuleListeners();
        setupEventHandlers();
        getStyleClass().add("tile-control");
    }

    private void setupModuleListeners() {
        module.addListener(observable -> {
            DesktopModule current = getModule();
            name.setValue(current.getName());
            icon.setValue(current.getIcon());

            // Sets the id with toString of module.
            // Adds 'tile-', replaces spaces with hyphens and sets letters to lowercase.
            // eg. Customer Management converts to tile-customer-management
            String tileId = convertToId("tile-" + current.getName());
            LOGGER.debug("Set Tile-ID of '" + getModule() + "' to: '" + tileId + "'");
            setId(tileId);
        });
    }

    private void setupEventHandlers() {
        setOnMouseClicked(event -> open());
    }

    public final void open() {
        fxDesktop.addModuleVisibleProperty().set(false);
        fxDesktop.openModule(getModule());
    }

    public DesktopModule getModule() {
        return module.get();
    }

    public ObjectProperty<DesktopModule> moduleProperty() {
        return module;
    }

    public void setModule(DesktopModule module) {
        this.module.set(module);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Node getIcon() {
        return icon.get();
    }

    public ObjectProperty<Node> iconProperty() {
        return icon;
    }

    public void setIcon(Node icon) {
        this.icon.set(icon);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TileSkin(this);
    }
}
