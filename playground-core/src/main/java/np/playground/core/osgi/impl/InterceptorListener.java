package np.playground.core.osgi.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

public class InterceptorListener implements ServiceListener {
    private BundleContext bundleContext;

    public InterceptorListener(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void serviceChanged(ServiceEvent serviceEvent) {

    }
}
