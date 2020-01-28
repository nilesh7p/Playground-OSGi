package np.playground.core.osgi.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppFrameworkListener implements FrameworkListener {
    private final BundleContext bundleContext;
    private static final Logger logger = LoggerFactory.getLogger(AppFrameworkListener.class);

    public AppFrameworkListener(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void frameworkEvent(FrameworkEvent frameworkEvent) {
        logger.info("[{}] - [{}]", typeDesc(frameworkEvent.getType()), frameworkEvent.getBundle().getSymbolicName());
    }

    private String typeDesc(int type) {
        switch (type) {
            case FrameworkEvent.STARTED:
                return "STARTED";
            case FrameworkEvent.ERROR:
                return "ERROR";
            case FrameworkEvent.WARNING:
                return "WARNING";
            case FrameworkEvent.INFO:
                return "INFO";
            case FrameworkEvent.PACKAGES_REFRESHED:
                return "PACKAGES_REFRESHED";
            case FrameworkEvent.STARTLEVEL_CHANGED:
                return "STARTLEVEL_CHANGED";
            case FrameworkEvent.STOPPED:
                return "STOPPED";
            case FrameworkEvent.STOPPED_BOOTCLASSPATH_MODIFIED:
                return "STOPPED_BOOTCLASSPATH_MODIFIED";
            case FrameworkEvent.STOPPED_UPDATE:
                return "STOPPED_UPDATE";
            case FrameworkEvent.WAIT_TIMEDOUT:
                return "WAIT_TIMEDOUT";
        }
        return "?";
    }
}
