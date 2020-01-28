package np.playground.core.osgi.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class Activator implements BundleActivator {

    private BundleContext bundleContext;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    AppListener appListener;


    @Override
    public void start(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
        appListener = new AppListener(bundleContext);
        AppBundleListener appBundleListener = new AppBundleListener(bundleContext);
        AppFrameworkListener appFrameworkListener = new AppFrameworkListener(bundleContext);
        bundleContext.addServiceListener(appListener);
        logger.info("Registered app listener");
        bundleContext.addBundleListener(appBundleListener);
        logger.info("Registered appBundle listener");
        bundleContext.addFrameworkListener(appFrameworkListener);
        logger.info("Registered appFramework listener");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        logger.info("Stopping Playground");
        appListener.closeAllApps();
        //Platform.exit();
    }
}
