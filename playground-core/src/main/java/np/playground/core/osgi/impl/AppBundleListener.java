package np.playground.core.osgi.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppBundleListener implements BundleListener {
    private static final Logger logger = LoggerFactory.getLogger(AppBundleListener.class);
    private final BundleContext bundleContext;

    public AppBundleListener(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void bundleChanged(BundleEvent bundleEvent) {
        logger.info("[{}] - [{}]", typeDesc(bundleEvent.getType()), bundleEvent.getBundle().getSymbolicName());
    }

    private String typeDesc(int type) {
        switch (type) {
            case BundleEvent.INSTALLED:
                return "INSTALLED";
            case BundleEvent.RESOLVED:
                return "RESOLVED";
            case BundleEvent.LAZY_ACTIVATION:
                return "LAZY_ACTIVATION";
            case BundleEvent.STARTING:
                return "STARTING";
            case BundleEvent.STARTED:
                return "STARTED";
            case BundleEvent.STOPPING:
                return "STOPPING";
            case BundleEvent.STOPPED:
                return "STOPPED";
            case BundleEvent.UPDATED:
                return "UPDATED";
            case BundleEvent.UNRESOLVED:
                return "UNRESOLVED";
            case BundleEvent.UNINSTALLED:
                return "UNINSTALLED";

        }
        return "?";
    }
}
