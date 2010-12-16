/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 * 
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 * 
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 * 
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */

package mage.sets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.ExpansionSet;
import mage.cards.decks.DeckCardLists;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class Sets extends HashMap<String, ExpansionSet> {

	private static final Sets fINSTANCE =  new Sets();
	private static Set<String> names;

	public static Sets getInstance() {
		return fINSTANCE;
	}

	private Sets() {
		names = new TreeSet<String>();
		this.addSet(AlaraReborn.getInstance());
		this.addSet(Conflux.getInstance());
		this.addSet(Magic2010.getInstance());
		this.addSet(Magic2011.getInstance());
		this.addSet(Planechase.getInstance());
        this.addSet(RavnicaCityOfGuilds.getInstance());
		this.addSet(RiseOfTheEldrazi.getInstance());
		this.addSet(ShardsOfAlara.getInstance());
		this.addSet(Tenth.getInstance());
		this.addSet(Worldwake.getInstance());
		this.addSet(Zendikar.getInstance());
	}

	private void addSet(ExpansionSet set) {
		this.put(set.getCode(), set);
		for (Card card: set.createCards()) {
			names.add(card.getName());
		}
	}

	public static Set<String> getCardNames() {
		return names;
	}

	public static String findCard(String name) {
		for (ExpansionSet set: fINSTANCE.values()) {
			String cardName = set.findCard(name);
			if (cardName != null)
				return cardName;
		}
		return null;
	}
	
	public static ExpansionSet findSet(String code) {
		for (ExpansionSet set: fINSTANCE.values()) {
			if (set.getCode().equals(code))
				return set;
		}
		return null;
	}

	public static DeckCardLists loadDeck(String file) throws FileNotFoundException {
		DeckCardLists deckList = new DeckCardLists();

		File f = new File(file);
		Scanner scanner = new Scanner(f);
		Pattern pattern = Pattern.compile("(SB:)?\\s*(\\d*)\\s*\\[([a-zA-Z0-9]{3}):(\\d*)\\].*");
		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.startsWith("#")) continue;
				Matcher m = pattern.matcher(line);
				if (m.matches()) {
					boolean sideboard = false;
					if (m.group(1) != null && m.group(1).equals("SB:"))
						sideboard = true;
					int count = Integer.parseInt(m.group(2));
					String setCode = m.group(3);
					int cardNum = Integer.parseInt(m.group(4));
					ExpansionSet set = Sets.findSet(setCode);
					String card = set.findCard(cardNum);
					for (int i = 0; i < count; i++) {
						if (!sideboard) {
							deckList.getCards().add(card);
						}
						else {
							deckList.getSideboard().add(card);
						}
					}
				}
				else if (line.startsWith("NAME:")) {
					deckList.setName(line.substring(5, line.length()));
				}
				else if (line.startsWith("AUTHOR:")) {
					deckList.setAuthor(line.substring(7, line.length()));
				}
			}
		}
		finally {
			scanner.close();
		}

		return deckList;
	}

	public static void saveDeck(String file, DeckCardLists deck) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(file);
		Map<String, Integer> cards = new HashMap<String, Integer>();
		Map<String, Integer> sideboard = new HashMap<String, Integer>();
		try {
			if (deck.getName() != null && deck.getName().length() > 0)
				out.println("NAME:" + deck.getName());
			if (deck.getAuthor() != null && deck.getAuthor().length() > 0)
				out.println("AUTHOR:" + deck.getAuthor());
			for (String cardClass: deck.getCards()) {
				if (cards.containsKey(cardClass)) {
					cards.put(cardClass, cards.get(cardClass) + 1);
				}
				else {
					cards.put(cardClass, 1);
				}
			}
			for (String cardClass: deck.getSideboard()) {
				if (sideboard.containsKey(cardClass)) {
					sideboard.put(cardClass, sideboard.get(cardClass) + 1);
				}
				else {
					sideboard.put(cardClass, 1);
				}
			}
			for (Map.Entry<String, Integer> entry: cards.entrySet()) {
				Card card = CardImpl.createCard(entry.getKey());
				if (card != null) {
					out.printf("%d [%s:%d] %s%n", entry.getValue(), card.getExpansionSetCode(), card.getCardNumber(), card.getName());
				}
			}
			for (Map.Entry<String, Integer> entry: sideboard.entrySet()) {
				Card card = CardImpl.createCard(entry.getKey());
				if (card != null) {
					out.printf("SB: %d [%s:%d] %s%n", entry.getValue(), card.getExpansionSetCode(), card.getCardNumber(), card.getName());
				}
			}
		}
		finally {
			out.close();
		}
	}
}
