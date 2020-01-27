package np.playground.core.osgi.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

public class AppBundleListener implements BundleListener {
    private final BundleContext bundleContext;

    public AppBundleListener(BundleContext bundleContext) {

        this.bundleContext = bundleContext;
    }

    @Override
    public void bundleChanged(BundleEvent bundleEvent) {

    }
}
