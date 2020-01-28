package np.playground.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public interface AppProvider extends BundleActivator {
    App getApp();

    default void registerApp(BundleContext context) {
        context.registerService(AppProvider.class, this, new Hashtable<String, String>());
    }

    default void stop(BundleContext bundleContext) throws Exception {
        getApp().stop();
    }

}
