/*
 * Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of BetaSteward_at_googlemail.com.
 */

package mage.util;

import java.util.Iterator;
import java.util.UUID;

import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.costs.AlternativeCost;
import mage.abilities.costs.AlternativeCostImpl;
import mage.abilities.costs.VariableCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.costs.mana.ManaCost;
import mage.abilities.costs.mana.ManaCosts;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.costs.mana.VariableManaCost;
import mage.cards.Card;
import mage.constants.CardType;
import mage.game.Game;
import mage.game.permanent.token.Token;
import mage.util.functions.CopyFunction;
import mage.util.functions.CopyTokenFunction;



/**
 * @author nantuko
 */
public class CardUtil {

    static String numberStrings[] = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
                                      "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "ninteen", "twenty"};

    /**
     * Checks whether two cards share card types.
     *
     * @param card1
     * @param card2
     * @return
     */
    public static boolean shareTypes(Card card1, Card card2) {

        if (card1 == null || card2 == null) {
            throw new IllegalArgumentException("Params can't be null");
        }

        for (CardType type : card1.getCardType()) {
            if (card2.getCardType().contains(type)) {
                return true;
            }
        }

        return false;
    }
    /**
     * Checks whether two cards share card subtypes.
     *
     * @param card1
     * @param card2
     * @return
     */
    public static boolean shareSubtypes(Card card1, Card card2) {

        if (card1 == null || card2 == null) {
            throw new IllegalArgumentException("Params can't be null");
        }

        for (String subtype : card1.getSubtype()) {
            if (card2.getSubtype().contains(subtype)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Increase spell or ability cost to be paid.
     *
     * @param ability
     * @param increaseCount
     */
    public static void increaseCost(Ability ability, int increaseCount) {
        adjustCost(ability, -increaseCount);
        adjustAlternativeCosts(ability, -increaseCount);
    }

    /**
     * Reduces ability cost to be paid.
     *
     * @param ability
     * @param reduceCount
     */
    public static void reduceCost(Ability ability, int reduceCount) {
        adjustCost(ability, reduceCount);
        adjustAlternativeCosts(ability, reduceCount);
    }
    
    private static void adjustAlternativeCosts(Ability ability, int reduceCount) {
        for (AlternativeCost alternativeCost : ability.getAlternativeCosts()) {
            if (alternativeCost instanceof AlternativeCostImpl) {
                AlternativeCostImpl impl = (AlternativeCostImpl) alternativeCost;
                ManaCosts<ManaCost> adjustedCost = new ManaCostsImpl<ManaCost>();
                boolean updated = false;
                Iterator it = impl.iterator();
                while (it.hasNext()) {
                    Object cost = it.next();
                    if (cost instanceof ManaCosts) {
                        for (Object object : ((ManaCosts) cost)) {
                            if (object instanceof ManaCost) {
                                ManaCost manaCost = (ManaCost) object;
                                Mana mana = manaCost.getOptions().get(0);
                                int colorless = mana != null ? mana.getColorless() : 0;
                                if (!updated && colorless > 0) {
                                    if ((colorless - reduceCount) > 0) {
                                        int newColorless = colorless - reduceCount;
                                        it.remove();
                                        adjustedCost.add(new GenericManaCost(newColorless));
                                    }
                                    updated = true;
                                } else {
                                    adjustedCost.add(manaCost);
                                }
                            }
                        }
                    }
                }
                impl.add(adjustedCost);
            }
        }
    }

    /**
     * Adjusts spell or ability cost to be paid.
     *
     * @param spellAbility
     * @param reduceCount
     */
    public static void adjustCost(SpellAbility spellAbility, int reduceCount) {
        CardUtil.adjustCost((Ability) spellAbility, reduceCount);
        adjustAlternativeCosts(spellAbility, reduceCount);
    }

    /**
     * Adjusts ability cost to be paid.
     *
     * @param ability
     * @param reduceCount
     */
    private static void adjustCost(Ability ability, int reduceCount) {
        ManaCosts<ManaCost> previousCost = ability.getManaCostsToPay();
        ManaCosts<ManaCost> adjustedCost = new ManaCostsImpl<ManaCost>();
        boolean updated = false;
        for (ManaCost manaCost : previousCost) {
            Mana mana = manaCost.getOptions().get(0);
            int colorless = mana != null ? mana.getColorless() : 0;
            if (!updated && colorless > 0) {
                if ((colorless - reduceCount) > 0) {
                    int newColorless = colorless - reduceCount;
                    adjustedCost.add(new GenericManaCost(newColorless));
                }
                updated = true;
            } else {
                adjustedCost.add(manaCost);
            }
        }

        // for increasing spell cost effects
        if (!updated && reduceCount < 0) {
            adjustedCost.add(new GenericManaCost(-reduceCount));
        }

        ability.getManaCostsToPay().clear();
        ability.getManaCostsToPay().addAll(adjustedCost);
    }

    /**
     * Adjusts spell or ability cost to be paid by colored and generic mana.
     *
     * @param spellAbility
     * @param manaCostsToReduce costs to reduce
     */
    public static void adjustCost(SpellAbility spellAbility, ManaCosts<ManaCost> manaCostsToReduce) {
        ManaCosts<ManaCost> previousCost = spellAbility.getManaCostsToPay();
        ManaCosts<ManaCost> adjustedCost = new ManaCostsImpl<ManaCost>();
        // save X value (e.g. convoke ability)
        for (VariableCost vCost: previousCost.getVariableCosts()) {
            if (vCost instanceof VariableManaCost) {
                adjustedCost.add((VariableManaCost) vCost);
            }
        }


        Mana reduceMana = new Mana();
        for (ManaCost manaCost : manaCostsToReduce) {
            reduceMana.add(manaCost.getMana());
        }
        // subtract colored mana
        for (ManaCost newManaCost : previousCost) {
            Mana mana = newManaCost.getMana();
            if (mana.getColorless() > 0) {
                continue;
            }
            if (mana.getBlack() > 0 && reduceMana.getBlack() > 0) {
                if (reduceMana.getBlack() > mana.getBlack()) {
                    reduceMana.setBlack(reduceMana.getBlack()-mana.getBlack());
                    mana.setBlack(0);
                } else {
                    mana.setBlack(mana.getBlack()-reduceMana.getBlack());
                    reduceMana.setBlack(0);
                }
            }
            if (mana.getRed() > 0 && reduceMana.getRed() > 0) {
                if (reduceMana.getRed() > mana.getRed()) {
                    reduceMana.setRed(reduceMana.getRed()-mana.getRed());
                    mana.setRed(0);
                } else {
                    mana.setRed(mana.getRed()-reduceMana.getRed());
                    reduceMana.setRed(0);
                }
            }
            if (mana.getBlue() > 0 && reduceMana.getBlue() > 0) {
                if (reduceMana.getBlue() > mana.getBlue()) {
                    reduceMana.setBlue(reduceMana.getBlue()-mana.getBlue());
                    mana.setBlue(0);
                } else {
                    mana.setBlue(mana.getBlue()-reduceMana.getBlue());
                    reduceMana.setBlue(0);
                }
            }
            if (mana.getGreen() > 0 && reduceMana.getGreen() > 0) {
                if (reduceMana.getGreen() > mana.getGreen()) {
                    reduceMana.setGreen(reduceMana.getGreen()-mana.getGreen());
                    mana.setGreen(0);
                } else {
                    mana.setGreen(mana.getGreen()-reduceMana.getGreen());
                    reduceMana.setGreen(0);
                }
            }
            if (mana.getWhite() > 0 && reduceMana.getWhite() > 0) {
                if (reduceMana.getWhite() > mana.getWhite()) {
                    reduceMana.setWhite(reduceMana.getWhite()-mana.getWhite());
                    mana.setWhite(0);
                } else {
                    mana.setWhite(mana.getWhite()-reduceMana.getWhite());
                    reduceMana.setWhite(0);
                }
            }
            if (mana.count() > 0) {
                adjustedCost.add(newManaCost);
            }
        }
        // subtract colorless mana, use all mana that is left
        int reduceAmount = reduceMana.count();
        for (ManaCost newManaCost : previousCost) {
            Mana mana = newManaCost.getMana();
            if (mana.getColorless() == 0) {
                continue;
            }
            if (mana.getColorless() > 0 && reduceAmount > 0) {
                if (reduceAmount > mana.getColorless()) {
                    reduceAmount -= mana.getColorless();
                    mana.setColorless(0);
                } else {
                    mana.setColorless(mana.getColorless() - reduceAmount);
                    reduceAmount = 0;
                }
            }
            if (mana.count() > 0) {
                adjustedCost.add(0, new GenericManaCost(mana.count()));
            }
        }

        spellAbility.getManaCostsToPay().clear();
        spellAbility.getManaCostsToPay().addAll(adjustedCost);
    }
    /**
     * Returns function that copies params\abilities from one card to another.
     *
     * @param target
     */
    @Deprecated
    //public static CopyFunction copyTo(Card target) {
    private static CopyFunction copyTo(Card target) {
        return new CopyFunction(target);
    }

    /**
     * Returns function that copies params\abilities from one card to {@link Token}.
     *
     * @param target
     */
    public static CopyTokenFunction copyTo(Token target) {
        return new CopyTokenFunction(target);
    }

    public static boolean isPermanentCard ( Card card )  {
        boolean permanent = false;

        permanent |= card.getCardType().contains(CardType.ARTIFACT);
        permanent |= card.getCardType().contains(CardType.CREATURE);
        permanent |= card.getCardType().contains(CardType.ENCHANTMENT);
        permanent |= card.getCardType().contains(CardType.LAND);
        permanent |= card.getCardType().contains(CardType.PLANESWALKER);

        return permanent;
    }

    /**
     * Converts an integer number to string
     * Numbers > 20 will be returned as digits
     *
     */
    public static String numberToText(int number) {
        if (number >= 0 && number < 21) {
            return numberStrings[number];
        }
        return Integer.toString(number);
    }

    public static String numberToText(String number) {
        if (checkNumeric(number)) {
            return numberToText(Integer.parseInt(number));
        }
        return number;
    }

    public static String replaceSourceName(String message, String sourceName) {
        message = message.replace("{this}", sourceName);
        message = message.replace("{source}", sourceName);
        return message;
    }

    public static boolean checkNumeric(String s) {
        for(int i = 0; i < s.length(); i++) {
            if(!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates and saves a (card + zoneChangeCounter) specific exileId.
     *
     * @param game the current game
     * @param source source ability
     * @return the specific UUID
     */
    public static UUID getCardExileZoneId(Game game, Ability source) {
        String key = getCardZoneString("SourceExileZone", source.getSourceId(), game);
        UUID exileId = (UUID) game.getState().getValue(key);
        if (exileId == null) {
            exileId = UUID.randomUUID();
            game.getState().setValue(key, exileId);
        }
        return exileId;
    }

    /**
     * Creates a string from text + cardId and the zoneChangeCounter of the card (from cardId).
     * This string can be used to save and get values that must be specific to a permanent instance.
     * So they won't match, if a permanent was e.g. exiled and came back immediately.
     *
     * @param text short value to describe the value
     * @param cardId id of the card
     * @param game the game
     * @return
     */
    public static String getCardZoneString(String text, UUID cardId, Game game) {
        StringBuilder uniqueString = new StringBuilder();
        if (text != null) {
            uniqueString.append(text);
        }
        uniqueString.append(cardId);
        Card card = game.getCard(cardId);
        if (card != null) {
            uniqueString.append(card.getZoneChangeCounter());
        }
        return uniqueString.toString();
    }
}
