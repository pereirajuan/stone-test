package org.mage.card.arcane;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ScaledImagePanel extends JPanel {
    private static final long serialVersionUID = -1523279873208605664L;
    private volatile Image srcImage;

    private ScalingType scalingType = ScalingType.bilinear;
    private boolean scaleLarger;
    private MultipassType multiPassType = MultipassType.bilinear;

    public ScaledImagePanel () {
        super(false);
        setOpaque(false);
    }

    public void setImage(Image srcImage) {
        this.srcImage = srcImage;
    }

    public void clearImage () {
        srcImage = null;
        repaint();
    }

    public void setScalingMultiPassType (MultipassType multiPassType) {
        this.multiPassType = multiPassType;
    }

    public void setScalingType (ScalingType scalingType) {
        this.scalingType = scalingType;
    }

    public void setScaleLarger (boolean scaleLarger) {
        this.scaleLarger = scaleLarger;
    }

    public boolean hasImage () {
        return srcImage != null;
    }

    private ScalingInfo getScalingInfo () {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int srcWidth = srcImage.getWidth(null);
        int srcHeight = srcImage.getHeight(null);
        int targetWidth = srcWidth;
        int targetHeight = srcHeight;
        if (scaleLarger || srcWidth > panelWidth || srcHeight > panelHeight) {
            targetWidth = Math.round(panelHeight * (srcWidth / (float)srcHeight));
            if (targetWidth > panelWidth) {
                targetHeight = Math.round(panelWidth * (srcHeight / (float)srcWidth));
                targetWidth = panelWidth;
            } else {
                targetHeight = panelHeight;
            }
        }
        ScalingInfo info = new ScalingInfo();
        info.targetWidth = targetWidth;
        info.targetHeight = targetHeight;
        info.srcWidth = srcWidth;
        info.srcHeight = srcHeight;
        info.x = panelWidth / 2 - targetWidth / 2;
        info.y = panelHeight / 2 - targetHeight / 2;
        return info;
    }

    @Override
    public void paint (Graphics g) {
        if (srcImage == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D)g.create();
        ScalingInfo info = getScalingInfo();

        switch (scalingType) {
        case nearestNeighbor:
            scaleWithDrawImage(g2, info, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            break;
        case bilinear:
            scaleWithDrawImage(g2, info, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            break;
        case bicubic:
            scaleWithDrawImage(g2, info, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            break;
        case areaAveraging:
            scaleWithGetScaledInstance(g2, info, Image.SCALE_AREA_AVERAGING);
            break;
        case replicate:
            scaleWithGetScaledInstance(g2, info, Image.SCALE_REPLICATE);
            break;
        }
    }

    private void scaleWithGetScaledInstance (Graphics2D g2, ScalingInfo info, int hints) {
        Image scaledImage = srcImage.getScaledInstance(info.targetWidth, info.targetHeight, hints);
        g2.drawImage(scaledImage, info.x, info.y, null);
    }

    private void scaleWithDrawImage (Graphics2D g2, ScalingInfo info, Object hint) {
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);

        int tempDestWidth = info.srcWidth / 2, tempDestHeight = info.srcHeight / 2;
        if (tempDestWidth < info.targetWidth) {
            tempDestWidth = info.targetWidth;
        }
        if (tempDestHeight < info.targetHeight) {
            tempDestHeight = info.targetHeight;
        }

        // If not doing multipass or multipass only needs a single pass, just scale it once directly to the panel surface.
        if (multiPassType == MultipassType.none || (tempDestWidth == info.targetWidth && tempDestHeight == info.targetHeight)) {
            g2.drawImage(srcImage, info.x, info.y, info.targetWidth, info.targetHeight, null);
            return;
        }

        BufferedImage tempImage = new BufferedImage(tempDestWidth, tempDestHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2temp = tempImage.createGraphics();
        switch (multiPassType) {
        case nearestNeighbor:
            g2temp.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            break;
        case bilinear:
            g2temp.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            break;
        case bicubic:
            g2temp.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            break;
        }
        // Render first pass from image to temp.
        g2temp.drawImage(srcImage, 0, 0, tempDestWidth, tempDestHeight, null);
        // Render passes between the first and last pass.
        int tempSrcWidth = tempDestWidth;
        int tempSrcHeight = tempDestHeight;
        while (true) {
            if (tempDestWidth > info.targetWidth) {
                tempDestWidth = tempDestWidth / 2;
                if (tempDestWidth < info.targetWidth) {
                    tempDestWidth = info.targetWidth;
                }
            }

            if (tempDestHeight > info.targetHeight) {
                tempDestHeight = tempDestHeight / 2;
                if (tempDestHeight < info.targetHeight) {
                    tempDestHeight = info.targetHeight;
                }
            }

            if (tempDestWidth == info.targetWidth && tempDestHeight == info.targetHeight) {
                break;
            }

            g2temp.drawImage(tempImage, 0, 0, tempDestWidth, tempDestHeight, 0, 0, tempSrcWidth, tempSrcHeight, null);

            tempSrcWidth = tempDestWidth;
            tempSrcHeight = tempDestHeight;
        }
        g2temp.dispose();
        // Render last pass from temp to panel surface.
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
        g2.drawImage(tempImage, info.x, info.y, info.x + info.targetWidth, info.y + info.targetHeight, 0, 0, tempSrcWidth,
            tempSrcHeight, null);
    }

    public Image getSrcImage() {
        return srcImage;
    }

    private static class ScalingInfo {
        public int targetWidth;
        public int targetHeight;
        public int srcWidth;
        public int srcHeight;
        public int x;
        public int y;
    }

    public static enum MultipassType {
        none, nearestNeighbor, bilinear, bicubic
    }

    public static enum ScalingType {
        nearestNeighbor, replicate, bilinear, bicubic, areaAveraging
    }
}
