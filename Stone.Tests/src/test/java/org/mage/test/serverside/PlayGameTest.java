package org.mage.test.serverside;

import mage.Constants;
import mage.cards.Card;
import mage.cards.decks.Deck;
import mage.game.Game;
import mage.game.GameException;
import mage.game.GameOptions;
import mage.game.TwoPlayerDuel;
import mage.game.permanent.PermanentCard;
import mage.players.Player;
import mage.server.game.PlayerFactory;
import mage.sets.Sets;
import org.junit.Test;
import org.mage.test.serverside.base.MageTestBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author ayratn
 */
public class PlayGameTest extends MageTestBase {

	@Test
	public void playOneGame() throws GameException, FileNotFoundException, IllegalArgumentException {
		Game game = new TwoPlayerDuel(Constants.MultiplayerAttackOption.LEFT, Constants.RangeOfInfluence.ALL);

		Player computerA = createPlayer("ComputerA", "Computer - minimax hybrid");
//		Player computerA = createPlayer("ComputerA", "Computer - mad");
		Deck deck = Deck.load(Sets.loadDeck("RB Aggro.dck"));

		if (deck.getCards().size() < 40) {
			throw new IllegalArgumentException("Couldn't load deck, deck size=" + deck.getCards().size());
		}
		game.addPlayer(computerA, deck);
		game.loadCards(deck.getCards(), computerA.getId());

		Player computerB = createPlayer("ComputerB", "Computer - minimax hybrid");
//		Player computerB = createPlayer("ComputerB", "Computer - mad");
		Deck deck2 = Deck.load(Sets.loadDeck("RB Aggro.dck"));
		if (deck2.getCards().size() < 40) {
			throw new IllegalArgumentException("Couldn't load deck, deck size=" + deck2.getCards().size());
		}
		game.addPlayer(computerB, deck2);
		game.loadCards(deck2.getCards(), computerB.getId());

		parseScenario("scenario8.txt");
		game.cheat(computerA.getId(), commandsA);
		game.cheat(computerA.getId(), libraryCardsA, handCardsA, battlefieldCardsA, graveyardCardsA);
		game.cheat(computerB.getId(), commandsB);
		game.cheat(computerB.getId(), libraryCardsB, handCardsB, battlefieldCardsB, graveyardCardsB);

		//boolean testMode = false;
		boolean testMode = true;

		long t1 = System.nanoTime();
		GameOptions options = new GameOptions();
		options.testMode = true;
		game.start(computerA.getId(), options);
		long t2 = System.nanoTime();

		logger.info("Winner: " + game.getWinner());
		logger.info("Time: " + (t2 - t1) / 1000000 + " ms");
		/*if (!game.getWinner().equals("Player ComputerA is the winner")) {
			throw new RuntimeException("Lost :(");
		}*/
	}
}
