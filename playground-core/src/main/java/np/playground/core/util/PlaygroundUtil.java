package np.playground.core.util;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public static BufferedImage decodeBase64ToImage(String imageString) {
        assert imageString != null;
        imageString = imageString.replace("data:image/png;base64,", "");

        BufferedImage image = null;
        byte[] imageByte;

        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return image;
    }


    public static String encodeImageToBase64(BufferedImage image, String type) {
        assert image != null;
        assert type != null;

        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);
            bos.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return imageString;
    }
}
