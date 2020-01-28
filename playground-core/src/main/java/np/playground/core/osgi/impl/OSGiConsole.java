package np.playground.core.osgi.impl;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static np.playground.core.util.PlaygroundUtil.containsIgnoreCase;

@SuppressWarnings("unchecked")
public class OSGiConsole extends BorderPane {
    static Logger logger = LoggerFactory.getLogger(OSGiConsole.class);
    private final BundleContext context;

    BooleanProperty showing = new SimpleBooleanProperty(false);

    ListProperty<BundleK> bundles = new SimpleListProperty<>(FXCollections.observableArrayList());
    ListProperty<BundleK> systembundles = new SimpleListProperty<>(FXCollections.observableArrayList());

    public OSGiConsole(BundleContext context) {
        this.context = context;
        context.addBundleListener(this::refreshTable);
        setPrefWidth(600);
        setPrefHeight(300);
        TitledPane p = createPlaygroundPane();
        p.setAnimated(true);
        p.setText("Playground Bundles");
        TitledPane p2 = createSystemPane();
        p2.setAnimated(true);
        p2.setExpanded(false);
        p2.setText("Framework Bundles");
        Accordion accordion = new Accordion(
                p, p2
        );
        accordion.setExpandedPane(p);

        setCenter(accordion);
        showing.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                new Thread(() -> refreshTable(null)).start();
            }
        });
    }

    private TitledPane createSystemPane() {
        TableView<BundleK> tableView = createTableView();
        tableView.itemsProperty().bind(systembundles);
        TitledPane tp = new TitledPane();
        tp.setContent(tableView);
        tableView.setContextMenu(createMenu(tableView));
        tableView.setOnContextMenuRequested(event -> {
            AtomicReference<MenuItem> stop = new AtomicReference<>();
            ObservableList<MenuItem> items = tableView.getContextMenu().getItems();
            items.stream().filter(mi -> mi.getText().equals("Stop")).findFirst().ifPresent(mi -> {
                BundleK selectedItem = tableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    mi.setDisable(!selectedItem.getState().equals(Bundle.ACTIVE));
                    stop.set(mi);
                }
            });
            items.stream().filter(mi -> mi.getText().equals("Start")).findFirst()
                    .ifPresent(mi -> mi.setDisable(!stop.get().isDisable()));

        });
        return tp;
    }

    private TitledPane createPlaygroundPane() {
        TableView<BundleK> tableView = createTableView();
        tableView.itemsProperty().bind(bundles);
        TitledPane tp = new TitledPane();
        tp.setContent(tableView);
        tableView.setContextMenu(createMenu(tableView));
        tableView.setOnContextMenuRequested(event -> {
            AtomicReference<MenuItem> stop = new AtomicReference<>();
            ObservableList<MenuItem> items = tableView.getContextMenu().getItems();
            items.stream().filter(mi -> mi.getText().equals("Stop")).findFirst().ifPresent(mi -> {
                BundleK selectedItem = tableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    mi.setDisable(!selectedItem.getState().equals(Bundle.ACTIVE));
                    stop.set(mi);
                }
            });
            items.stream().filter(mi -> mi.getText().equals("Start")).findFirst().ifPresent(mi -> {
                mi.setDisable(!stop.get().isDisable());
            });

        });
        return tp;
    }

    private TableView<BundleK> createTableView() {
        TableView<BundleK> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("No rows to display"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<BundleK, Long> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("bundleId"));
        setColWidth(id, 75);

        TableColumn<BundleK, String> state = new TableColumn<>("State");
        state.setCellValueFactory(new PropertyValueFactory<>("stateDescription"));
        setColWidth(state, 120);

        TableColumn<BundleK, Integer> level = new TableColumn<>("Level");
        level.setCellValueFactory(new PropertyValueFactory<>("level"));
        setColWidth(level, 75);

        TableColumn<BundleK, String> description = new TableColumn<>("Name");
        description.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableView.getColumns().addAll(id, state, level, description);
        return tableView;
    }

    private ContextMenu createMenu(TableView<BundleK> tableView) {
        MenuItem stop = new MenuItem("Stop");
        stop.setOnAction(e -> stopBundle(tableView));
        MenuItem start = new MenuItem("Start");
        start.setOnAction(e -> startBundle(tableView));
        MenuItem uninstall = new MenuItem("Uninstall");
        uninstall.setOnAction(e -> uninstallBundle(tableView));
        MenuItem update = new MenuItem("Update");
        update.setOnAction(e -> updateBundle(tableView));
        MenuItem updateFrom = new MenuItem("Update from...");
        updateFrom.setOnAction(e -> updateFromBundle(tableView));
        MenuItem setLevel = new MenuItem("Set start level...");
        setLevel.setOnAction(e -> setLevelBundle(tableView));
        ContextMenu cm = new ContextMenu();
        cm.getItems().addAll(stop, start, uninstall, update, updateFrom, setLevel);
        return cm;
    }

    private void setColWidth(TableColumn<?, ?> column, double width) {
        column.setMinWidth(width);
        column.setMaxWidth(width);
        column.setPrefWidth(width);
    }

    private void refreshTable(BundleEvent event) {
        Bundle[] bundles = context.getBundles();
        if (Objects.nonNull(bundles)) {
            ArrayList<BundleK> bundleKS = new ArrayList<>();

            Arrays.asList(bundles).forEach(b -> bundleKS.add(toBundleK(b)));
            setBundles(FXCollections.observableArrayList(
                    bundleKS.stream()
                            .filter(b ->
                                    containsIgnoreCase("playground", b.getDescription())
                            )
                            .sorted(Comparator.comparing(BundleK::getDescription))
                            .collect(Collectors.toList())
                    )
            );
            setSystemBundles(FXCollections.observableArrayList(
                    bundleKS.stream()
                            .filter(b ->
                                    !containsIgnoreCase("playground", b.getDescription())
                            )
                            .sorted(Comparator.comparing(BundleK::getDescription))
                            .collect(Collectors.toList())
                    )
            );

        }
    }

    private BundleK toBundleK(Bundle b) {
        BundleK k = new BundleK();
        k.setBundle(b);
        k.setBundleId(b.getBundleId());
        k.setDescription(getDescription(b));
        BundleStartLevel adapt = b.adapt(BundleStartLevel.class);
        if (adapt != null) {
            k.setLevel(adapt.getStartLevel());
        } else {
            k.setLevel(-9999);
        }
        k.setStateDescription(getStateDescription(b));
        k.setState(b.getState());
        return k;
    }

    public final String getStateDescription(final Bundle $receiver) {
        String s;
        switch ($receiver.getState()) {
            case 32:
                s = "Active";
                break;
            case 2:
                s = "Installed";
                break;
            case 4:
                s = "Resolved";
                break;
            case 8:
                s = "Starting";
                break;
            case 16:
                s = "Stopping";
                break;
            case 1:
                s = "Uninstalled";
                break;
            default:
                s = "Unknown";
                break;
        }
        return s;
    }

    public final String getDescription(final Bundle $receiver) {
        String symbolicName;
        String location;
        String s;
        if ((s = (location = (symbolicName = $receiver.getHeaders().get("Bundle-Name")))) == null) {
            location = (s = (symbolicName = $receiver.getSymbolicName()));
        }
        if (s == null) {
            symbolicName = (location = $receiver.getLocation());
        }
        if (location == null) {
            symbolicName = "?";
        }
        final String name = symbolicName;
        return name + " | " + $receiver.getVersion();
    }

    private void setBundles(ObservableList<BundleK> bundles) {
        Platform.runLater(() -> this.bundles.set(bundles));
    }

    private void setSystemBundles(ObservableList<BundleK> bundles) {
        Platform.runLater(() -> this.systembundles.set(bundles));
    }

    public boolean isShowing() {
        return showing.get();
    }

    public BooleanProperty showingProperty() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing.set(showing);
    }

    public class BundleK {
        public Long bundleId;
        public String stateDescription;
        public Integer state;

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public Integer level;
        public String description;
        private Bundle bundle;

        public Bundle getBundle() {
            return bundle;
        }

        public void setBundle(Bundle bundle) {
            this.bundle = bundle;
        }

        public Long getBundleId() {
            return bundleId;
        }

        public void setBundleId(Long bundleId) {
            this.bundleId = bundleId;
        }

        public String getStateDescription() {
            return stateDescription;
        }

        public void setStateDescription(String stateDescription) {
            this.stateDescription = stateDescription;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "BundleK[" +
                    "bundleId=" + bundleId +
                    ", stateDescription='" + stateDescription + '\'' +
                    ", state=" + state +
                    ", description='" + description + '\'' +
                    ']';
        }
    }

    private void stopBundle(TableView<BundleK> tableView) {
        BundleK selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(selectedItem)) {
            Bundle bundle = selectedItem.getBundle();
            if (Objects.nonNull(bundle)) {
                try {
                    bundle.stop();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    private void startBundle(TableView<BundleK> tableView) {
        BundleK selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(selectedItem)) {
            Bundle bundle = selectedItem.getBundle();
            if (Objects.nonNull(bundle)) {
                try {
                    bundle.start();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    private void uninstallBundle(TableView<BundleK> tableView) {
        BundleK selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(selectedItem)) {
            Bundle bundle = selectedItem.getBundle();
            if (Objects.nonNull(bundle)) {
                try {
                    bundle.uninstall();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    private void updateBundle(TableView<BundleK> tableView) {
        BundleK selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(selectedItem)) {
            Bundle bundle = selectedItem.getBundle();
            if (Objects.nonNull(bundle)) {
                try {
                    bundle.update();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }

    }

    private void updateFromBundle(TableView<BundleK> tableView) {
       /* BundleK selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(selectedItem)) {
            Bundle bundle = selectedItem.getBundle();
            if (Objects.nonNull(bundle)) {
                try {
                    bundle.stop();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }*/

       /* item("Update from...").action {
            val result = chooseFile("Select file to replace ${selectedItem!!.symbolicName}", arrayOf(FileChooser.ExtensionFilter("OSGi Bundle Jar", "jar")))
            if (result.isNotEmpty()) selectedItem?.update(Files.newInputStream(result.first().toPath()))
        }*/

    }

    private void setLevelBundle(TableView<BundleK> tableView) {

        /*item("Set start level...").action {
            TextInputDialog("").showAndWait().ifPresent {
                selectedItem!!.bundleContext.bundle.adapt(BundleStartLevel::class.java).startLevel = it.toInt()
            }
        }*/
    }
}
