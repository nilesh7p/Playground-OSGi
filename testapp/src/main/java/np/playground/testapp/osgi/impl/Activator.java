package np.playground.testapp.osgi.impl;

import np.playground.core.App;
import np.playground.core.AppProvider;
import np.playground.testapp.TestApp;
import org.osgi.framework.BundleContext;


public class Activator implements AppProvider {

    private BundleContext bundleContext;
    private TestApp testApp;

    @Override
    public App getApp() {
        return testApp;
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
        testApp = new TestApp(bundleContext);
        registerApp(bundleContext);
    }
}
