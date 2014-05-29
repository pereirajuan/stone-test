/**
 * GathererSymbols.java
 *
 * Created on 25.08.2010
 */

package org.mage.plugins.card.dl.sources;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.mage.plugins.card.dl.DownloadJob;
import static org.mage.plugins.card.dl.DownloadJob.fromURL;
import static org.mage.plugins.card.dl.DownloadJob.toFile;

/**
 * Used when we need to point to direct links to download resources from.
 *
 * @author noxx
 */
public class DirectLinksForDownload implements Iterable<DownloadJob> {

    private static final String backsideUrl = "http://upload.wikimedia.org/wikipedia/en/a/aa/Magic_the_gathering-card_back.jpg";

    private static final Map<String, String> directLinks = new LinkedHashMap<>();

    public static final String cardbackFilename = "cardback.jpg";
    public static final String tokenFrameFilename = "tokenFrame.png";

    static {
        directLinks.put(cardbackFilename, backsideUrl);
    }

    private static final String DEFAULT_IMAGES_PATH =  File.separator + "default";
    private static final File DEFAULT_OUT_DIR = new File("plugins" + File.separator + "images" + DEFAULT_IMAGES_PATH);
    public static File outDir  = DEFAULT_OUT_DIR;

    public DirectLinksForDownload(String path) {
        if (path == null) {
            useDefaultDir();
        } else {
            changeOutDir(path);
        }
    }

    @Override
    public Iterator<DownloadJob> iterator() {
        ArrayList<DownloadJob> jobs = new ArrayList<>();

        for (Map.Entry<String, String> url : directLinks.entrySet()) {
            File dst = new File(outDir, url.getKey());
            jobs.add(new DownloadJob(url.getKey(), fromURL(url.getValue()), toFile(dst)));
        }
        return jobs.iterator();
    }

    private void changeOutDir(String path) {
        File file = new File(path + DEFAULT_IMAGES_PATH);
        if (file.exists()) {
            outDir = file;
        } else {
            file.mkdirs();
            if (file.exists()) {
                outDir = file;
            }
        }
    }

    private void useDefaultDir() {
        outDir = DEFAULT_OUT_DIR;
    }
}
