package np.playground.testapp.osgi.impl;

import np.playground.core.App;
import np.playground.core.ApplicationProvider;
import np.playground.testapp.TestApp;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends ApplicationProvider implements BundleActivator {
    App testApp;

    public void start(BundleContext bundleContext) throws Exception {
        testApp = new TestApp();
        registerApplication(bundleContext);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        System.out.println("Stopping " + getAppName());
        testApp.stop();
    }

    public App getApplication() {
        return testApp;
    }

    @Override
    public String getAppName() {
        return "testapp";
    }
}
