package np.playground.core.osgi.impl;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import np.playground.core.App;
import np.playground.core.ApplicationProvider;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ApplicationListener implements ServiceListener {

    private ServiceTracker<ApplicationProvider, Object> tracker;
    private static App delegate;
    private static Stage realPrimaryStage;
    private BundleContext context;

    public ApplicationListener(BundleContext context) {
        try {
            this.context = context;
            tracker = new ServiceTracker<>(context, context.createFilter("(&(objectClass=" + ApplicationProvider.class.getName() + "))"), null);
            tracker.open();
            System.out.println("Application Provider Service tracker running.");

            Object[] providerServices = tracker.getServices();

            if (Objects.nonNull(providerServices)) {
                Arrays.stream(providerServices).forEach(appProvider -> {
                    try {
                        System.out.println("Trying to start delegate for " + ((ApplicationProvider) appProvider).getAppName());
                        startDelegateIfPossible((ApplicationProvider) appProvider);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (InvalidSyntaxException sy) {
            sy.printStackTrace();
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        switch (event.getType()) {
            case ServiceEvent.MODIFIED:
                System.out.println("MODIFIED");
                break;
            case ServiceEvent.MODIFIED_ENDMATCH:
                System.out.println("MODIFIED_ENDMATCH");
                break;
            case ServiceEvent.REGISTERED:
                System.out.println("REGISTERED");
                break;
            case ServiceEvent.UNREGISTERING:
                System.out.println("UNREGISTERING");
                break;
        }

        try {
            if (isApplicationProviderEvent(event)) {
                final Object service = context.getService(event.getServiceReference());
                if (service == null) {
                    throw new TypeCastException("null cannot be cast to non-null type");
                }
                final ApplicationProvider appProvider = (ApplicationProvider) service;
                if (event.getType() == ServiceEvent.REGISTERED) {
                    this.startDelegateIfPossible(appProvider);
                } else if (event.getType() == ServiceEvent.UNREGISTERING && isRunningApplication(appProvider.getApplication())) {
                    ApplicationListener.stopDelegate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isRunningApplication(App application) {
        final App delegate = ApplicationListener.delegate;
        if (delegate != null) {
            return delegate.equals(application);
        } else {
            return false;
        }
    }

    private boolean isApplicationProviderEvent(ServiceEvent event) {
        return "np.playground.core.ApplicationProvider".equalsIgnoreCase(OsgiSupport.getObjectClass(event));
    }

    private boolean getHasActiveApplication() {
        return ApplicationListener.delegate != null;
    }

    private void startDelegateIfPossible(ApplicationProvider appProvider) throws Exception {
        if (!getHasActiveApplication()) {
            startDelegate(appProvider);
        } else {
            System.out.println((Object) ("An application already running, not starting " + appProvider.getApplication() + '.'));
        }
    }

    private void startDelegate(ApplicationProvider provider) throws Exception {
        ApplicationListener.ensureFxRuntimeInitialized();
        ApplicationListener.delegate = provider.getApplication();
        if (ApplicationListener.realPrimaryStage == null) {
            System.out.print((Object) "Waiting for Primary Stage to be initialized");
            while (ApplicationListener.realPrimaryStage == null) {
                Thread.sleep(100L);
                System.out.print((Object) ".");
            }
            System.out.println((Object) "[Done]");
        }
        final App delegate = ApplicationListener.delegate;
        if (delegate == null) {
            throw new NullPointerException();
        }
        delegate.init();
        delegate.setOnCloseHandler(() -> {
            Bundle[] bundles = context.getBundles();
            if (Objects.nonNull(bundles)) {
                Arrays.stream(bundles).forEach(b -> {
                    if (b.getSymbolicName().equalsIgnoreCase(provider.getAppName())) {
                        try {
                            b.stop();
                        } catch (BundleException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        Platform.runLater(() -> {
            try {
                delegate.start(realPrimaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            realPrimaryStage.toFront();
        });
    }

    private static void ensureFxRuntimeInitialized() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        if (fxRuntimeNotInitialized()) {
            new Thread(() -> {
                ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(ApplicationListener.class.getClassLoader());
                Application.launch(ProxyApplication.class);
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }).start();
            System.out.print((Object) "Waiting for JavaFX Runtime Startup");
            do {
                Thread.sleep(100L);
                System.out.print((Object) ".");
            } while (fxRuntimeNotInitialized());
            System.out.println((Object) "[Done]");
        }
    }

    private static void stopDelegate() {
        Platform.runLater(() -> {
            try {
                delegate.stop();
                //realPrimaryStage.close();
                realPrimaryStage.getScene().setRoot(new BorderPane() {{
                    setCenter(new Label("No Playground Application running"));
                }});
                delegate = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private static void setRealPrimaryStage(Stage stage) {
        realPrimaryStage = stage;
    }

    private static boolean fxRuntimeNotInitialized() throws NoSuchFieldException, IllegalAccessException {
        Field initializedField = PlatformImpl.class.getDeclaredField("initialized");
        initializedField.setAccessible(true);
        AtomicBoolean initialized = (AtomicBoolean) initializedField.get(null);
        return !initialized.get();
    }

    public static final class ProxyApplication extends Application {
        public void start(final Stage stage) {
            stage.setOnCloseRequest(event -> stop());
            ApplicationListener.setRealPrimaryStage(stage);
            Platform.setImplicitExit(false);
        }

        public void stop() {
            System.out.println("Stopping Delegate");
            ApplicationListener.stopDelegate();
        }
    }
}

