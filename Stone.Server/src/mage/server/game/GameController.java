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

package mage.server.game;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import mage.abilities.ActivatedAbility;
import mage.cards.Card;
import mage.cards.Cards;
import mage.cards.decks.Deck;
import mage.cards.decks.DeckCardLists;
import mage.game.Game;
import mage.game.GameReplay;
import mage.game.events.TableEvent;
import mage.server.ChatManager;
import mage.server.util.ThreadExecutor;
import mage.game.events.Listener;
import mage.game.events.PlayerQueryEvent;
import mage.human.HumanPlayer;
import mage.players.Player;
import mage.util.Logging;
import mage.view.AbilityPickerView;
import mage.view.CardsView;
import mage.view.GameView;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class GameController implements GameCallback {

	private static ExecutorService gameExecutor = ThreadExecutor.getInstance().getGameExecutor();
	private final static Logger logger = Logging.getLogger(GameController.class.getName());

	private ConcurrentHashMap<UUID, GameSession> gameSessions = new ConcurrentHashMap<UUID, GameSession>();
	private ConcurrentHashMap<UUID, GameWatcher> watchers = new ConcurrentHashMap<UUID, GameWatcher>();
	private ConcurrentHashMap<UUID, UUID> sessionPlayerMap;
	private UUID gameSessionId;
	private Game game;
	private UUID chatId;
	private UUID tableId;
	private Future<?> gameFuture;


	public GameController(Game game, ConcurrentHashMap<UUID, UUID> sessionPlayerMap, UUID tableId) {
		gameSessionId = UUID.randomUUID();
		this.sessionPlayerMap = sessionPlayerMap;
		chatId = ChatManager.getInstance().createChatSession();
		this.game = game;
		this.tableId = tableId;
		init();
	}

	private void init() {
		game.addTableEventListener(
			new Listener<TableEvent> () {
				@Override
				public void event(TableEvent event) {
					switch (event.getEventType()) {
						case UPDATE:
							updateGame();
							break;
						case INFO:
							ChatManager.getInstance().broadcast(chatId, "", event.getMessage());
							logger.fine(game.getId() + " " + event.getMessage());
							break;
					}
				}
			}
		);
		game.addPlayerQueryEventListener(
			new Listener<PlayerQueryEvent> () {
				@Override
				public void event(PlayerQueryEvent event) {
					switch (event.getQueryType()) {
						case ASK:
							ask(event.getPlayerId(), event.getMessage());
							break;
						case PICK_TARGET:
							target(event.getPlayerId(), event.getMessage(), event.getCards(), event.isRequired());
							break;
						case SELECT:
							select(event.getPlayerId(), event.getMessage());
							break;
						case PLAY_MANA:
							playMana(event.getPlayerId(), event.getMessage());
							break;
						case PLAY_X_MANA:
							playXMana(event.getPlayerId(), event.getMessage());
							break;
						case CHOOSE_ABILITY:
							chooseAbility(event.getPlayerId(), event.getAbilities());
							break;
						case CHOOSE:
							choose(event.getPlayerId(), event.getMessage(), event.getChoices());
							break;
						case AMOUNT:
							amount(event.getPlayerId(), event.getMessage(), event.getMin(), event.getMax());
							break;
					}
				}
			}
		);
	}

	private UUID getPlayerId(UUID sessionId) {
		return sessionPlayerMap.get(sessionId);
	}

	public void join(UUID sessionId) {
		UUID playerId = sessionPlayerMap.get(sessionId);
		GameSession gameSession = new GameSession(game, sessionId, playerId);
		gameSessions.put(playerId, gameSession);
		logger.info("player " + playerId + " has joined game " + game.getId());
		gameSession.init(getGameView(playerId));
		ChatManager.getInstance().broadcast(chatId, "", game.getPlayer(playerId).getName() + " has joined the game");
		if (allJoined()) {
			startGame();
		}
	}

	private synchronized void startGame() {
		if (gameFuture == null) {
			GameWorker worker = new GameWorker(game, this);
			gameFuture = gameExecutor.submit(worker);
		}
	}

	private boolean allJoined() {
		for (Player player: game.getPlayers().values()) {
			if (player instanceof HumanPlayer && gameSessions.get(player.getId()) == null) {
				return false;
			}
		}
		return true;
	}

	public void watch(UUID sessionId) {
		GameWatcher gameWatcher = new GameWatcher(sessionId, game.getId());
		watchers.put(sessionId, gameWatcher);
		gameWatcher.init(getGameView());
		ChatManager.getInstance().broadcast(chatId, "", " has started watching");
	}

	public GameReplay createReplay() {
		if (game.isGameOver()) {
			return new GameReplay(game.getGameStates());
		}
		return null;
	}
	
	public void stopWatching(UUID sessionId) {
		watchers.remove(sessionId);
		ChatManager.getInstance().broadcast(chatId, "", " has stopped watching");
	}
	
	public void concede(UUID sessionId) {
		game.concede(getPlayerId(sessionId));
	}

	private void leave(UUID sessionId) {
		game.quit(getPlayerId(sessionId));
	}

	public void cheat(UUID sessionId, DeckCardLists deckList) {
		Player player = game.getPlayer(getPlayerId(sessionId));
		Deck deck = Deck.load(deckList);
		deck.setOwnerId(player.getId());
		for (Card card: deck.getCards().values()) {
			player.putOntoBattlefield(card, game);
		}
		updateGame();
	}

//	public void timeout(UUID sessionId) {
//		kill(sessionId);
//	}

	public void kill(UUID sessionId) {
		if (sessionPlayerMap.containsKey(sessionId)) {
			gameSessions.get(sessionPlayerMap.get(sessionId)).setKilled();
			gameSessions.remove(sessionPlayerMap.get(sessionId));
			leave(sessionId);
			sessionPlayerMap.remove(sessionId);
		}
		if (watchers.containsKey(sessionId)) {
			watchers.get(sessionId).setKilled();
			watchers.remove(sessionId);
		}
	}

	public void timeout(UUID sessionId) {
		if (sessionPlayerMap.containsKey(sessionId)) {
			ChatManager.getInstance().broadcast(chatId, "", game.getPlayer(sessionPlayerMap.get(sessionId)).getName() + " has timed out.  Auto concede.");
			concede(sessionId);
		}
	}

	public void endGame(final String message) {
		for (final GameSession gameSession: gameSessions.values()) {
			gameSession.gameOver(message);
		}
		for (final GameWatcher gameWatcher: watchers.values()) {
			gameWatcher.gameOver(message);
		}
		TableManager.getInstance().endGame(tableId);
	}

	public UUID getSessionId() {
		return this.gameSessionId;
	}

	public UUID getChatId() {
		return chatId;
	}

	public void sendPlayerUUID(UUID sessionId, UUID data) {
		gameSessions.get(sessionPlayerMap.get(sessionId)).sendPlayerUUID(data);
	}

	public void sendPlayerString(UUID sessionId, String data) {
		gameSessions.get(sessionPlayerMap.get(sessionId)).sendPlayerString(data);
	}

	public void sendPlayerBoolean(UUID sessionId, Boolean data) {
		gameSessions.get(sessionPlayerMap.get(sessionId)).sendPlayerBoolean(data);
	}

	public void sendPlayerInteger(UUID sessionId, Integer data) {
		gameSessions.get(sessionPlayerMap.get(sessionId)).sendPlayerInteger(data);
	}

	private void updateGame() {

		for (final Entry<UUID, GameSession> entry: gameSessions.entrySet()) {
			entry.getValue().update(getGameView(entry.getKey()));
		}
		for (final GameWatcher gameWatcher: watchers.values()) {
			gameWatcher.update(getGameView());
		}
	}

	private void ask(UUID playerId, String question) {
		informOthers(playerId);
		gameSessions.get(playerId).ask(question, getGameView(playerId));
	}

	private void chooseAbility(UUID playerId, Collection<? extends ActivatedAbility> choices) {
		informOthers(playerId);
		gameSessions.get(playerId).chooseAbility(new AbilityPickerView(choices));
	}

	private void choose(UUID playerId, String message, String[] choices) {
		informOthers(playerId);
		gameSessions.get(playerId).choose(message, choices);
	}

	private void target(UUID playerId, String question, Cards cards, boolean required) {
		informOthers(playerId);
		gameSessions.get(playerId).target(question, getCardView(cards), required, getGameView(playerId));
	}

	private void select(UUID playerId, String message) {
		informOthers(playerId);
		gameSessions.get(playerId).select(message, getGameView(playerId));
	}

	private void playMana(UUID playerId, String message) {
		informOthers(playerId);
		gameSessions.get(playerId).playMana(message, getGameView(playerId));
	}

	private void playXMana(UUID playerId, String message) {
		informOthers(playerId);
		gameSessions.get(playerId).playXMana(message, getGameView(playerId));
	}

	private void amount(UUID playerId, String message, int min, int max) {
		informOthers(playerId);
		gameSessions.get(playerId).getAmount(message, min, max);
	}

	private void revealCards(String name, Cards cards) {
		for (GameSession session: gameSessions.values()) {
			session.revealCards(name, getCardView(cards));
		}
	}

	private void informOthers(UUID playerId) {
		final String message = "Waiting for " + game.getPlayer(playerId).getName();
		for (final Entry<UUID, GameSession> entry: gameSessions.entrySet()) {
			if (!entry.getKey().equals(playerId)) {
				entry.getValue().inform(message, getGameView(entry.getKey()));
			}
		}
		for (final GameWatcher watcher: watchers.values()) {
			watcher.inform(message, getGameView());
		}
	}

	private GameView getGameView() {
		return new GameView(game.getState());
	}

	private GameView getGameView(UUID playerId) {
		GameView gameView = new GameView(game.getState());
		gameView.setHand(getCardView(game.getPlayer(playerId).getHand()));
		return gameView;
	}

	private CardsView getCardView(Cards cards) {
		if (cards == null)
			return null;
		return new CardsView(cards.values());
	}

	@Override
	public void gameResult(String result) {
		endGame(result);
	}

}
