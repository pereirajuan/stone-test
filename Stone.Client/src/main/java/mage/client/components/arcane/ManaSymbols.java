package mage.client.components.arcane;

import mage.client.cards.CardsStorage;
import mage.client.constants.Constants;
import mage.client.util.gui.BufferedImageBuilder;
import mage.client.util.gui.ImageResizeUtil;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class ManaSymbols {
	private static final Logger log = Logger.getLogger(ManaSymbols.class);
	static private final Map<String, Image> manaImages = new HashMap<String, Image>();
	static private final Map<String, Image> manaImagesOriginal = new HashMap<String, Image>();
	static private final Map<String, Image> setImages = new HashMap<String, Image>();
	static private Pattern replaceSymbolsPattern = Pattern.compile("\\{([^}/]*)/?([^}]*)\\}");
	static private boolean noManaSymbols = false;

	static public void loadImages() {
		String[] symbols = new String[]{"0", "1", "10", "11", "12", "15", "16", "2", "3", "4", "5", "6", "7", "8", "9", "B", "BG",
				"BR", "G", "GU", "GW", "R", "RG", "RW", "S", "T", "U", "UB", "UR", "W", "WB", "WU", "X" /*, "Y", "Z", "slash"*/};
		for (String symbol : symbols) {
			File file = new File(Constants.RESOURCE_PATH_MANA_LARGE + "/" + symbol + ".jpg");
			Rectangle r = new Rectangle(11, 11);
			try {
				Image image = UI.getImageIcon(file.getAbsolutePath()).getImage();
				BufferedImage resized = ImageResizeUtil.getResizedImage(BufferedImageBuilder.bufferImage(image, BufferedImage.TYPE_INT_ARGB), r);
				manaImages.put(symbol, resized);
			} catch (Exception e) {
				noManaSymbols = true;
			}
			file = new File(Constants.RESOURCE_PATH_MANA_MEDIUM + "/" + symbol + ".jpg");
			try {
				Image image = UI.getImageIcon(file.getAbsolutePath()).getImage();
				manaImagesOriginal.put(symbol, image);
			} catch (Exception e) {
			}
		}
		for (String set : CardsStorage.getSetCodes()) {
			String _set = set.equals("CON") ? "CFX" : set;
			File file = new File(Constants.RESOURCE_PATH_SET + _set + "-C.jpg");
			try {
				Image image = UI.getImageIcon(file.getAbsolutePath()).getImage();
				int width = image.getWidth(null);
				if (width > 21) {
					int h = image.getHeight(null);
					if (h > 0) {
						Rectangle r = new Rectangle(21, (int) (h * 21.0f / width));
						BufferedImage resized = ImageResizeUtil.getResizedImage(BufferedImageBuilder.bufferImage(image, BufferedImage.TYPE_INT_ARGB), r);
						setImages.put(set, resized);
					}
				} else {
					setImages.put(set, image);
				}
			} catch (Exception e) {
			}
			String[] codes = new String[]{"C", "U", "R", "M"};
			try {
				file = new File(Constants.RESOURCE_PATH_SET_SMALL);
				if (!file.exists()) {
					file.mkdirs();
				}

				for (String code : codes) {
					file = new File(Constants.RESOURCE_PATH_SET_SMALL + set + "-" + code + ".png");
					if (file.exists()) {
						continue;
					}
					file = new File(Constants.RESOURCE_PATH_SET + _set + "-" + code + ".jpg");
					Image image = UI.getImageIcon(file.getAbsolutePath()).getImage();
					try {
						int width = image.getWidth(null);
						int height = image.getHeight(null);
						if (height > 0) {
							int dx = 0;
							if (set.equals("M10") || set.equals("M11")) {
								dx = 6;
							}
							Rectangle r = new Rectangle(15 + dx, (int) (height * (15.0f + dx) / width));
							BufferedImage resized = ImageResizeUtil.getResizedImage(BufferedImageBuilder.bufferImage(image, BufferedImage.TYPE_INT_ARGB), r);
							File newFile = new File(Constants.RESOURCE_PATH_SET_SMALL + File.separator + _set + "-" + code + ".png");
							ImageIO.write(resized, "png", newFile);
						}
					} catch (Exception e) {
						if (file != null && file.exists()) {
							file.delete();
						}
					}
				}

			} catch (Exception e) {
			}
		}
	}

	static public Image getManaSymbolImage(String symbol) {
		return manaImagesOriginal.get(symbol);
	}

	static public Image getSetSymbolImage(String set) {
		return setImages.get(set);
	}

	static public void draw(Graphics g, String manaCost, int x, int y) {
		if (manaCost.length() == 0) return;
		manaCost = manaCost.replace("\\", "");
		manaCost = UI.getDisplayManaCost(manaCost);
		StringTokenizer tok = new StringTokenizer(manaCost, " ");
		while (tok.hasMoreTokens()) {
			String symbol = tok.nextToken().substring(0);
			Image image = manaImages.get(symbol);
			if (image == null) {
				//log.error("Symbol not recognized \"" + symbol + "\" in mana cost: " + manaCost);
				continue;
			}
			g.drawImage(image, x, y, null);
			x += symbol.length() > 2 ? 10 : 12; // slash.png is only 10 pixels wide.
		}
	}

	static public String getStringManaCost(List<String> manaCost) {
		StringBuilder sb = new StringBuilder();
		for (String s : manaCost) {
			sb.append(s);
		}
		return sb.toString().replace("{", "").replace("}", " ").trim();
	}

	static public int getWidth(String manaCost) {
		int width = 0;
		manaCost = manaCost.replace("\\", "");
		StringTokenizer tok = new StringTokenizer(manaCost, " ");
		while (tok.hasMoreTokens()) {
			String symbol = tok.nextToken().substring(0);
			width += symbol.length() > 2 ? 10 : 12; // slash.png is only 10 pixels wide.
		}
		return width;
	}

	static public synchronized String replaceSymbolsWithHTML(String value, boolean small) {
		if (noManaSymbols) {
			return value;
		} else {
			if (small)
				return replaceSymbolsPattern.matcher(value).replaceAll("<img src='file:plugins/images/symbols/small/$1$2.jpg' alt='$1$2' width=11 height=11>");
			else {
				value = value.replace("{slash}", "<img src='file:plugins/images/symbols/medium/slash.jpg' alt='slash' width=10 height=13>");
				return replaceSymbolsPattern.matcher(value).replaceAll("<img src='file:plugins/images/symbols/medium/$1$2.jpg' alt='$1$2' width=13 height=13>");
			}
		}
	}
}
