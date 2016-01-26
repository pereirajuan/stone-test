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
package mage.cards.decks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mage.cards.Card;
import mage.cards.repository.CardInfo;
import mage.cards.repository.CardRepository;
import mage.constants.Rarity;
import org.apache.log4j.Logger;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class Constructed extends DeckValidator {

    private static final Logger logger = Logger.getLogger(DeckValidator.class);

    protected List<String> banned = new ArrayList<>();
    protected List<String> restricted = new ArrayList<>();
    protected List<String> setCodes = new ArrayList<>();
    protected List<Rarity> rarities = new ArrayList<>();

    public Constructed() {
        super("Constructed");
    }

    protected Constructed(String name) {
        super(name);
    }

    @Override
    public boolean validate(Deck deck) {
        logger.debug("DECK validate start: " + name + " deckname: " + deck.getName());
        boolean valid = true;
        //20091005 - 100.2a
        if (deck.getCards().size() < 60) {
            invalid.put("Deck", "Must contain at least 60 cards: has only " + deck.getCards().size() + " cards");
            valid = false;
        }
        //20130713 - 100.4a
        if (deck.getSideboard().size() > 15) {
            invalid.put("Sideboard", "Must contain no more than 15 cards : has " + deck.getSideboard().size() + " cards");
            valid = false;
        }

        List<String> basicLandNames = new ArrayList<>(Arrays.asList("Forest", "Island", "Mountain", "Swamp", "Plains", "Wastes",
                "Snow-Covered Forest", "Snow-Covered Island", "Snow-Covered Mountain", "Snow-Covered Swamp", "Snow-Covered Plains"));
        Map<String, Integer> counts = new HashMap<>();
        countCards(counts, deck.getCards());
        countCards(counts, deck.getSideboard());
        for (Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > 4) {
                if (!basicLandNames.contains(entry.getKey()) && !entry.getKey().equals("Relentless Rats") && !entry.getKey().equals("Shadowborn Apostle")) {
                    invalid.put(entry.getKey(), "Too many: " + entry.getValue());
                    valid = false;
                }
            }
        }
        for (String bannedCard : banned) {
            if (counts.containsKey(bannedCard)) {
                invalid.put(bannedCard, "Banned");
                valid = false;
            }
        }

        for (String restrictedCard : restricted) {
            if (counts.containsKey(restrictedCard)) {
                int count = counts.get(restrictedCard);
                if (count > 1) {
                    invalid.put(restrictedCard, "Restricted: " + count);
                    valid = false;
                }
            }
        }

        if (!rarities.isEmpty()) {
            for (Card card : deck.getCards()) {
                if (!rarities.contains(card.getRarity())) {
                    invalid.put(card.getName(), "Invalid rarity: " + card.getRarity());
                    valid = false;
                }
            }
            for (Card card : deck.getSideboard()) {
                if (!rarities.contains(card.getRarity())) {
                    invalid.put(card.getName(), "Invalid rarity: " + card.getRarity());
                    valid = false;
                }
            }
        }

        if (!setCodes.isEmpty()) {
            for (Card card : deck.getCards()) {
                if (!setCodes.contains(card.getExpansionSetCode())) {
                    // check if card is legal if taken from other set
                    boolean legal = false;
                    List<CardInfo> cardInfos = CardRepository.instance.findCards(card.getName());
                    for (CardInfo cardInfo : cardInfos) {
                        if (setCodes.contains(cardInfo.getSetCode())) {
                            legal = true;
                            break;
                        }
                    }
                    if (!legal && !invalid.containsKey(card.getName())) {
                        invalid.put(card.getName(), "Invalid set: " + card.getExpansionSetCode());
                        valid = false;
                    }
                }
            }
            for (Card card : deck.getSideboard()) {
                if (!setCodes.contains(card.getExpansionSetCode())) {
                    // check if card is legal if taken from other set
                    boolean legal = false;
                    List<CardInfo> cardInfos = CardRepository.instance.findCards(card.getName());
                    for (CardInfo cardInfo : cardInfos) {
                        if (setCodes.contains(cardInfo.getSetCode())) {
                            legal = true;
                            break;
                        }
                    }
                    if (!legal && !invalid.containsKey(card.getName())) {
                        invalid.put(card.getName(), "Invalid set: " + card.getExpansionSetCode());
                        valid = false;
                    }
                }
            }
        }
        logger.debug("DECK validate end: " + name + " deckname: " + deck.getName() + " invalids:" + invalid.size());
        return valid;
    }

}
