package org.mage.plugins.card.constants;

import java.awt.Rectangle;
import java.io.File;

public final class Constants {

    public static final String RESOURCE_PATH_MANA_SMALL = File.separator + "symbols" + File.separator + "small";
    public static final String RESOURCE_PATH_MANA_LARGE = File.separator + "symbols" + File.separator + "large";
    public static final String RESOURCE_PATH_MANA_MEDIUM = File.separator + "symbols" + File.separator + "medium";
    public static final String RESOURCE_PATH_MANA_SVG = File.separator + "symbols" + File.separator + "svg";

    public static final String RESOURCE_PATH_SET = File.separator + "sets";
    public static final String RESOURCE_PATH_SET_SMALL = RESOURCE_PATH_SET + File.separator + "small";

    public static final Rectangle CARD_SIZE_FULL = new Rectangle(101, 149);
    public static final Rectangle THUMBNAIL_SIZE_FULL = new Rectangle(102, 146);

    public interface IO {
        String imageBaseDir = "plugins" + File.separator + "images";
        String IMAGE_PROPERTIES_FILE = "image.url.properties";
    }

    public static final String CARD_IMAGE_PATH_TEMPLATE = '.' + File.separator + "plugins" + File.separator + "images/{set}/{name}.{collector}.full.jpg";
}
