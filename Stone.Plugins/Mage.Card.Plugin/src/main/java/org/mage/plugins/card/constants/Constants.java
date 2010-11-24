package org.mage.plugins.card.constants;

import java.awt.Rectangle;
import java.io.File;

public class Constants {
	public static final String RESOURCE_PATH_MANA = IO.imageBaseDir + "symbols" + File.separator + "large";
	
	public static final Rectangle CARD_SIZE_FULL = new Rectangle(101, 149);
	
	public interface IO { 
		public static final String imageBaseDir = "plugins" + File.separator + "images" + File.separator; 
		public static final String IMAGE_PROPERTIES_FILE = "image.url.properties";
	}
	
	public static final String CARD_IMAGE_PATH_TEMPLATE = "." + File.separator + "plugins" + File.separator + "images/{set}/{name}.{collector}.full.jpg";
}
