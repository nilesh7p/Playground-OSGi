package np.playground.core.osgi.impl;

import javafx.application.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private BundleContext bundleContext;
    Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
        AppListener appListener = new AppListener(bundleContext);
        AppBundleListener appBundleListener = new AppBundleListener(bundleContext);
        AppFrameworkListener appFrameworkListener = new AppFrameworkListener(bundleContext);
        bundleContext.addServiceListener(appListener);
        log.info("Registered app listener");
        bundleContext.addBundleListener(appBundleListener);
        log.info("Registered appBundle listener");
        bundleContext.addFrameworkListener(appFrameworkListener);
        log.info("Registered appFramework listener");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        log.info("Stopping Playground");
        Platform.exit();
        System.exit(0);
    }
}
