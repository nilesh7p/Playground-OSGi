package np.playground.desktopapp.core.utils;


import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Objects;

public class FXDesktopUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FXDesktopUtil.class);
    private static final String DEF_IMG = "question.png";

    public static String getStyleSheet(String styleSheetPath) {
        if (!Strings.isNullOrEmpty(styleSheetPath)) {
            URL resource = FXDesktopUtil.class.getResource("/css/" + styleSheetPath);
            if (Objects.nonNull(resource)) {
                return resource.toExternalForm();
            }
        }
        LOGGER.debug("Could not find Stylesheet {}", styleSheetPath);
        return "";
    }

    public static Image getImage(String imageFilePath) {
        if (!Strings.isNullOrEmpty(imageFilePath)) {
            URL resource = FXDesktopUtil.class.getResource("/images/" + imageFilePath);
            if (Objects.nonNull(resource)) {
                return new Image(resource.toExternalForm());
            }
        }
        LOGGER.debug("Could not find Image {}", imageFilePath);
        LOGGER.debug("Loading default Image {}", DEF_IMG);
        return getImage(DEF_IMG);
    }

    public static void logListContents(List list, Logger logger) {
        Logger localLogger = Objects.isNull(logger) ? LOGGER : logger;
        if (!isNullOrEmpty(list)) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                localLogger.trace("List[{}] = {}", i + 1, list.get(i));
            }
        }

    }

    private static boolean isNullOrEmpty(List list) {
        return Objects.isNull(list) || list.isEmpty();
    }

    private static boolean isNullOrEmpty(String str) {
        return Strings.isNullOrEmpty(str);
    }

    public static String convertToId(String name) {
        return CharMatcher.inRange('a', 'z')
                .or(CharMatcher.inRange('A', 'Z'))
                .or(CharMatcher.inRange('0', '9'))
                .or(CharMatcher.is(' '))
                .or(CharMatcher.is('-'))
                .retainFrom(name)
                .replace(' ', '-')
                .toLowerCase();
    }

    public static int calculateColumnsPerRow(int modulesPerPage) {
        LOGGER.trace("calculateColumnsPerRow for tiles/page {}",modulesPerPage);
        return modulesPerPage <= 3 ? modulesPerPage : (int) Math.ceil(Math.sqrt(modulesPerPage));
    }

    public static Background createBackground(String imageName) {
        return new Background(
                new BackgroundImage(getImage(imageName),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        new BackgroundPosition(Side.LEFT, 0.5, true, Side.TOP, 0.5, true),
                        new BackgroundSize(0.5, 0.5, true, true, false, true)));
    }
}
