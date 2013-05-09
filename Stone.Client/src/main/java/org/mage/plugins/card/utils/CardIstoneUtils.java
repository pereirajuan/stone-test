package org.mage.plugins.card.utils;

import de.schlichtherle.truezip.file.TFile;
import java.util.HashMap;
import mage.client.constants.Constants;
import mage.client.dialog.PreferencesDialog;
import org.mage.plugins.card.images.CardInfo;
import org.mage.plugins.card.properties.SettingsManager;

public class CardImageUtils {

    private static HashMap<CardInfo, String> pathCache = new HashMap<CardInfo, String>();

    /**
     * Get path to image for specific card.
     * 
     * @param card
     *            card to get path for
     * @return String if image exists, else null
     */
    public static String getImagePath(CardInfo card) {
        String filePath;

        TFile file;
        if (card.isToken()) {
            if (pathCache.containsKey(card)) {
                return pathCache.get(card);
            }
            filePath = getTokenImagePath(card);
            file = new TFile(filePath);

            if (!file.exists()) {
                filePath = searchForCardImage(card);
                file = new TFile(filePath);
            }

            if (file.exists()) {
                pathCache.put(card, filePath);
            }
        } else {
            String useDefault = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_CARD_IMAGES_USE_DEFAULT, "true");
            String path = useDefault.equals("true") ? null : PreferencesDialog.getCachedValue(PreferencesDialog.KEY_CARD_IMAGES_PATH, null);
            filePath = getImagePath(card, path);
            file = new TFile(filePath);
        }

        if (file.exists()) {
            return filePath;
        } else {
            return null;
        }
    }

    private static String getTokenImagePath(CardInfo card) {
        String useDefault = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_CARD_IMAGES_USE_DEFAULT, "true");
        String path = useDefault.equals("true") ? null : PreferencesDialog.getCachedValue(PreferencesDialog.KEY_CARD_IMAGES_PATH, null);
        String filename = getImagePath(card, path);

        TFile file = new TFile(filename);
        if (!file.exists()) {
            CardInfo updated = new CardInfo(card);
            updated.setName(card.getName() + " 1");
            filename = getImagePath(updated, path);
            file = new TFile(filename);
            if (!file.exists()) {
                updated = new CardInfo(card);
                updated.setName(card.getName() + " 2");
                filename = getImagePath(updated, path);
            }
        }

        return filename;
    }

    private static String searchForCardImage(CardInfo card) {
        TFile file;
        String path;
        CardInfo c = new CardInfo(card);

        for (String set : SettingsManager.getIntance().getTokenLookupOrder()) {
            c.setSet(set);
            path = getTokenImagePath(c);
            file = new TFile(path);
            if (file.exists()) {
                pathCache.put(card, path);
                return path;
            }
        }
        return "";
    }

    public static String updateSet(String cardSet, boolean forUrl) {
        String set = cardSet.toLowerCase();
        if (set.equals("con")) {
            set = "cfx";
        }
        if (forUrl) {
            set = SettingsManager.getIntance().getSetNameReplacement(set);
        }
        return set;
    }

    private static String getImageDir(CardInfo card, String imagesPath) {
        if (card.getSet() == null) {
            return "";
        }
        String set = updateSet(card.getSet(), false).toUpperCase();
        String imagesDir = (imagesPath != null ? imagesPath :  Constants.IO.imageBaseDir);
        if (card.isToken()) {
            return buildTokenPath(imagesDir, set);
        } else {
            return buildPath(imagesDir, set);
        }
    }
    
    private static String buildTokenPath(String imagesDir, String set) {
        if (PreferencesDialog.isSaveImagesToZip()) {
            return imagesDir + TFile.separator + "TOK" + ".zip" + TFile.separator + set;
        } else {
            return imagesDir + TFile.separator + "TOK" + TFile.separator + set;
        }
    }

    private static String buildPath(String imagesDir, String set) {
        if (PreferencesDialog.isSaveImagesToZip()) {
            return imagesDir + TFile.separator + set + ".zip" + TFile.separator + set;
        } else {
            return imagesDir + TFile.separator + set;
        }
    }

    public static String getImagePath(CardInfo card, String imagesPath) {
        String imageDir = getImageDir(card, imagesPath);
        String imageName;

        String type = card.getType() != 0 ? " " + Integer.toString(card.getType()) : "";
        String name = card.getName().replace(":", "").replace("//", "-");

        if (card.getUsesVariousArt()) {
            imageName = name + "." + card.getCollectorId() + ".full.jpg";
        } else {
            imageName = name + type + ".full.jpg";
        }

        if (new TFile(imageDir).exists() && !new TFile(imageDir + TFile.separator + imageName).exists()) {
            for (String fileName : new TFile(imageDir).list()) {
                if (fileName.toLowerCase().equals(imageName.toLowerCase())) {
                    imageName = fileName;
                    break;
                }
            }
        }

        return imageDir + TFile.separator + imageName;
    }
}
