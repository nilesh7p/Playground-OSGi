package np.playground.core;

import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public interface AppProvider {
    App getApp();

    default void registerApp(BundleContext context) {
        context.registerService(AppProvider.class, this, new Hashtable<String, String>());
    }
}
