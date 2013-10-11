package org.mage.plugins.card;

import mage.cards.MagePermanent;
import mage.cards.action.ActionCallback;
import mage.constants.Rarity;
import mage.interfaces.plugin.CardPlugin;
import mage.utils.CardUtil;
import mage.view.CardView;
import mage.view.PermanentView;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.meta.Author;
import org.apache.log4j.Logger;
import org.mage.card.arcane.Animation;
import org.mage.card.arcane.CardPanel;
import org.mage.card.arcane.ManaSymbols;
import org.mage.plugins.card.constants.Constants;
import org.mage.plugins.card.dl.DownloadGui;
import org.mage.plugins.card.dl.DownloadJob;
import org.mage.plugins.card.dl.Downloader;
import org.mage.plugins.card.dl.sources.DirectLinksForDownload;
import org.mage.plugins.card.dl.sources.GathererSets;
import org.mage.plugins.card.dl.sources.GathererSymbols;
import org.mage.plugins.card.images.ImageCache;
import org.mage.plugins.card.info.CardInfoPaneImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * {@link CardPlugin} implementation.
 *
 * @author nantuko
 * @version 0.1 01.11.2010 Mage permanents. Sorting card layout.
 * @version 0.6 17.07.2011 #sortPermanents got option to display non-land permanents in one pile
 * @version 0.7 29.07.2011 face down cards support
 */
@PluginImplementation
@Author(name = "nantuko")
public class CardPluginImpl implements CardPlugin {

    private static final Logger log = Logger.getLogger(CardPluginImpl.class);

    private static final int GUTTER_Y = 15;
    private static final int GUTTER_X = 5;
    static final float EXTRA_CARD_SPACING_X = 0.04f;
    private static final float CARD_SPACING_Y = 0.03f;
    private static final float STACK_SPACING_X = 0.07f;
    private static final float STACK_SPACING_Y = 0.13f;

    private int landStackMax = 5;
    private int cardWidthMin = 50, cardWidthMax = Constants.CARD_SIZE_FULL.width;
    private boolean stackVertical = false;

    private int playAreaWidth, playAreaHeight;
    private int cardWidth, cardHeight;
    private int extraCardSpacingX, cardSpacingX, cardSpacingY;
    private int stackSpacingX, stackSpacingY;
    private List<Row> rows = new ArrayList<Row>();

    @Init
    public void init() {
    }

    @PluginLoaded
    public void newPlugin(CardPlugin plugin) {
    }

    @Override
    public String toString() {
        return "[Card plugin, version 0.7]";
    }

    @Override
    public MagePermanent getMagePermanent(PermanentView permanent, Dimension dimension, UUID gameId, ActionCallback callback, boolean canBeFoil, boolean loadImage) {
        boolean foil = canBeFoil && (new Random()).nextInt(5) == 0;
        CardPanel cardPanel = new CardPanel(permanent, gameId, loadImage, callback, foil);
        cardPanel.setCardBounds(0, 0, dimension.width, dimension.height);
        boolean implemented = !permanent.getRarity().equals(Rarity.NA);
        cardPanel.setShowCastingCost(implemented);
        return cardPanel;
    }

    @Override
    public MagePermanent getMageCard(CardView permanent, Dimension dimension, UUID gameId, ActionCallback callback, boolean canBeFoil, boolean loadImage) {
        boolean foil = canBeFoil && (new Random()).nextInt(5) == 0;
        CardPanel cardPanel = new CardPanel(permanent, gameId, loadImage, callback, foil);
        cardPanel.setCardBounds(0, 0, dimension.width, dimension.height);
        boolean implemented = !permanent.getRarity().equals(Rarity.NA);
        cardPanel.setShowCastingCost(implemented);
        return cardPanel;
    }

    @Override
    public int sortPermanents(Map<String, JComponent> ui, Collection<MagePermanent> permanents, Map<String, String> options) {
        //TODO: add caching
        //requires to find out is position have been changed that includes:
        //adding/removing permanents, type change

        if (ui == null) {
            throw new RuntimeException("Error: no components");
        }
        JComponent component = ui.get("battlefieldPanel");

        if (component == null) {
            throw new RuntimeException("Error: battlefieldPanel is missing");
        }

        JLayeredPane battlefieldPanel = (JLayeredPane) component;
        JComponent jPanel = ui.get("jPanel");

        Row allLands = new Row();

        outerLoop:
        //
        for (MagePermanent permanent : permanents) {
            if (!CardUtil.isLand(permanent) || CardUtil.isCreature(permanent)) {
                continue;
            }

            int insertIndex = -1;

            // Find lands with the same name.
            for (int i = 0, n = allLands.size(); i < n; i++) {
                Stack stack = allLands.get(i);
                MagePermanent firstPanel = stack.get(0);
                if (firstPanel.getOriginal().getName().equals(permanent.getOriginal().getName())) {
                    if (!empty(firstPanel.getLinks())) {
                        // Put this land to the left of lands with the same name and attachments.
                        insertIndex = i;
                        break;
                    }
                    if (!empty(permanent.getLinks()) || stack.size() == landStackMax) {
                        // If this land has attachments or the stack is full, put it to the right.
                        insertIndex = i + 1;
                        continue;
                    }
                    // Add to stack.
                    stack.add(0, permanent);
                    continue outerLoop;
                }
                if (insertIndex != -1) {
                    break;
                }
            }

            Stack stack = new Stack();
            stack.add(permanent);
            allLands.add(insertIndex == -1 ? allLands.size() : insertIndex, stack);
        }

        Row allCreatures = new Row(permanents, RowType.creature);
        Row allOthers = new Row(permanents, RowType.other);

        boolean othersOnTheRight = true;
        if (options != null && options.containsKey("nonLandPermanentsInOnePile")) {
            if (options.get("nonLandPermanentsInOnePile").equals("true")) {
                othersOnTheRight = false;
                   allCreatures.addAll(allOthers);
                allOthers.clear();
            }
        }

        cardWidth = cardWidthMax;
        Rectangle rect = battlefieldPanel.getVisibleRect();
        playAreaWidth = rect.width;
        playAreaHeight = rect.height;
        while (true) {
            rows.clear();
            cardHeight = Math.round(cardWidth * CardPanel.ASPECT_RATIO);
            extraCardSpacingX = Math.round(cardWidth * EXTRA_CARD_SPACING_X);
            cardSpacingX = cardHeight - cardWidth + extraCardSpacingX;
            cardSpacingY = Math.round(cardHeight * CARD_SPACING_Y);
            stackSpacingX = stackVertical ? 0 : Math.round(cardWidth * STACK_SPACING_X);
            stackSpacingY = Math.round(cardHeight * STACK_SPACING_Y);
            Row creatures = (Row) allCreatures.clone();
            Row lands = (Row) allLands.clone();
            Row others = (Row) allOthers.clone();
            // Wrap all creatures and lands.
            wrap(creatures, rows, -1);
            int afterCreaturesIndex = rows.size();
            wrap(lands, rows, afterCreaturesIndex);
            // Store the current rows and others.
            List<Row> storedRows = new ArrayList<Row>(rows.size());
            for (Row row : rows) {
                storedRows.add((Row) row.clone());
            }
            Row storedOthers = (Row) others.clone();
            // Fill in all rows with others.
            for (Row row : rows) {
                fillRow(others, rows, row);
            }

            // Stop if everything fits, otherwise revert back to the stored values.
            if (creatures.isEmpty() && lands.isEmpty() && others.isEmpty()) {
                break;
            }
            rows = storedRows;
            others = storedOthers;
            // Try to put others on their own row(s) and fill in the rest.
            wrap(others, rows, afterCreaturesIndex);
            for (Row row : rows) {
                fillRow(others, rows, row);
            }
            // If that still doesn't fit, scale down.
            if (creatures.isEmpty() && lands.isEmpty() && others.isEmpty()) {
                break;
            }
            //FIXME: -1 is too slow. why not binary search?
            cardWidth -= 3;
        }

        // Get size of all the rows.
        int x, y = GUTTER_Y;
        int maxRowWidth = 0;
        for (Row row : rows) {
            int rowBottom = 0;
            x = GUTTER_X;
            for (int stackIndex = 0, stackCount = row.size(); stackIndex < stackCount; stackIndex++) {
                Stack stack = row.get(stackIndex);
                rowBottom = Math.max(rowBottom, y + stack.getHeight());
                x += stack.getWidth();
            }
            y = rowBottom;
            maxRowWidth = Math.max(maxRowWidth, x);
        }

        // Position all card panels.
        y = GUTTER_Y;
        for (Row row : rows) {
            int rowBottom = 0;
            x = GUTTER_X;
            for (int stackIndex = 0, stackCount = row.size(); stackIndex < stackCount; stackIndex++) {
                Stack stack = row.get(stackIndex);
                // Align others to the right.
                if (othersOnTheRight && RowType.other.isType(stack.get(0))) {
                    x = playAreaWidth - GUTTER_X + extraCardSpacingX;
                    for (int i = stackIndex, n = row.size(); i < n; i++) {
                        x -= row.get(i).getWidth();
                    }
                }
                for (int panelIndex = 0, panelCount = stack.size(); panelIndex < panelCount; panelIndex++) {
                    MagePermanent panel = stack.get(panelIndex);
                    int stackPosition = panelCount - panelIndex - 1;
                    if (jPanel != null) {
                        jPanel.setComponentZOrder(panel, panelIndex);
                    }
                    int panelX = x + (stackPosition * stackSpacingX);
                    int panelY = y + (stackPosition * stackSpacingY);
                    try {
                        // may cause:
                        // java.lang.IllegalArgumentException: illegal component position 26 should be less then 26
                        battlefieldPanel.moveToFront(panel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    panel.setCardBounds(panelX, panelY, cardWidth, cardHeight);
                }
                rowBottom = Math.max(rowBottom, y + stack.getHeight());
                x += stack.getWidth();
            }
            y = rowBottom;
        }

        return y;
    }

    private boolean empty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private int wrap(Row sourceRow, List<Row> rows, int insertIndex) {
        // The cards are sure to fit (with vertical scrolling) at the minimum card width.
        boolean allowHeightOverflow = cardWidth == cardWidthMin;

        Row currentRow = new Row();
        for (int i = 0, n = sourceRow.size() - 1; i <= n; i++) {
            Stack stack = sourceRow.get(i);
            // If the row is not empty and this stack doesn't fit, add the row.
            int rowWidth = currentRow.getWidth();
            if (!currentRow.isEmpty() && rowWidth + stack.getWidth() > playAreaWidth) {
                // Stop processing if the row is too wide or tall.
                if (!allowHeightOverflow && rowWidth > playAreaWidth) {
                    break;
                }
                if (!allowHeightOverflow && getRowsHeight(rows) + sourceRow.getHeight() > playAreaHeight) {
                    break;
                }
                rows.add(insertIndex == -1 ? rows.size() : insertIndex, currentRow);
                currentRow = new Row();
            }
            currentRow.add(stack);
        }
        // Add the last row if it is not empty and it fits.
        if (!currentRow.isEmpty()) {
            int rowWidth = currentRow.getWidth();
            if (allowHeightOverflow || rowWidth <= playAreaWidth) {
                if (allowHeightOverflow || getRowsHeight(rows) + sourceRow.getHeight() <= playAreaHeight) {
                    rows.add(insertIndex == -1 ? rows.size() : insertIndex, currentRow);
                }
            }
        }
        // Remove the wrapped stacks from the source row.
        for (Row row : rows) {
            for (Stack stack : row) {
                sourceRow.remove(stack);
            }
        }
        return insertIndex;
    }

    private void fillRow(Row sourceRow, List<Row> rows, Row row) {
        int rowWidth = row.getWidth();
        while (!sourceRow.isEmpty()) {
            Stack stack = sourceRow.get(0);
            rowWidth += stack.getWidth();
            if (rowWidth > playAreaWidth) {
                break;
            }
            if (stack.getHeight() > row.getHeight()
                    && getRowsHeight(rows) - row.getHeight() + stack.getHeight() > playAreaHeight) {
                break;
            }
            row.add(sourceRow.remove(0));
        }
    }

    private int getRowsHeight(List<Row> rows) {
        int height = 0;
        for (Row row : rows) {
            height += row.getHeight();
        }
        return height - cardSpacingY + GUTTER_Y * 2;
    }

    private static enum RowType {
        land, creature, other;

        public boolean isType(MagePermanent card) {
            switch (this) {
                case land:
                    return CardUtil.isLand(card);
                case creature:
                    return CardUtil.isCreature(card);
                case other:
                    return !CardUtil.isLand(card) && !CardUtil.isCreature(card);
                default:
                    throw new RuntimeException("Unhandled type: " + this);
            }
        }
    }

    private class Row extends ArrayList<Stack> {
        private static final long serialVersionUID = 1L;

        public Row() {
            super(16);
        }

        public Row(Collection<MagePermanent> permanents, RowType type) {
            this();
            addAll(permanents, type);
        }

        private void addAll(Collection<MagePermanent> permanents, RowType type) {
            for (MagePermanent panel : permanents) {
                if (!type.isType(panel)) {
                    continue;
                }
                Stack stack = new Stack();
                stack.add(panel);
                add(stack);
            }
        }

        @Override
        public boolean addAll(Collection<? extends Stack> c) {
            boolean changed = super.addAll(c);
            c.clear();
            return changed;
        }

        private int getWidth() {
            if (isEmpty()) {
                return 0;
            }
            int width = 0;
            for (Stack stack : this) {
                width += stack.getWidth();
            }
            return width + GUTTER_X * 2 - extraCardSpacingX;
        }

        private int getHeight() {
            if (isEmpty()) {
                return 0;
            }
            int height = 0;
            for (Stack stack : this) {
                height = Math.max(height, stack.getHeight());
            }
            return height;
        }
    }

    private class Stack extends ArrayList<MagePermanent> {
        private static final long serialVersionUID = 1L;

        public Stack() {
            super(8);
        }

        private int getWidth() {
            return cardWidth + (size() - 1) * stackSpacingX + cardSpacingX;
        }

        private int getHeight() {
            return cardHeight + (size() - 1) * stackSpacingY + cardSpacingY;
        }
    }

    /**
     * Download various symbols (mana, tap, set).
     *
     * @param imagesPath Path to check in and store symbols to. Can be null, in such case default path should be used.
     */
    @Override
    public void downloadSymbols(String imagesPath) {
        final DownloadGui g = new DownloadGui(new Downloader());

        Iterable<DownloadJob> it = new GathererSymbols(imagesPath);

        for (DownloadJob job : it) {
            g.getDownloader().add(job);
        }

        it = new GathererSets(imagesPath);
        for(DownloadJob job:it) {
                g.getDownloader().add(job);
        }

        it = new DirectLinksForDownload(imagesPath);
        for(DownloadJob job:it) {
            g.getDownloader().add(job);
        }

        JDialog d = new JDialog((Frame) null, "Download pictures", false);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                g.getDownloader().dispose();
            }
        });
        d.setLayout(new BorderLayout());
        d.add(g);
        d.pack();
        d.setVisible(true);
    }

    @Override
    public Image getManaSymbolImage(String symbol) {
        return ManaSymbols.getManaSymbolImage(symbol);
    }

    @Override
    public void onAddCard(MagePermanent card, int count) {
        if (card != null) {
            Animation.showCard((CardPanel) card, count > 0 ? count : 1);
            try {
                while ((card).getAlpha() + 0.05f < 1) {
                    Thread.sleep(30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRemoveCard(MagePermanent card, int count) {
        if (card != null) {
            Animation.hideCard(card, count > 0 ? count : 1);
            try {
                while ((card).getAlpha() - 0.05f > 0) {
                    Thread.sleep(30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JComponent getCardInfoPane() {
        return new CardInfoPaneImpl();
    }

    @Override
    public BufferedImage getOriginalImage(CardView card) {
        return ImageCache.getImageOriginal(card);
    }
}
