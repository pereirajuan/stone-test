package org.mage.plugins.card.dl.sources;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mage.plugins.card.images.CardDownloadData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author North
 */
public class WizardCardsImageSource implements CardImageSource {

    private static CardImageSource instance;
    private static Map<String, String> setsAliases;
    private Map<String, Map<String, String>> sets;

    public static CardImageSource getInstance() {
        if (instance == null) {
            instance = new WizardCardsImageSource();
        }
        return instance;
    }

    public WizardCardsImageSource() {
        sets = new HashMap<String, Map<String, String>>();
        setsAliases = new HashMap<String, String>();
        setsAliases.put("THS", "theros/cig");
        setsAliases.put("M14", "magic2014coreset/cig");
        setsAliases.put("MMA", "modernmasters/cig");
        setsAliases.put("DGM", "dragonsmaze/cig");
        setsAliases.put("GTC", "gatecrash/cig");
        setsAliases.put("RTR", "returntoravnica/cig");
        setsAliases.put("M13", "magic2013/cig");
        setsAliases.put("AVR", "avacynrestored/cig");
        setsAliases.put("DKA", "darkascension/cig");
        setsAliases.put("ISD", "innistrad/cig");
        setsAliases.put("M12", "magic2012/cig");
        setsAliases.put("CMD", "commander/cig");
        setsAliases.put("NPH", "newphyrexia/spoiler");
        setsAliases.put("MBS", "mirrodinbesieged/spoiler");
        setsAliases.put("SOM", "scarsofmirrodin/spoiler");
        setsAliases.put("M11", "magic2011/spoiler");
        setsAliases.put("ROE", "riseoftheeldrazi/spoiler");
        setsAliases.put("WWK", "worldwake/spoiler");
        setsAliases.put("ZEN", "zendikar/spoiler");
        setsAliases.put("M10", "magic2010/spoiler");
        setsAliases.put("ARB", "alarareborn/spoiler");
        setsAliases.put("CON", "conflux/spoiler");
        setsAliases.put("ALA", "shardsofalara/spoiler");
        setsAliases.put("PC2", "planechase2012edition/cig");
    }

    private Map<String, String> getSetLinks(String cardSet) {
        Map<String, String> setLinks = new HashMap<String, String>();
        try {
            Document doc = Jsoup.connect("http://www.wizards.com/magic/tcg/article.aspx?x=mtg/tcg/" + setsAliases.get(cardSet)).get();
            Elements cardsImages = doc.select("img[height$=370]");
            for (int i = 0; i < cardsImages.size(); i++) {
                String cardName = normalizeName(cardsImages.get(i).attr("title"));
                if (cardName != null && !cardName.isEmpty()) {
                    if (cardName.equals("Forest") || cardName.equals("Swamp") || cardName.equals("Mountain") || cardName.equals("Island") || cardName.equals("Plains")) {
                        int landNumber = 1;
                        while (setLinks.get((cardName + landNumber).toLowerCase()) != null) {
                            landNumber++;
                        }
                        cardName += landNumber;
                    }
                    setLinks.put(cardName.toLowerCase(), cardsImages.get(i).attr("src"));
                } else {
                    setLinks.put(Integer.toString(i), cardsImages.get(i).attr("src"));
                }
            }

            cardsImages = doc.select("img[height$=470]");
            for (int i = 0; i < cardsImages.size(); i++) {
                String cardName = normalizeName(cardsImages.get(i).attr("title"));

                if (cardName != null && !cardName.isEmpty()) {
                    String[] cardNames = cardName.replace(")", "").split(" \\(");
                    for (String name : cardNames) {
                        setLinks.put(name.toLowerCase(), cardsImages.get(i).attr("src"));
                    }
                } else {
                    setLinks.put(Integer.toString(i), cardsImages.get(i).attr("src"));
                }
            }
        } catch (IOException ex) {
            System.out.println("Exception when parsing the wizards page: " + ex.getMessage());
        }
        return setLinks;
    }

    private String normalizeName(String name) {
        return name.replace("\u2014", "-").replace("\u2019", "'")
                .replace("\u00C6", "AE").replace("\u00E6", "ae")
                .replace("\u00C1", "A").replace("\u00E1", "a")
                .replace("\u00C2", "A").replace("\u00E2", "a")
                .replace("\u00D6", "O").replace("\u00F6", "o")
                .replace("\u00DB", "U").replace("\u00FB", "u")
                .replace("\u00DC", "U").replace("\u00FC", "u")
                .replace("\u00E9", "e").replace("&", "//")
                .replace("Hintreland Scourge", "Hinterland Scourge");
    }

    @Override
    public String generateURL(CardDownloadData card) throws Exception {
        Integer collectorId = card.getCollectorId();
        String cardSet = card.getSet();
        if (collectorId == null || cardSet == null) {
            throw new Exception("Wrong parameters for image: collector id: " + collectorId + ",card set: " + cardSet);
        }
        if (card.isFlippedSide()) { //doesn't support rotated images
            return null;
        }
        if (setsAliases.get(cardSet) != null) {
            Map<String, String> setLinks = sets.get(cardSet);
            if (setLinks == null) {
                setLinks = getSetLinks(cardSet);
                sets.put(cardSet, setLinks);
            }
            String link = setLinks.get(card.getDownloadName().toLowerCase());
            if (link == null) {
                if (setLinks.size() >= collectorId) {
                    link = setLinks.get(Integer.toString(collectorId - 1));
                } else {
                    link = setLinks.get(Integer.toString(collectorId - 21));
                    if (link != null) {
                        link = link.replace(Integer.toString(collectorId - 20), (Integer.toString(collectorId - 20) + "a"));
                    }
                }
            }
            if (link != null && !link.startsWith("http://")) {
                link = "http://www.wizards.com" + link;
            }
            return link;
        }
        return null;
    }

    @Override
    public String generateTokenUrl(CardDownloadData card) {
        return null;
    }

    @Override
    public Float getAverageSize() {
        return 60.0f;
    }
}
