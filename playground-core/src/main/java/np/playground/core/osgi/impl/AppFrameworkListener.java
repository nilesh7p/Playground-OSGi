package np.playground.core.osgi.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

public class AppFrameworkListener implements FrameworkListener {
    private final BundleContext bundleContext;

    public AppFrameworkListener(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void frameworkEvent(FrameworkEvent frameworkEvent) {

    }
}
