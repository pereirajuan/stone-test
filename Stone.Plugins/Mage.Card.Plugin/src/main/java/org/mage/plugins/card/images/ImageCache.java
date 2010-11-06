package org.mage.plugins.card.images;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import mage.view.CardView;

import org.apache.log4j.Logger;
import org.mage.plugins.card.utils.CardImageUtils;

import com.google.common.base.Function;
import com.google.common.collect.ComputationException;
import com.google.common.collect.MapMaker;
import com.mortennobel.imagescaling.ResampleOp;

/**
 * This class stores ALL card images in a cache with soft values. this means
 * that the images may be collected when they are not needed any more, but will
 * be kept as long as possible.
 * 
 * Key format: "<cardname>#<setname>#<collectorID>#<param>"
 * 
 * where param is:
 * 
 * <ul>
 * <li>#Normal: request for unrotated image</li>
 * <li>#Tapped: request for rotated image</li>
 * <li>#Cropped: request for cropped image that is used for Shandalar like card
 * look</li>
 * </ul>
 */
public class ImageCache {

	private static final Logger log = Logger.getLogger(ImageCache.class);

	private static final Map<String, BufferedImage> imageCache;

	/**
	 * Common pattern for keys.
	 * Format: "<cardname>#<setname>#<collectorID>"
	 */
	private static final Pattern KEY_PATTERN = Pattern.compile("(.*)#(.*)#(.*)");
	
	static {
		imageCache = new MapMaker().softValues().makeComputingMap(new Function<String, BufferedImage>() {
			public BufferedImage apply(String key) {
				try {
					Matcher m = KEY_PATTERN.matcher(key);

					if (m.matches()) {
						String name = m.group(1);
						String set = m.group(2);
						Integer collectorId = Integer.parseInt(m.group(3));

						CardInfo info = new CardInfo(name, set, collectorId);
						
						if (collectorId == 0) info.isToken = true;
						String path = CardImageUtils.getImagePath(info);
						if (path == null) return null;
						File file = new File(path);

						BufferedImage image = loadImage(file);
						return image;
					} else {
						throw new RuntimeException(
								"Requested image doesn't fit the requirement for key (<cardname>#<setname>#<collectorID>): " + key);
					}
				} catch (Exception ex) {
					if (ex instanceof ComputationException)
						throw (ComputationException) ex;
					else
						throw new ComputationException(ex);
				}
			}
		});
	}

	public static BufferedImage getImageOriginal(CardView card) {
		String key = getKey(card);
		log.debug("#key: " + key);
		return getImage(key);
	}

	/**
	 * Returns the Image corresponding to the key
	 */
	private static BufferedImage getImage(String key) {
		try {
			BufferedImage image = imageCache.get(key);
			return image;
		} catch (NullPointerException ex) {
			// unfortunately NullOutputException, thrown when apply() returns
			// null, is not public
			// NullOutputException is a subclass of NullPointerException
			// legitimate, happens when a card has no image
			return null;
		} catch (ComputationException ex) {
			if (ex.getCause() instanceof NullPointerException)
				return null;
			log.error(ex,ex);
			return null;
		}
	}

	/**
	 * Returns the map key for a card, without any suffixes for the image size.
	 */
	private static String getKey(CardView card) {
		String set = card.getExpansionSetCode();
		String key = card.getName() + "#" + set + "#" + String.valueOf(card.getCardNumber());

		return key;
	}

	/**
	 * Load image from file
	 * 
	 * @param file
	 *            file to load image from
	 * @return {@link BufferedImage}
	 */
	public static BufferedImage loadImage(File file) {
		BufferedImage image = null;
		if (!file.exists()) {
			return null;
		}
		try {
			image = ImageIO.read(file);
		} catch (Exception e) {
			log.error(e, e);
		}

		return image;
	}

	/**
	 * Returns an image scaled to the size given
	 */
	/*private static BufferedImage getNormalSizeImage(BufferedImage original) {
		int srcWidth = original.getWidth();
		int srcHeight = original.getHeight();
		
		int tgtWidth = SettingsManager.getManager().getCardSize().width;
		int tgtHeight = SettingsManager.getManager().getCardSize().height;
		
		if (srcWidth == tgtWidth && srcHeight == tgtHeight)
			return original;

		ResampleOp resampleOp = new ResampleOp(tgtWidth, tgtHeight);
		BufferedImage image = resampleOp.filter(original, null);
		return image;
	}*/

	/**
	 * Returns an image scaled to the size appropriate for the card picture
	 * panel For future use.
	 */
	private static BufferedImage getFullSizeImage(BufferedImage original, double scale) {
		if (scale == 1)
			return original;
		ResampleOp resampleOp = new ResampleOp((int) (original.getWidth() * scale), (int) (original.getHeight() * scale));
		BufferedImage image = resampleOp.filter(original, null);
		return image;
	}

	/**
	 * Returns an image scaled to the size appropriate for the card picture
	 * panel
	 */
	private static BufferedImage getResizedImage(BufferedImage original, Rectangle sizeNeed) {
		ResampleOp resampleOp = new ResampleOp(sizeNeed.width, sizeNeed.height);
		BufferedImage image = resampleOp.filter(original, null);
		return image;
	}

	/**
	 * Returns the image appropriate to display the card in the picture panel
	 */
	public static BufferedImage getImage(CardView card, int width, int height) {
		String key = getKey(card);
		BufferedImage original = getImage(key);
		if (original == null)
			return null;

		double scale = Math.min((double) width / original.getWidth(), (double) height / original.getHeight());
		if (scale > 1)
			scale = 1;

		return getFullSizeImage(original, scale);
	}
}
