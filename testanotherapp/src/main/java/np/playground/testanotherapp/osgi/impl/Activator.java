package np.playground.testanotherapp.osgi.impl;

import np.playground.core.App;
import np.playground.core.AppProvider;
import np.playground.testanotherapp.TestAnotherApp;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator, AppProvider {
    private TestAnotherApp app;

    @Override
    public App getApp() {
        return app;
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        app = new TestAnotherApp(bundleContext);
        registerApp(bundleContext);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

    }
}
