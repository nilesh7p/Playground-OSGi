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
import np.playground.core.util.Decorator;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static np.playground.core.util.PlaygroundUtil.getObjectClass;

public class AppListener implements ServiceListener {
    private final BundleContext bundleContext;
    private ServiceTracker<AppProvider, Object> tracker;
    private static Stage primaryStage;
    private Stage osgiConsoleStage;
    OSGiConsole console;
    Set<Stage> stages = new HashSet<>();

    static Logger logger = LoggerFactory.getLogger(AppListener.class);

    public AppListener(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        logger.info("Starting Tracker for [AppProvider]");
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
        osgiConsoleStage.setScene(new Scene(new Decorator(osgiConsoleStage, console)));
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
                    logger.error("", e);
                }
            });
        } else {
            logger.info("No Apps to Start");
        }
    }


    private void start(AppProvider provider) throws Exception {
        if (Objects.nonNull(provider)) {
            ensureFxRuntimeInitialized();
            if (primaryStage == null) {
                logger.info("Waiting for Primary Stage");
                do {
                    Thread.sleep(100L);
                    System.out.print(". ");
                } while (primaryStage == null);
                logger.info("[Done].");
            }
            App app = Objects.requireNonNull(provider.getApp());
            logger.info("[" + app.getAppName() + "] init()");
            app.init();
            Platform.runLater(() -> {
                try {
                    logger.info("[" + app.getAppName() + "] start()");
                    app.start(getNewStage(app.getAppName()));
                    if (osgiConsoleStage == null) {
                        initConsole();
                    }
                } catch (Exception e) {
                    logger.error("", e);
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
        stages.add(temp);
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
        logger.info("[" + appName + "] close()");
        Bundle[] bundles = bundleContext.getBundles();
        if (Objects.nonNull(bundles)) {
            Optional<Bundle> bundle = Arrays.stream(bundles).filter(b -> b.getSymbolicName().equals(appName)).findFirst();
            if (bundle.isPresent()) {
                try {
                    logger.info("[" + appName + "] . BundleId [" + bundle.get().getBundleId() + "] . STOPPING");
                    bundle.get().stop();
                    logger.info("[" + appName + "] . BundleId [" + bundle.get().getBundleId() + "] . STOPPED");
                } catch (BundleException be) {
                    logger.error("", be);
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
            logger.info("FX runtime init = " + initialized.get());
            return !initialized.get();
        } catch (Exception e) {
            logger.error("", e);
        }
        return false;
    }

    private void ensureFxRuntimeInitialized() {
        if (fxRuntimeNotInitialized()) {
            try {
                fxRuntimeInitializer().start();
                logger.info("Waiting for JavaFX Runtime Startup");
                do {
                    Thread.sleep(100L);
                    System.out.print(". ");
                } while (fxRuntimeNotInitialized());
                logger.info("[Done]");
            } catch (Exception e) {
                logger.error("", e);
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
            logger.error("", e);
        }
    }

    private boolean isApplicationProviderEvent(ServiceEvent serviceEvent) {
        return "np.playground.core.AppProvider".equalsIgnoreCase(getObjectClass(serviceEvent));
    }

    private void stop(AppProvider appProvider) {
        // logger.info(" Stop App " + appProvider.getApp().getAppName());
    }

    public static final class ProxyApplication extends Application {
        @Override
        public void start(Stage primaryStage) {
            setRealPrimaryStage(primaryStage);
            Platform.setImplicitExit(false);
            logger.info("Proxy Application Started");
        }
    }

    private static void setRealPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public void closeAllApps() {
        Platform.runLater(() -> {
            osgiConsoleStage.setOnCloseRequest(null);
            osgiConsoleStage.close();
            stages.forEach(Stage::close);
        });
    }
}
