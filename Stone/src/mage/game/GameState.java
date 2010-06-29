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

package mage.game;

import mage.abilities.TriggeredAbility;
import mage.cards.Card;
import mage.game.events.GameEvent;
import mage.game.stack.SpellStack;
import mage.game.stack.StackObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import mage.Constants.Zone;
import mage.MageObject;
import mage.abilities.DelayedTriggeredAbilities;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.SpecialActions;
import mage.abilities.TriggeredAbilities;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.ContinuousEffects;
import mage.game.combat.Combat;
import mage.game.permanent.Battlefield;
import mage.game.permanent.Permanent;
import mage.game.turn.Turn;
import mage.game.turn.TurnMods;
import mage.players.Player;
import mage.players.PlayerList;
import mage.players.Players;
import mage.watchers.Watchers;

/**
 *
 * @author BetaSteward_at_googlemail.com
 *
 * since at any time the game state may be copied and restored you cannot
 * rely on any object maintaining it's instance
 * it then becomes necessary to only refer to objects by their ids since
 * these will always remain constant throughout its lifetime
 * 
 */
public class GameState implements Serializable {

	private Players players = new Players();
	private PlayerList playerList = new PlayerList();
	private UUID activePlayerId;
	private UUID priorityPlayerId;
	private Turn turn = new Turn();
	private SpellStack stack = new SpellStack();
	private Exile exile = new Exile();
	private Battlefield battlefield = new Battlefield();
	private int turnNum = 0;
	private boolean gameOver = false;
	private List<String> messages = new ArrayList<String>();
	private ContinuousEffects effects = new ContinuousEffects();
	private TriggeredAbilities triggers = new TriggeredAbilities();
	private DelayedTriggeredAbilities delayed = new DelayedTriggeredAbilities();
	private SpecialActions specialActions = new SpecialActions();
	private Combat combat = new Combat();
	private TurnMods turnMods = new TurnMods();
	private Watchers watchers = new Watchers();
	private Map<String, Object> values = new HashMap<String, Object>();

	public void addPlayer(Player player) {
		players.put(player.getId(), player);
		playerList.add(player.getId());
	}
	
	public Players getPlayers() {
		return players;
	}

	public Player getPlayer(UUID playerId) {
		return players.get(playerId);
	}

	public UUID getActivePlayerId() {
		return activePlayerId;
	}

	public void setActivePlayerId(UUID activePlayerId) {
		this.activePlayerId = activePlayerId;
	}

	public UUID getPriorityPlayerId() {
		return priorityPlayerId;
	}

	public void setPriorityPlayerId(UUID priorityPlayerId) {
		this.priorityPlayerId = priorityPlayerId;
	}

	public Battlefield getBattlefield() {
		return this.battlefield;
	}

	public SpellStack getStack() {
		return this.stack;
	}

	public Exile getExile() {
		return exile;
	}

	public Turn getTurn() {
		return turn;
	}

	public Combat getCombat() {
		return combat;
	}

	public int getTurnNum() {
		return turnNum;
	}

	public void setTurnNum(int turnNum) {
		this.turnNum = turnNum;
	}

	public boolean isGameOver() {
		return this.gameOver;
	}

	public TurnMods getTurnMods() {
		return this.turnMods;
	}

	public Watchers getWatchers() {
		return this.watchers;
	}

	public SpecialActions getSpecialActions() {
		return this.specialActions;
	}
	
	public void endGame() {
		this.gameOver = true;
	}

	public void applyEffects(Game game) {
		for (Player player: players.values()) {
			player.reset();
		}
		battlefield.reset();
		effects.apply(game);
	}

	public void removeEotEffects(Game game) {
		effects.removeEndOfTurnEffects();
		applyEffects(game);
	}

	public void addEffect(ContinuousEffect effect) {
		effects.addEffect(effect);
	}

	public void addMessage(String message) {
		this.messages.add(message);
	}

	public PlayerList getPlayerList() {
		return playerList;
	}

	public PlayerList getPlayerList(UUID playerId) {
		PlayerList newPlayerList = new PlayerList();
		for (Player player: players.values()) {
			if (!player.hasLeft() && !player.hasLost())
				newPlayerList.add(player.getId());
		}
		newPlayerList.setCurrent(playerId);
		return newPlayerList;
	}

	public MageObject getObject(UUID objectId) {
		MageObject object;
		if (battlefield.containsPermanent(objectId)) {
			object = battlefield.getPermanent(objectId);
			object.setZone(Zone.BATTLEFIELD);
			return object;
		}
		object = getCard(objectId);
		if (object != null)
			return object;
		for (StackObject item: stack) {
			if (item.getId().equals(objectId)) {
				item.setZone(Zone.STACK);
				return item;
			}
		}

		return null;
	}

	public Card getCard(UUID cardId) {
		Card card;
		for(Player player: players.values()) {
			if (player.getHand().containsKey(cardId)) {
				card = player.getHand().get(cardId);
				card.setZone(Zone.HAND);
				return card;
			}
			if (player.getGraveyard().containsKey(cardId)) {
				card = player.getGraveyard().get(cardId);
				card.setZone(Zone.GRAVEYARD);
				return card;
			}
		}
		return this.exile.getCard(cardId);
	}

	public Permanent getPermanent(UUID permanentId) {
		Permanent permanent;
		if (battlefield.containsPermanent(permanentId)) {
			permanent = battlefield.getPermanent(permanentId);
			permanent.setZone(Zone.BATTLEFIELD);
			return permanent;
		}
		return null;
	}

	public void restore(GameState state) {
		this.stack = state.stack;
		this.effects = state.effects;
		this.triggers = state.triggers;
		this.combat = state.combat;
		this.exile = state.exile;
		this.battlefield = state.battlefield;
		for (Player copyPlayer: state.players.values()) {
			Player origPlayer = players.get(copyPlayer.getId());
			origPlayer.restore(copyPlayer);
		}
	}

	public void handleEvent(GameEvent event, Game game) {
		if (!replaceEvent(event, game)) {
			for (Player player: players.values()) {
				player.checkTriggers(event, game);
			}
		}
		battlefield.checkTriggers(event, game);
		stack.checkTriggers(event, game);
		delayed.checkTriggers(event, game);
		exile.checkTriggers(event, game);
		watchers.watch(event, game);
	}

	public boolean replaceEvent(GameEvent event, Game game) {
		return stack.replaceEvent(event, game) | effects.replaceEvent(event, game);

	}

	public void addTriggeredAbility(TriggeredAbility ability) {
		this.triggers.add(ability);
	}

	public void addDelayedTriggeredAbility(DelayedTriggeredAbility ability) {
		this.delayed.add(ability);
	}

	public void removeDelayedTriggeredAbility(UUID abilityId) {
		for (DelayedTriggeredAbility ability: delayed) {
			if (ability.getId().equals(abilityId))  {
				delayed.remove(ability);
				break;
			}
		}
	}

	public TriggeredAbilities getTriggered() {
		return this.triggers;
	}

	public ContinuousEffects getContinuousEffects() {
		return effects;
	}

	public Object getValue(String valueId) {
		return values.get(valueId);
	}

	public void setValue(String valueId, Object value) {
		values.put(valueId, value);
	}
}
