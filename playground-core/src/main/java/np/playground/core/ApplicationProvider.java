package np.playground.core;

import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public abstract class ApplicationProvider {
    public abstract App getApplication();

    public abstract String getAppName();

    protected void registerApplication(BundleContext context) {
        System.out.println("Register Provider for :: "+getAppName());
        context.registerService(ApplicationProvider.class, this, new Hashtable<String, String>());
    }
}
