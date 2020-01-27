package np.playground.core.osgi.impl;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
import java.util.stream.Collectors;

import static np.playground.core.util.PlaygroundUtil.containsIgnoreCase;

public class OSGiConsole extends BorderPane {
    static Logger log = LoggerFactory.getLogger(OSGiConsole.class);
    private final BundleContext context;

    BooleanProperty showing = new SimpleBooleanProperty(false);

    ListProperty<BundleK> bundles = new SimpleListProperty<>(FXCollections.observableArrayList());
    ListProperty<BundleK> systembundles = new SimpleListProperty<>(FXCollections.observableArrayList());

    @SuppressWarnings("unchecked")
    public OSGiConsole(BundleContext context) {
        this.context = context;
        context.addBundleListener(this::refreshTable);
        setPrefHeight(800);
        setPrefWidth(600);
        ScrollPane p = createPlaygroundPane();
        ScrollPane p2 = createSystemPane();
        VBox b = new VBox(
                p, p2
        );
        VBox.setVgrow(p, Priority.ALWAYS);
        VBox.setVgrow(p2, Priority.ALWAYS);
        setCenter(b);
        showing.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                new Thread(() -> refreshTable(null)).start();
            }
        });
    }

    private ScrollPane createSystemPane() {
        TableView<BundleK> tableView = createTableView();
        tableView.itemsProperty().bind(systembundles);
        //refreshTable(null);
        ScrollPane p = new ScrollPane(tableView);
        p.setFitToWidth(true);
        p.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return p;
    }

    private TableView<BundleK> createTableView() {
        TableView<BundleK> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("No rows to display"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<BundleK, Long> id = new TableColumn<>("bundleId");
        id.setCellValueFactory(new PropertyValueFactory<>("bundleId"));
        setColWidth(id, 75);

        TableColumn<BundleK, String> state = new TableColumn<>("stateDescription");
        state.setCellValueFactory(new PropertyValueFactory<>("stateDescription"));
        setColWidth(state, 120);

        TableColumn<BundleK, Integer> level = new TableColumn<>("level");
        level.setCellValueFactory(new PropertyValueFactory<>("level"));
        setColWidth(level, 75);

        TableColumn<BundleK, String> description = new TableColumn<>("description");
        description.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableView.getColumns().addAll(id, state, level, description);
        return tableView;
    }

    private ScrollPane createPlaygroundPane() {
        TableView<BundleK> tableView = createTableView();
        tableView.itemsProperty().bind(bundles);
        // refreshTable(null);
        ScrollPane p = new ScrollPane(tableView);
        p.setFitToWidth(true);
        p.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return p;
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
                                    containsIgnoreCase("playground", b.getDescription()) ||
                                            containsIgnoreCase("System Bundle", b.getDescription())
                            )
                            .sorted(Comparator.comparing(BundleK::getDescription))
                            .collect(Collectors.toList())
                    )
            );
            setSystemBundles(FXCollections.observableArrayList(
                    bundleKS.stream()
                            .filter(b ->
                                    !(containsIgnoreCase("playground", b.getDescription()) ||
                                            containsIgnoreCase("System Bundle", b.getDescription()))
                            )
                            .sorted(Comparator.comparing(BundleK::getDescription))
                            .collect(Collectors.toList())
                    )
            );

        }
    }

    private BundleK toBundleK(Bundle b) {
        BundleK k = new BundleK();
        k.setBundleId(b.getBundleId());
        k.setDescription(getDescription(b));
        BundleStartLevel adapt = b.adapt(BundleStartLevel.class);
        if (adapt != null) {
            k.setState(adapt.getStartLevel());
        } else {
            k.setState(-9999);
        }

        /*if (b.getHeaders() != null && !b.getHeaders().isEmpty()) {
            Enumeration<String> keys = b.getHeaders().keys();
            log.info("#####################################################");
            while (keys.hasMoreElements()) {
                String s = keys.nextElement();
                log.info("{} - {}", s, b.getHeaders().get(s));
            }
            log.info("#####################################################");
        }*/
        k.setStateDescription(getStateDescription(b));
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
        Platform.runLater(()->this.bundles.set(bundles));
    }

    private void setSystemBundles(ObservableList<BundleK> bundles) {
        Platform.runLater(()->this.systembundles.set(bundles));
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
        public String description;

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
}
