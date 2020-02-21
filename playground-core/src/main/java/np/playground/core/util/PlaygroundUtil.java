package np.playground.core.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

public class PlaygroundUtil {
    static Logger logger = LoggerFactory.getLogger(PlaygroundUtil.class);

    public static String DEFAULT_CONSOLE_ICON = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACf0lEQVQ4T2NkwAEyI6yXigmxSoOkX737/XT6iqPR2JQyIgsmBllWfv7598ibr78uh1kLnw+y41cAya859On+mqNvjMS4WPW4OVis56873g7TBzcgNcyqI8FLOPPthz+/bj75+dFOj0fx4aufr0EK5cXYRQ9d+nJfQ5adX4ifhW3+1ncz56w+WgaSAxsAsjnFT6SCm52Jj5uD6de0jW9Of3vPNNeQhf8ySP78n4+6XIL/krP8RUy//vjH9u3nv09rD3zK6V1wcDHYgDAvU7tYd5G1GnIcfC2LXyz4xiyT1SEoKM3OyAgOg5///z+teP/+KdffJ9NqYiUSVGTYnjP8/qfJaLX6O9gABwcDAW8ToTPP3vx+8eSrhH0fP78tMwNDwj9GRnAYMP3//+AvA8OCoo8fD8twvzioIcshvPzwa8sDBy58YASFtq4yh5WxOrfcvLUfU8rE1fdy/vvXFHXjwgKGfwwO4MBiYjiwTMMg4TsTU133ixsuiSECs8/e/Pro8t0fxxjrsxwOWGhz2X/+/vflu+PsPj4ikqwM//61x1w9f+DAsTMNYBdamTQs0TZ0YGBiqtzy5vlvIcufW3g5mcVPXP12kHIDQF7QVua0NFXnkifFC6dvfnt49e734/BAjLQVPX7j8Y+3xASilAirxNYz70zAgQjy4/9joZwMrEw37jz5JUFMNF5/+OPTot1vg9ZsPXUYbEBxgn1ssAPfFC6khPT9HeM8A1aBSyD5C78/6HEK/U+CJaSvP/99mrPpTQcoScOTckqodVeCp2DGu09/f+JKyuoy7PzCAixsC7a9nT571bEKeFKGZQxCmUmEm02Xl53ZBmtmQs+qxGZnADK/fDSODV+FAAAAAElFTkSuQmCC";

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
            throw new RuntimeException("null cannot be cast to non-null type");
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
            imageByte = Base64.getDecoder().decode(imageString);
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
            imageString = Base64.getEncoder().encodeToString(imageBytes);
            bos.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return imageString;
    }

    public static Image toFXImage(String base64Str) {
        assert base64Str != null;
        return SwingFXUtils.toFXImage(decodeBase64ToImage(base64Str), null);

    }
}
