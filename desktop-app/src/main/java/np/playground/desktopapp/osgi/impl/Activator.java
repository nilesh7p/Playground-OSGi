package np.playground.desktopapp.osgi.impl;

import np.playground.core.App;
import np.playground.core.AppProvider;
import org.osgi.framework.BundleContext;

public class Activator implements AppProvider {
    @Override
    public App getApp() {
        return null;
    }

    @Override
    public void start(BundleContext context) throws Exception {

    }
}
