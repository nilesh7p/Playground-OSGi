package np.playground.core.osgi.impl;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import np.playground.core.App;
import np.playground.core.AppProvider;
import np.playground.core.util.PlaygroundUtil;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static np.playground.core.util.PlaygroundUtil.getObjectClass;

public class AppListener implements ServiceListener {
    private final BundleContext bundleContext;
    private ServiceTracker<AppProvider, Object> tracker;
    private static Stage primaryStage;
    private Stage osgiConsoleStage;
    OSGiConsole console;

    static Logger log = LoggerFactory.getLogger(AppListener.class);

    public AppListener(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        log.info("Starting Tracker for [AppProvider]");
        startTracker();
        startAppsIfAvailable();
    }

    private void initConsole() {
        console = new OSGiConsole(bundleContext);
        osgiConsoleStage = new Stage();
        osgiConsoleStage.setOnCloseRequest(event -> {
            event.consume();
            console.setShowing(false);
            osgiConsoleStage.hide();
        });
        osgiConsoleStage.setScene(new Scene(console));
    }

    private void startTracker() {
        tracker = new ServiceTracker<>(bundleContext,
                Objects.requireNonNull(PlaygroundUtil.createObjectClassFilter(AppProvider.class.getName())),
                null);
        tracker.open();
    }

    private void startAppsIfAvailable() {
        assert tracker != null;
        Object[] services = tracker.getServices();
        if (Objects.nonNull(services)) {
            Arrays.asList(services).forEach(service -> {
                try {
                    start((AppProvider) service);
                } catch (Exception e) {
                    log.error("",e);
                }
            });
        } else {
            log.info("No Apps to Start");
        }
    }


    private void start(AppProvider provider) throws Exception {
        if (Objects.nonNull(provider)) {
            ensureFxRuntimeInitialized();
            if (primaryStage == null) {
                log.info("Waiting for Primary Stage");
                do {
                    Thread.sleep(100L);
                    System.out.print(". ");
                } while (primaryStage == null);
                log.info("[Done].");
            }
            App app = Objects.requireNonNull(provider.getApp());
            log.info("[" + app.getAppName() + "] init()");
            app.init();
            Platform.runLater(() -> {
                try {
                    log.info("[" + app.getAppName() + "] start()");
                    app.start(getNewStage(app.getAppName()));
                    if (osgiConsoleStage == null) {
                        initConsole();
                    }
                } catch (Exception e) {
                    log.error("",e);
                }
            });
        }
    }

    private Stage getNewStage(String appName) {
        Stage temp = new Stage();
        temp.setOnCloseRequest(e -> doOnClose(appName));
        temp.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCode.F8.equals(event.getCode())) {
                showConsole();
            }
        });
        return temp;
    }

    private void showConsole() {
        if (console.isShowing()) {
            osgiConsoleStage.hide();
            console.setShowing(false);
        } else {
            osgiConsoleStage.show();
            console.setShowing(true);
        }
    }

    private void doOnClose(String appName) {
        log.info("[" + appName + "] close()");
        Bundle[] bundles = bundleContext.getBundles();
        if (Objects.nonNull(bundles)) {
            Optional<Bundle> bundle = Arrays.stream(bundles).filter(b -> b.getSymbolicName().equals(appName)).findFirst();
            if (bundle.isPresent()) {
                try {
                    log.info("[" + appName + "] . BundleId [" + bundle.get().getBundleId() + "] . STOPPING");
                    bundle.get().stop();
                    log.info("[" + appName + "] . BundleId [" + bundle.get().getBundleId() + "] . STOPPED");
                } catch (BundleException be) {
                    log.error("",be);
                }
            }
        }
    }

    private Thread fxRuntimeInitializer() {
        return new Thread(() -> {
            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(AppListener.class.getClassLoader());
            Application.launch(ProxyApplication.class);
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        });
    }

    private boolean fxRuntimeNotInitialized() {
        try {
            Field initializedField = PlatformImpl.class.getDeclaredField("initialized");
            initializedField.setAccessible(true);
            AtomicBoolean initialized = (AtomicBoolean) initializedField.get(null);
            log.info("FX runtime init = " + initialized.get());
            return !initialized.get();
        } catch (Exception e) {
            log.error("",e);
        }
        return false;
    }

    private void ensureFxRuntimeInitialized() {
        if (fxRuntimeNotInitialized()) {
            try {
                fxRuntimeInitializer().start();
                log.info("Waiting for JavaFX Runtime Startup");
                do {
                    Thread.sleep(100L);
                    System.out.print(". ");
                } while (fxRuntimeNotInitialized());
                log.info("[Done]");
            } catch (Exception e) {
                log.error("",e);
            }
        }
    }

    @Override
    public void serviceChanged(ServiceEvent serviceEvent) {
        try {
            if (isApplicationProviderEvent(serviceEvent)) {
                final Object service = bundleContext.getService(serviceEvent.getServiceReference());
                if (service == null) {
                    throw new Exception("null cannot be cast to non-null type");
                }
                final AppProvider appProvider = (AppProvider) service;
                if (serviceEvent.getType() == ServiceEvent.REGISTERED) {
                    start(appProvider);
                } else if (serviceEvent.getType() == ServiceEvent.UNREGISTERING) {
                    stop(appProvider);
                }
            }
        } catch (Exception e) {
            log.error("",e);
        }
    }

    private boolean isApplicationProviderEvent(ServiceEvent serviceEvent) {
        return "np.playground.core.AppProvider".equalsIgnoreCase(getObjectClass(serviceEvent));
    }

    private void stop(AppProvider appProvider) {
        // log.info(" Stop App " + appProvider.getApp().getAppName());
    }

    public static final class ProxyApplication extends Application {
        @Override
        public void start(Stage primaryStage) {
            setRealPrimaryStage(primaryStage);
            Platform.setImplicitExit(false);
            log.info("Proxy Application Started");
        }
    }

    private static void setRealPrimaryStage(Stage stage) {
        primaryStage = stage;
    }
}
