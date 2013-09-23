package org.mage.plugins.card.info;

import mage.components.CardInfoPane;
import mage.constants.CardType;
import mage.utils.CardUtil;
import mage.utils.ThreadUtils;
import mage.view.CardView;
import mage.view.CounterView;
import mage.view.PermanentView;
import org.mage.card.arcane.ManaSymbols;
import org.mage.card.arcane.UI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import mage.constants.MageObjectType;

/**
 * Card info pane for displaying card rules.
 * Supports drawing mana symbols.
 *
 * @author nantuko
 */
public class CardInfoPaneImpl extends JEditorPane implements CardInfoPane {

    private CardView currentCard;
    private int type;

    public CardInfoPaneImpl() {
        UI.setHTMLEditorKit(this);
        setEditable(false);
        setBackground(Color.white);
    }

    @Override
    public void setCard(final CardView card, final Component container) {
        if (card == null || isCurrentCard(card)) {
            return;
        }
        currentCard = card;

        ThreadUtils.threadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!card.equals(currentCard)) {
                        return;
                    }

                    String manaCost = "";
                    for (String m : card.getManaCost()) {
                        manaCost += m;
                    }
                    String castingCost = UI.getDisplayManaCost(manaCost);
                    castingCost = ManaSymbols.replaceSymbolsWithHTML(castingCost, ManaSymbols.Type.CARD);

                    int symbolCount = 0;
                    int offset = 0;
                    while ((offset = castingCost.indexOf("<img", offset) + 1) != 0) {
                        symbolCount++;
                    }

                    List<String> rules = card.getRules();
                    List<String> rulings = new ArrayList<String>(rules);

                    if (card.getMageObjectType().equals(MageObjectType.PERMANENT)) {
                        if (card.getPairedCard() != null) {
                            rulings.add("<span color='green'><i>Paired with another creature</i></span>");
                        }
                    }
                    if (card.getMageObjectType().canHaveCounters()) {
                        List<CounterView> counters;
                        if (card instanceof PermanentView) {
                            counters = ((PermanentView) card).getCounters();
                        } else {
                            counters = ((CardView) card).getCounters();
                        }
                        int count = counters != null ? counters.size() : 0;
                        if (count > 0) {
                            StringBuilder sb = new StringBuilder();
                            int index = 0;
                            for (CounterView counter : counters) {
                                if (counter.getCount() > 0) {
                                    if (index == 0) {
                                        sb.append("<b>Counters:</b> ");
                                    } else {
                                        sb.append(", ");
                                    }
                                    sb.append(counter.getCount()).append("x<i>").append(counter.getName()).append("</i>");
                                    index++;
                                }
                            }
                            rulings.add(sb.toString());
                        }
                    }
                    if (card.getMageObjectType().isPermanent() && card instanceof PermanentView) {
                        int damage = ((PermanentView)card).getDamage();
                        if (damage > 0) {
                            rulings.add("<span color='red'><b>Damage dealt:</b> " + damage + "</span>");
                        }
                    }

                    int fontSize = 11;

                    String fontFamily = "tahoma";
                    /*if (prefs.fontFamily == CardFontFamily.arial)
                                            fontFamily = "arial";
                                        else if (prefs.fontFamily == CardFontFamily.verdana) {
                                            fontFamily = "verdana";
                                        }*/

                    final StringBuilder buffer = new StringBuilder(512);
                    buffer.append("<html><body style='font-family:");
                    buffer.append(fontFamily);
                    buffer.append(";font-size:");
                    buffer.append(fontSize);
                    buffer.append("pt;margin:0px 1px 0px 1px'>");
                    buffer.append("<table cellspacing=0 cellpadding=0 border=0 width='100%'>");
                    buffer.append("<tr><td valign='top'><b>");
                    buffer.append(card.getDisplayName());
                    buffer.append("</b></td><td align='right' valign='top' style='width:");
                    buffer.append(symbolCount * 11 + 1);
                    buffer.append("px'>");
                    if (!card.isSplitCard()) {
                        buffer.append(castingCost);
                    }
                    buffer.append("</td></tr></table>");
                    buffer.append("<table cellspacing=0 cellpadding=0 border=0 width='100%'><tr><td style='margin-left: 1px'>");
                    buffer.append(getTypes(card));
                    buffer.append("</td><td align='right'>");
                    switch (card.getRarity()) {
                        case RARE:
                            buffer.append("<b color='#FFBF00'>");
                            break;
                        case UNCOMMON:
                            buffer.append("<b color='silver'>");
                            break;
                        case COMMON:
                            buffer.append("<b color='black'>");
                            break;
                        case MYTHIC:
                            buffer.append("<b color='#D5330B'>");
                            break;
                    }
                    String rarity = card.getRarity().getCode();
                    if (card.getExpansionSetCode() != null) {
                        buffer.append(ManaSymbols.replaceSetCodeWithHTML(card.getExpansionSetCode().toUpperCase(), rarity));
                    }
                    buffer.append("</td></tr></table>");

                    String pt = "";
                    if (CardUtil.isCreature(card)) {
                        pt = card.getPower() + "/" + card.getToughness();
                    } else if (CardUtil.isPlaneswalker(card)) {
                        pt = card.getLoyalty().toString();
                    }

                    buffer.append("<table cellspacing=0 cellpadding=0 border=0 width='100%' valign='bottom'><tr><td><b>");
                    buffer.append(pt).append("</b></td>");
                    buffer.append("<td align='right'>");
                    if (!card.isControlledByOwner()) {
                        buffer.append("[only controlled] ");
                    }
                    buffer.append(card.getMageObjectType().toString()).append("</td>");
                    buffer.append("</tr></table>");

                    StringBuilder rule = new StringBuilder("<br/>");
                    if (card.isSplitCard()) {
                        rule.append("<table cellspacing=0 cellpadding=0 border=0 width='100%'>");
                        rule.append("<tr><td valign='top'><b>");
                        rule.append(card.getLeftSplitName());
                        rule.append("</b></td><td align='right' valign='top' style='width:");
                        rule.append(card.getLeftSplitCosts().getSymbols().size() * 11 + 1);
                        rule.append("px'>");
                        rule.append(card.getLeftSplitCosts().getText());
                        rule.append("</td></tr></table>");
                        for (String ruling : card.getLeftSplitRules()) {
                            if (ruling != null && !ruling.replace(".", "").trim().isEmpty()) {
                                rule.append("<p style='margin: 2px'>").append(ruling).append("</p>");
                            }
                        }
                        rule.append("<table cellspacing=0 cellpadding=0 border=0 width='100%'>");
                        rule.append("<tr><td valign='top'><b>");
                        rule.append(card.getRightSplitName());
                        rule.append("</b></td><td align='right' valign='top' style='width:");
                        rule.append(card.getRightSplitCosts().getSymbols().size() * 11 + 1);
                        rule.append("px'>");
                        rule.append(card.getRightSplitCosts().getText());
                        rule.append("</td></tr></table>");
                        for (String ruling : card.getRightSplitRules()) {
                            if (ruling != null && !ruling.replace(".", "").trim().isEmpty()) {
                                rule.append("<p style='margin: 2px'>").append(ruling).append("</p>");
                            }
                        }
                    }
                    if (rulings.size() > 0) {
                        for (String ruling : rulings) {
                            if (ruling != null && !ruling.replace(".", "").trim().isEmpty()) {
                                rule.append("<p style='margin: 2px'>").append(ruling).append("</p>");
                            }
                        }                        
                    }

                    String legal = rule.toString();
                    if (legal.length() > 0) {
// this 2 replaces were only done with the empty string, is it any longer needed? (LevelX2)
//                        legal = legal.replaceAll("#([^#]+)#", "<i>$1</i>");
//                        legal = legal.replaceAll("\\s*//\\s*", "<hr width='50%'>");
//                        legal = legal.replace("\r\n", "<div style='font-size:5pt'></div>");
                        legal = legal.replaceAll("\\{this\\}", card.getName());
                        legal = legal.replaceAll("\\{source\\}", card.getName());
                        buffer.append(ManaSymbols.replaceSymbolsWithHTML(legal, ManaSymbols.Type.CARD));
                    }

                    buffer.append("<br></body></html>");

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (!card.equals(currentCard)) {
                                return;
                            }
                            resizeTooltipIfNeeded(buffer, container);
                            setText(buffer.toString());
                            setCaretPosition(0);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void resizeTooltipIfNeeded(StringBuilder buffer, Component container) {
        if (container == null) {
            return;
        }
        int i = buffer.indexOf("</p>");
        int count = 0;
        while (i != -1) {
            count++;
            i = buffer.indexOf("</p>", i+1);
        }
        if (count > 5 && this.type == 0) {
            type = 1;
            container.setSize(
                org.mage.plugins.card.constants.Constants.TOOLTIP_WIDTH_MIN + org.mage.plugins.card.constants.Constants.TOOLTIP_BORDER_WIDTH,
                org.mage.plugins.card.constants.Constants.TOOLTIP_HEIGHT_MAX + org.mage.plugins.card.constants.Constants.TOOLTIP_BORDER_WIDTH
            );
            this.setSize(org.mage.plugins.card.constants.Constants.TOOLTIP_WIDTH_MIN, org.mage.plugins.card.constants.Constants.TOOLTIP_HEIGHT_MAX);
        } else if (count < 6 && type == 1) {
            type = 0;
            container.setSize(
                    org.mage.plugins.card.constants.Constants.TOOLTIP_WIDTH_MIN + org.mage.plugins.card.constants.Constants.TOOLTIP_BORDER_WIDTH,
                    org.mage.plugins.card.constants.Constants.TOOLTIP_HEIGHT_MIN + org.mage.plugins.card.constants.Constants.TOOLTIP_BORDER_WIDTH
            );
            this.setSize(org.mage.plugins.card.constants.Constants.TOOLTIP_WIDTH_MIN, org.mage.plugins.card.constants.Constants.TOOLTIP_HEIGHT_MIN);
        }
    }

    private String getTypes(CardView card) {
        String types = "";
        for (String superType : card.getSuperTypes()) {
            types += superType + " ";
        }
        for (CardType cardType : card.getCardTypes()) {
            types += cardType.toString() + " ";
        }
        if (card.getSubTypes().size() > 0) {
            types += "- ";
        }
        for (String subType : card.getSubTypes()) {
            types += subType + " ";
        }
        return types.trim();
    }

    public boolean isCurrentCard(CardView card) {
        return currentCard != null && card.equals(currentCard);
    }
}
