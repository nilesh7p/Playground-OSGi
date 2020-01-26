package np.playground.core.osgi.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

public class ViewListener implements ServiceListener {
    private BundleContext bundleContext;

    public ViewListener(BundleContext bundleContext) {

        this.bundleContext = bundleContext;
    }

    @Override
    public void serviceChanged(ServiceEvent serviceEvent) {

    }
}
