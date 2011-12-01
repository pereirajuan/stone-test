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
import mage.game.events.GameEvent;
import mage.game.stack.SpellStack;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import mage.Constants.Zone;
import mage.abilities.Abilities;
import mage.abilities.AbilitiesImpl;
import mage.abilities.Ability;
import mage.abilities.ActivatedAbility;
import mage.abilities.DelayedTriggeredAbilities;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.SpecialActions;
import mage.abilities.TriggeredAbilities;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.ContinuousEffects;
import mage.game.combat.Combat;
import mage.game.combat.CombatGroup;
import mage.game.command.Command;
import mage.game.events.GameEvent.EventType;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.Battlefield;
import mage.game.permanent.Permanent;
import mage.game.permanent.PermanentCard;
import mage.game.stack.StackObject;
import mage.game.turn.Turn;
import mage.game.turn.TurnMods;
import mage.players.Player;
import mage.players.PlayerList;
import mage.players.Players;
import mage.util.Copyable;
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
public class GameState implements Serializable, Copyable<GameState> {

	private Players players;
	private PlayerList playerList;
	private UUID activePlayerId;
	private UUID priorityPlayerId;
	private Turn turn;
	private SpellStack stack;
	private Command command;
	private Exile exile;
	private Revealed revealed;
	private Map<UUID, LookedAt> lookedAt = new HashMap<UUID, LookedAt>();
	private Battlefield battlefield;
	private int turnNum = 1;
	private boolean gameOver;
    private boolean paused;
//	private List<String> messages = new ArrayList<String>();
	private ContinuousEffects effects;
	private TriggeredAbilities triggers;
	private DelayedTriggeredAbilities delayed;
	private SpecialActions specialActions;
    private Map<UUID, Abilities<ActivatedAbility>> otherAbilities = new HashMap<UUID, Abilities<ActivatedAbility>>();
	private Combat combat;
	private TurnMods turnMods;
	private Watchers watchers;
	private Map<String, Object> values = new HashMap<String, Object>();
	private Map<UUID, Zone> zones = new HashMap<UUID, Zone>();

	public GameState() {
		players = new Players();
		playerList = new PlayerList();
		turn = new Turn();
		stack = new SpellStack();
		command = new Command();
		exile = new Exile();
		revealed = new Revealed();
		lookedAt = new HashMap<UUID, LookedAt>();
		battlefield = new Battlefield();
		effects = new ContinuousEffects();
		triggers = new TriggeredAbilities();
		delayed = new DelayedTriggeredAbilities();
		specialActions = new SpecialActions();
		combat = new Combat();
		turnMods = new TurnMods();
		watchers = new Watchers();
	}

	public GameState(final GameState state) {
		this.players = state.players.copy();
		this.playerList = state.playerList.copy();
		this.activePlayerId = state.activePlayerId;
		this.priorityPlayerId = state.priorityPlayerId;
		this.turn = state.turn.copy();
		this.stack = state.stack.copy();
		this.command = state.command.copy();
		this.exile = state.exile.copy();
		this.revealed = state.revealed.copy();
        this.lookedAt.putAll(state.lookedAt);
		this.battlefield = state.battlefield.copy();
		this.turnNum = state.turnNum;
		this.gameOver = state.gameOver;
		this.effects = state.effects.copy();
		this.triggers = state.triggers.copy();
		this.delayed = state.delayed.copy();
		this.specialActions = state.specialActions.copy();
		this.combat = state.combat.copy();
		this.turnMods = state.turnMods.copy();
		this.watchers = state.watchers.copy();
        this.values.putAll(state.values);
        this.zones.putAll(state.zones);
		for (Map.Entry<UUID, Abilities<ActivatedAbility>> entry: state.otherAbilities.entrySet()) {
			otherAbilities.put(entry.getKey(), entry.getValue().copy());
		}
	}

	@Override
	public GameState copy() {
		return new GameState(this);
	}

	public void addPlayer(Player player) {
		players.put(player.getId(), player);
		playerList.add(player.getId());
	}

	public String getValue() {
		StringBuilder sb = new StringBuilder(1024);

		sb.append(turnNum).append(turn.getPhaseType()).append(turn.getStepType()).append(activePlayerId).append(priorityPlayerId);

		for (Player player: players.values()) {
			sb.append("player").append(player.getLife()).append("hand").append(player.getHand()).append("library").append(player.getLibrary().size()).append("graveyard").append(player.getGraveyard());
		}

		for (UUID permanentId: battlefield.getAllPermanentIds()) {
			sb.append("permanent").append(permanentId);
		}

		for (StackObject spell: stack) {
			sb.append("spell").append(spell.getId());
		}
        
        for (ExileZone zone: exile.getExileZones()) {
            sb.append("exile").append(zone.getName()).append(zone);
        }
        
        for (CombatGroup group: combat.getGroups()) {
            sb.append("combat").append(group.getDefenderId()).append(group.getAttackers()).append(group.getBlockers());
        }

		return sb.toString();
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

	public Command getCommand() {
		return command;
	}

	public Revealed getRevealed() {
		return revealed;
	}

	public LookedAt getLookedAt(UUID playerId) {
		if (lookedAt.get(playerId) == null) {
			LookedAt l = new LookedAt();
			lookedAt.put(playerId, l);
			return l;
		}
		return lookedAt.get(playerId);
	}

    public void clearLookedAt() {
        lookedAt.clear();
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
		battlefield.reset(game);
        resetOtherAbilities();
		effects.apply(game);
		battlefield.fireControlChangeEvents(game);
	}

	public void removeEotEffects(Game game) {
		effects.removeEndOfTurnEffects();
		applyEffects(game);
	}

	public void addEffect(ContinuousEffect effect, Ability source) {
		effects.addEffect(effect, source);
	}

//	public void addMessage(String message) {
//		this.messages.add(message);
//	}

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

	public Permanent getPermanent(UUID permanentId) {
        if (permanentId != null && battlefield.containsPermanent(permanentId)) {
            Permanent permanent = battlefield.getPermanent(permanentId);
            setZone(permanent.getId(), Zone.BATTLEFIELD);
            return permanent;
        }
		return null;
	}

	public Zone getZone(UUID id) {
		if (id != null && zones.containsKey(id))
			return zones.get(id);
		return null;
	}

	public void setZone(UUID id, Zone zone) {
		zones.put(id, zone);
	}

	public void restore(GameState state) {
		this.stack = state.stack;
		this.command = state.command;
		this.effects = state.effects;
		this.triggers = state.triggers;
		this.combat = state.combat;
		this.exile = state.exile;
		this.battlefield = state.battlefield;
		this.zones = state.zones;
		for (Player copyPlayer: state.players.values()) {
			Player origPlayer = players.get(copyPlayer.getId());
			origPlayer.restore(copyPlayer);
		}
	}

	public void handleEvent(GameEvent event, Game game) {
		watchers.watch(event, game);
//		if (!replaceEvent(event, game)) {
			//TODO: this is awkward - improve
			if (event.getType() == EventType.ZONE_CHANGE) {
				ZoneChangeEvent zEvent = (ZoneChangeEvent)event;
				if (zEvent.getFromZone() == Zone.BATTLEFIELD && zEvent.getTarget() != null) {
					if (zEvent.getTarget() instanceof PermanentCard) {
						((PermanentCard)zEvent.getTarget()).checkPermanentOnlyTriggers(zEvent, game);
					}
					else {
						zEvent.getTarget().checkTriggers(zEvent.getFromZone(), event, game);
						zEvent.getTarget().checkTriggers(zEvent.getToZone(), event, game);
					}
				}
			}
			for (Player player: players.values()) {
				player.checkTriggers(event, game);
			}
			battlefield.checkTriggers(event, game);
			stack.checkTriggers(event, game);
			command.checkTriggers(event, game);
			delayed.checkTriggers(event, game);
			exile.checkTriggers(event, game);
//		}
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

    public DelayedTriggeredAbilities getDelayed() {
        return this.delayed;
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

    public Abilities<ActivatedAbility> getOtherAbilities(UUID objectId, Zone zone) {
        if (otherAbilities.containsKey(objectId)) {
            return otherAbilities.get(objectId).getActivatedAbilities(zone);
        }
        return null;
    }
    
    public void addOtherAbility(UUID objectId, ActivatedAbility ability) {
        if (!otherAbilities.containsKey(objectId)) {
            otherAbilities.put(objectId, new AbilitiesImpl());
        }
        otherAbilities.get(objectId).add(ability);
    }
    
    private void resetOtherAbilities() {
        for (Abilities<ActivatedAbility> abilities: otherAbilities.values()) {
            abilities.clear();
        }
    }
    
    public void clear() {
        battlefield.clear();
        effects.clear();
        delayed.clear();
        triggers.clear();
        stack.clear();
        exile.clear();
        command.clear();
        revealed.clear();
        lookedAt.clear();
        turnNum = 0;
        gameOver = false;
    	specialActions.clear();
        otherAbilities.clear();
        combat.clear();
        turnMods.clear();
        watchers.clear();
        values.clear();
        zones.clear();
    }

    public void pause() {
        this.paused = true;
    }
    
    public void resume() {
        this.paused = false;
    }
    
    public boolean isPaused() {
        return this.paused;
    }

}
