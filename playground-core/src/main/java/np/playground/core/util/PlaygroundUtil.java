package np.playground.core.util;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PlaygroundUtil {
    static Logger logger = LoggerFactory.getLogger(PlaygroundUtil.class);

    public static Filter createObjectClassFilter(String s) {
        try {
            return FrameworkUtil.createFilter("(&(objectClass=" + s + "))");
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    public static String getObjectClass(final ServiceEvent serviceEvent) {
        final Object property = serviceEvent.getServiceReference().getProperty("objectClass");
        if (property == null) {
            throw new RuntimeException("null cannot be cast to non-null type kotlin.Array<kotlin.String>");
        }
        return ((String[]) property)[0];
    }

    public static boolean containsIgnoreCase(String s1, String s2) {
        if (Objects.nonNull(s1) && Objects.nonNull(s2)) {
            return s1.trim().toLowerCase().contains(s2.trim().toLowerCase())
                    || s2.trim().toLowerCase().contains(s1.trim().toLowerCase());
        }
        return false;
    }
}
