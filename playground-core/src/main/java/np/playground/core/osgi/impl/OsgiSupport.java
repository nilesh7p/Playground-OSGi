package np.playground.core.osgi.impl;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceEvent;

public class OsgiSupport {


    public static String getObjectClass(final ServiceEvent $receiver) {

        final Object property = $receiver.getServiceReference().getProperty("objectClass");
        if (property == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<kotlin.String>");
        }
        return ((String[]) property)[0];
    }


    public static Bundle getFxBundle() {
        return FrameworkUtil.getBundle(Activator.class);
    }


    public static BundleContext getFxBundleContext() {
        return getFxBundle().getBundleContext();
    }


    public static Long getBundleId(final Class<?> classFromBundle) {

        try {
            final Bundle bundle = FrameworkUtil.getBundle(classFromBundle);
            return (bundle != null) ? bundle.getBundleId() : null;
        } catch (Exception ex) {
            //FX.Companion.getLog().log(Level.WARNING, "OSGi was on the classpath but no Framework did not respond correctly", ex);
            return null;
        }
    }

    /*private static final <T> Long getBundleId() {
        final int $i$f$getBundleId = 0;
        //Intrinsics.reifiedOperationMarker(4, "T");
        return getBundleId();
    }*/

}
