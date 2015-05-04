package org.mage.test.serverside.base;

import java.io.File;
import java.io.FileNotFoundException;
import mage.cards.Card;
import mage.cards.decks.Deck;
import mage.cards.decks.importer.DeckImporterUtil;
import mage.constants.MultiplayerAttackOption;
import mage.constants.PhaseStep;
import mage.constants.RangeOfInfluence;
import mage.filter.Filter;
import mage.game.Game;
import mage.game.GameException;
import mage.game.GameOptions;
import mage.game.TwoPlayerDuel;
import mage.game.permanent.Permanent;
import mage.players.Player;
import org.junit.Assert;
import org.junit.Before;
import org.mage.test.player.TestPlayer;
import static org.mage.test.serverside.base.MageTestPlayerBase.TESTS_PATH;
import static org.mage.test.serverside.base.MageTestPlayerBase.activePlayer;
import static org.mage.test.serverside.base.MageTestPlayerBase.currentGame;
import static org.mage.test.serverside.base.MageTestPlayerBase.logger;
import org.mage.test.serverside.base.impl.CardTestPlayerAPIImpl;

/**
 * Base class for testing single cards and effects.
 *
 * @author ayratn
 */
public abstract class CardTestPlayerBase extends CardTestPlayerAPIImpl {

    public static final String NO_TARGET = "NO_TARGET";
    
    protected enum ExpectedType {
        TURN_NUMBER,
        RESULT,
        LIFE,
        BATTLEFIELD,
        GRAVEYARD,
        UNKNOWN
    }
    
    protected GameOptions gameOptions;

    public CardTestPlayerBase() {
    }

    @Override
    protected TestPlayer createNewPlayer(String playerName) {
        return createPlayer(playerName);
    }

    @Before
    public void reset() throws GameException, FileNotFoundException {
        if (currentGame != null) {
            logger.debug("Resetting previous game and creating new one!");
            currentGame = null;
            System.gc();
        }

        Game game = new TwoPlayerDuel(MultiplayerAttackOption.LEFT, RangeOfInfluence.ONE, 0, 20);

        playerA = createNewPlayer("PlayerA");
        playerA.setTestMode(true);
        logger.debug("Loading deck...");
        Deck deck = Deck.load(DeckImporterUtil.importDeck("RB Aggro.dck"), false, false);
        logger.debug("Done!");
        if (deck.getCards().size() < 40) {
            throw new IllegalArgumentException("Couldn't load deck, deck size=" + deck.getCards().size());
        }        
        game.loadCards(deck.getCards(), playerA.getId());
        game.addPlayer(playerA, deck);

        playerB = createNewPlayer("PlayerB");
        playerB.setTestMode(true);
        Deck deck2 = Deck.load(DeckImporterUtil.importDeck("RB Aggro.dck"), false, false);
        if (deck2.getCards().size() < 40) {
            throw new IllegalArgumentException("Couldn't load deck, deck size=" + deck2.getCards().size());
        }        
        game.loadCards(deck2.getCards(), playerB.getId());
        game.addPlayer(playerB, deck2);
        activePlayer = playerA;
        currentGame = game;

        stopOnTurn = 2;
        stopAtStep = PhaseStep.UNTAP;

        for (Player player : currentGame.getPlayers().values()) {
            TestPlayer testPlayer = (TestPlayer)player;
            getCommands(testPlayer).clear();
            getLibraryCards(testPlayer).clear();
            getHandCards(testPlayer).clear();
            getBattlefieldCards(testPlayer).clear();
            getGraveCards(testPlayer).clear();
        }

        gameOptions = new GameOptions();
    }

    public void load(String path) throws FileNotFoundException, GameException {
        String cardPath = TESTS_PATH + path;
        File checkFile = new File(cardPath);
        if (!checkFile.exists()) {
            throw new FileNotFoundException("Couldn't find test file: " + cardPath);
        }
        if (checkFile.isDirectory()) {
            throw new FileNotFoundException("Couldn't find test file: " + cardPath + ". It is directory.");
        }

        if (currentGame != null) {
            logger.debug("Resetting previous game and creating new one!");
            currentGame = null;
            System.gc();
        }

        Game game = new TwoPlayerDuel(MultiplayerAttackOption.LEFT, RangeOfInfluence.ALL, 0, 20);

        playerA = createNewPlayer("ComputerA");
        playerA.setTestMode(true);

        Deck deck = Deck.load(DeckImporterUtil.importDeck("RB Aggro.dck"), false, false);

        if (deck.getCards().size() < 40) {
            throw new IllegalArgumentException("Couldn't load deck, deck size=" + deck.getCards().size());
        }
        game.addPlayer(playerA, deck);
        game.loadCards(deck.getCards(), playerA.getId());

        playerB = createNewPlayer("ComputerB");
        playerB.setTestMode(true);
        Deck deck2 = Deck.load(DeckImporterUtil.importDeck("RB Aggro.dck"), false, false);
        if (deck2.getCards().size() < 40) {
            throw new IllegalArgumentException("Couldn't load deck, deck size=" + deck2.getCards().size());
        }
        game.addPlayer(playerB, deck2);
        game.loadCards(deck2.getCards(), playerB.getId());

        parseScenario(cardPath);

        activePlayer = playerA;
        currentGame = game;
    }

    /**
     * Starts testing card by starting current game.
     *
     * @throws IllegalStateException In case game wasn't created previously. Use {@link #load} method to initialize the game.
     */
    public void execute() throws IllegalStateException {
        if (currentGame == null || activePlayer == null) {
            throw new IllegalStateException("Game is not initialized. Use load method to load a test case and initialize a game.");
        }

        for (Player player : currentGame.getPlayers().values()) {
            TestPlayer testPlayer = (TestPlayer)player;
            currentGame.cheat(player.getId(), getCommands(testPlayer));
            currentGame.cheat(player.getId(), getLibraryCards(testPlayer), getHandCards(testPlayer),
                    getBattlefieldCards(testPlayer), getGraveCards(testPlayer));
        }

        boolean testMode = true;
        long t1 = System.nanoTime();

        gameOptions.testMode = true;
        gameOptions.stopOnTurn = stopOnTurn;
        gameOptions.stopAtStep = stopAtStep;
        currentGame.start(activePlayer.getId(), gameOptions);
        long t2 = System.nanoTime();
        logger.debug("Winner: " + currentGame.getWinner());
        logger.info("Test has been executed. Execution time: " + (t2 - t1) / 1000000 + " ms");

        assertTheResults();
    }

    /**
     * Assert expected and actual results.
     */
    private void assertTheResults() {
        logger.debug("Matching expected results:");
        for (String line : expectedResults) {
            boolean ok = false;
            try {
                ExpectedType type = getExpectedType(line);
                if (type.equals(CardTestPlayerBase.ExpectedType.UNKNOWN)) {
                    throw new AssertionError("Unknown expected type, check the line in $expected section=" + line);
                }
                parseType(type, line);
                ok = true;
            } finally {
                logger.info("  " + line + " - " + (ok ? "OK" : "ERROR"));
            }
        }
    }

    private ExpectedType getExpectedType(String line) {
        if (line.startsWith("turn:")) {
            return CardTestPlayerBase.ExpectedType.TURN_NUMBER;
        }
        if (line.startsWith("result:")) {
            return CardTestPlayerBase.ExpectedType.RESULT;
        }
        if (line.startsWith("life:")) {
            return CardTestPlayerBase.ExpectedType.LIFE;
        }
        if (line.startsWith("battlefield:")) {
            return CardTestPlayerBase.ExpectedType.BATTLEFIELD;
        }
        if (line.startsWith("graveyard:")) {
            return CardTestPlayerBase.ExpectedType.GRAVEYARD;
        }
        return CardTestPlayerBase.ExpectedType.UNKNOWN;
    }

    private void parseType(ExpectedType type, String line) {
        if (type.equals(CardTestPlayerBase.ExpectedType.TURN_NUMBER)) {
            int turn = getIntParam(line, 1);
            Assert.assertEquals("Turn numbers are not equal", turn, currentGame.getTurnNum());
            return;
        }
        if (type.equals(CardTestPlayerBase.ExpectedType.RESULT)) {
            String expected = getStringParam(line, 1);
            String actual = "draw";
            switch (currentGame.getWinner()) {
                case "Player ComputerA is the winner":
                    actual = "won";
                    break;
                case "Player ComputerB is the winner":
                    actual = "lost";
                    break;
            }
            Assert.assertEquals("Game results are not equal", expected, actual);
            return;
        }

        Player player = null;
        String playerName = getStringParam(line, 1);
        switch (playerName) {
            case "ComputerA":
                player = currentGame.getPlayer(playerA.getId());
                break;
            case "ComputerB":
                player = currentGame.getPlayer(playerB.getId());
                break;
        }
        if (player == null) {
            throw new IllegalArgumentException("Wrong player in 'battlefield' line, player=" + player + ", line=" + line);
        }

        if (type.equals(CardTestPlayerBase.ExpectedType.LIFE)) {
            int expected = getIntParam(line, 2);
            int actual = player.getLife();
            Assert.assertEquals("Life amounts are not equal", expected, actual);
            return;
        }

        if (type.equals(CardTestPlayerBase.ExpectedType.BATTLEFIELD)) {
            String cardName = getStringParam(line, 2);
            int expectedCount = getIntParam(line, 3);
            int actualCount = 0;
            for (Permanent permanent : currentGame.getBattlefield().getAllPermanents()) {
                if (permanent.getControllerId().equals(player.getId())) {
                    if (permanent.getName().equals(cardName)) {
                        actualCount++;
                    }
                }
            }
            Assert.assertEquals("(Battlefield) Card counts are not equal (" + cardName + ")", expectedCount, actualCount);
            return;
        }

        if (type.equals(CardTestPlayerBase.ExpectedType.GRAVEYARD)) {
            String cardName = getStringParam(line, 2);
            int expectedCount = getIntParam(line, 3);
            int actualCount = 0;
            for (Card card : player.getGraveyard().getCards(currentGame)) {
                if (card.getName().equals(cardName)) {
                    actualCount++;
                }
            }
            Assert.assertEquals("(Graveyard) Card counts are not equal (" + cardName + ")", expectedCount, actualCount);
        }
    }

    private int getIntParam(String line, int index) {
        String[] params = line.split(":");
        if (index > params.length - 1) {
            throw new IllegalArgumentException("Not correct line: " + line);
        }
        return Integer.parseInt(params[index]);
    }

    private String getStringParam(String line, int index) {
        String[] params = line.split(":");
        if (index > params.length - 1) {
            throw new IllegalArgumentException("Not correct line: " + line);
        }
        return params[index];
    }

    protected void checkPermanentPT(Player player, String cardName, int power, int toughness, Filter.ComparisonScope scope) {
        if (currentGame == null) {
            throw new IllegalStateException("Current game is null");
        }
        if (scope.equals(Filter.ComparisonScope.All)) {
            throw new UnsupportedOperationException("ComparisonScope.All is not implemented.");
        }

        for (Permanent permanent : currentGame.getBattlefield().getAllActivePermanents(player.getId())) {
            if (permanent.getName().equals(cardName)) {
                Assert.assertEquals("Power is not the same", power, permanent.getPower().getValue());
                Assert.assertEquals("Toughness is not the same", toughness, permanent.getToughness().getValue());
                break;
            }
        }
    }

    protected void skipInitShuffling() {
        gameOptions.skipInitShuffling = true;
    }
}
