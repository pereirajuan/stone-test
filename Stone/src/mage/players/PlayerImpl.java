/*
* Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
*
* Redistribution and use in ability and binary forms, with or without modification, are
* permitted provided that the following conditions are met:
*
*    1. Redistributions of ability code must retain the above copyright notice, this list of
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

package mage.players;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import mage.MageObject;
import mage.Mana;
import mage.abilities.Abilities;
import mage.abilities.AbilitiesImpl;
import mage.abilities.Ability;
import mage.abilities.ActivatedAbility;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.Mode;
import mage.abilities.PlayLandAbility;
import mage.abilities.SpecialAction;
import mage.abilities.SpellAbility;
import mage.abilities.TriggeredAbility;
import mage.abilities.common.PassAbility;
import mage.abilities.common.delayed.AtTheEndOfTurnStepPostDelayedTriggeredAbility;
import mage.abilities.costs.AdjustingSourceCosts;
import mage.abilities.costs.AlternativeCost;
import mage.abilities.effects.RestrictionEffect;
import mage.abilities.effects.RestrictionUntapNotMoreThanEffect;
import mage.abilities.effects.common.LoseControlOnOtherPlayersControllerEffect;
import mage.abilities.keyword.FlashbackAbility;
import mage.abilities.keyword.HexproofAbility;
import mage.abilities.keyword.InfectAbility;
import mage.abilities.keyword.LifelinkAbility;
import mage.abilities.keyword.ProtectionAbility;
import mage.abilities.keyword.ShroudAbility;
import mage.abilities.mana.ManaAbility;
import mage.abilities.mana.ManaOptions;
import mage.actions.MageDrawAction;
import mage.cards.Card;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.cards.SplitCard;
import mage.cards.decks.Deck;
import mage.constants.AsThoughEffectType;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.RangeOfInfluence;
import mage.constants.SpellAbilityType;
import mage.constants.TimingRule;
import mage.constants.Zone;
import mage.counters.Counter;
import mage.counters.CounterType;
import mage.counters.Counters;
import mage.filter.FilterCard;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.common.FilterCreatureForCombat;
import mage.filter.common.FilterCreatureForCombatBlock;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.permanent.PermanentIdPredicate;
import mage.game.ExileZone;
import mage.game.Game;
import mage.game.Table;
import mage.game.combat.CombatGroup;
import mage.game.events.DamagePlayerEvent;
import mage.game.events.DamagedPlayerEvent;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.Permanent;
import mage.game.stack.Spell;
import mage.game.stack.StackAbility;
import mage.game.stack.StackObject;
import mage.players.net.UserData;
import mage.target.Target;
import mage.target.TargetAmount;
import mage.target.TargetCard;
import mage.target.TargetPermanent;
import mage.target.common.TargetCardInLibrary;
import mage.target.common.TargetDiscard;
import mage.watchers.common.BloodthirstWatcher;
import org.apache.log4j.Logger;


public abstract class PlayerImpl<T extends PlayerImpl<T>> implements Player, Serializable {

    private static final transient Logger log = Logger.getLogger(PlayerImpl.class);

    private static Random rnd = new Random();

    /**
     * Used to cancel waiting requests send to the player
     */
    protected boolean abort;

    protected final UUID playerId;
    protected String name;
    protected boolean human;
    protected int life;
    protected boolean wins;
    protected boolean loses;
    protected Library library;
    protected Cards sideboard;
    protected Cards hand;
    protected Cards graveyard;
    protected Abilities<Ability> abilities;
    protected Counters counters;
    protected int landsPlayed;
    protected int landsPerTurn = 1;
    protected int maxHandSize = 7;
    protected ManaPool manaPool;
    protected boolean passed;
    protected boolean passedTurn;
    protected int turns;
    protected int storedBookmark = -1;
    protected int priorityTimeLeft = Integer.MAX_VALUE;

    /**
     * This indicates that player passed all turns until his own turn starts.
     * Note! This differs from passedTurn as it doesn't care about spells and abilities in the stack and will pass them as well.
     */
    protected boolean passedAllTurns;

    // conceded or connection lost game
    protected boolean left;
    // quit match
    protected boolean quit;

    protected RangeOfInfluence range;
    protected Set<UUID> inRange = new HashSet<UUID>();
    protected boolean isTestMode = false;
    protected boolean canGainLife = true;
    protected boolean canLoseLife = true;
    protected boolean canPayLifeCost = true;
    protected boolean canPaySacrificeCost = true;
    protected boolean isGameUnderControl = true;
    protected UUID turnController;
    protected Set<UUID> playersUnderYourControl = new HashSet<UUID>();
    protected List<UUID> attachments = new ArrayList<UUID>();

    protected boolean topCardRevealed = false;
    
    // 800.4i When a player leaves the game, any continuous effects with durations that last until that player's next turn
    // or until a specific point in that turn will last until that turn would have begun. 
    // They neither expire immediately nor last indefinitely.
    protected boolean reachedNextTurnAfterLeaving = false;

    protected UserData userData;

    @Override
    public abstract T copy();

    public PlayerImpl(String name, RangeOfInfluence range) {
        this(UUID.randomUUID());
        this.name = name;
        this.range = range;
        hand = new CardsImpl(Zone.HAND);
        graveyard = new CardsImpl(Zone.GRAVEYARD);
        abilities = new AbilitiesImpl<Ability>();
        counters = new Counters();
        manaPool = new ManaPool();
        library = new Library(playerId);
        sideboard = new CardsImpl(Zone.OUTSIDE);
    }

    protected PlayerImpl(UUID id) {
        this.playerId = id;
    }

    public PlayerImpl(final PlayerImpl<T> player) {
        this.abort = player.abort;
        this.playerId = player.playerId;

        this.name = player.name;
        this.human = player.human;
        this.life = player.life;
        this.wins = player.wins;
        this.loses = player.loses;

        this.library = player.library.copy();
        this.sideboard = player.sideboard.copy();
        this.hand = player.hand.copy();
        this.graveyard = player.graveyard.copy();
        this.abilities = player.abilities.copy();
        this.counters = player.counters.copy();

        this.landsPlayed = player.landsPlayed;
        this.landsPerTurn = player.landsPerTurn;
        this.maxHandSize = player.maxHandSize;
        this.manaPool = player.manaPool.copy();
        this.turns = player.turns;

        this.left = player.left;
        this.quit = player.quit;
        this.range = player.range;
        this.canGainLife = player.canGainLife;
        this.canLoseLife = player.canLoseLife;
        this.attachments.addAll(player.attachments);

        this.inRange.addAll(player.inRange);
        this.userData = player.userData;
        this.canPayLifeCost = player.canPayLifeCost;
        this.canPaySacrificeCost = player.canPaySacrificeCost;
        this.storedBookmark = player.storedBookmark;

        this.topCardRevealed = player.topCardRevealed;
        this.playersUnderYourControl.clear();
        this.playersUnderYourControl.addAll(player.playersUnderYourControl);
        this.isTestMode = player.isTestMode;
        this.isGameUnderControl = player.isGameUnderControl;

        this.turnController = player.turnController;
        this.passed = player.passed;
        
        this.passedTurn = player.passedTurn;
        this.passedAllTurns = player.passedAllTurns;
        this.priorityTimeLeft = player.getPriorityTimeLeft();
        this.reachedNextTurnAfterLeaving = player.reachedNextTurnAfterLeaving;
    }

    @Override
    public void restore(Player player) {
        this.name = player.getName();
        this.human = player.isHuman();
        this.life = player.getLife();
        this.wins = player.hasWon();
        this.loses = player.hasLost();

        this.library = player.getLibrary().copy();
        this.sideboard = player.getSideboard().copy();
        this.hand = player.getHand().copy();
        this.graveyard = player.getGraveyard().copy();
        this.abilities = player.getAbilities().copy();
        this.counters = player.getCounters().copy();

        this.landsPlayed = player.getLandsPlayed();
        this.landsPerTurn = player.getLandsPerTurn();
        this.maxHandSize = player.getMaxHandSize();
        this.manaPool = player.getManaPool().copy();
        this.turns = player.getTurns();

        this.left = player.hasLeft();
        this.quit = player.hasQuit();
        this.range = player.getRange();
        this.canGainLife = player.isCanGainLife();
        this.canLoseLife = player.isCanLoseLife();
        this.attachments.clear();
        this.attachments.addAll(player.getAttachments());

        this.inRange.clear();
        this.inRange.addAll(player.getInRange());
        this.userData = player.getUserData();
        this.canPayLifeCost = player.canPayLifeCost();
        this.canPaySacrificeCost = player.canPaySacrificeCost();
        this.storedBookmark = player.getStoredBookmark();

        this.topCardRevealed = player.isTopCardRevealed();
        this.playersUnderYourControl.clear();
        this.playersUnderYourControl.addAll(player.getPlayersUnderYourControl());
        this.isTestMode = player.isTestMode();
        this.isGameUnderControl = player.isGameUnderControl();

        this.turnController = player.getTurnControlledBy();
        this.passed = player.isPassed();
        this.priorityTimeLeft = player.getPriorityTimeLeft();
        this.reachedNextTurnAfterLeaving = player.hasReachedNextTurnAfterLeaving();
    }

    @Override
    public void useDeck(Deck deck, Game game) {
        library.clear();
        library.addAll(deck.getCards(), game);
        sideboard.clear();
        for (Card card : deck.getSideboard()) {
            sideboard.add(card);
        }
    }

    @Override
    public void init(Game game) {
        init(game, false);
    }

    @Override
    public void init(Game game, boolean testMode) {
        this.abort = false;
        if (!testMode) {
            this.hand.clear();
            this.graveyard.clear();
        }
        this.library.reset();
        this.abilities.clear();
        this.counters.clear();
        this.wins = false;
        this.loses = false;
        this.left = false;
        // quittet won't be reset because the player stays quit
        this.passed = false;
        this.passedTurn = false;
        this.passedAllTurns = false;
        this.canGainLife = true;
        this.canLoseLife = true;
        this.topCardRevealed = false;
        this.setLife(game.getLife(), game);
        this.setReachedNextTurnAfterLeaving(false);
        game.getState().getWatchers().add(new BloodthirstWatcher(playerId));

    }

    @Override
    public void reset() {
        this.abilities.clear();
        this.landsPerTurn = 1;
        this.maxHandSize = 7;
        this.canGainLife = true;
        this.canLoseLife = true;
        this.canPayLifeCost = true;
        this.canPaySacrificeCost = true;
        this.topCardRevealed = false;
    }

    @Override
    public Counters getCounters() {
        return counters;
    }

    @Override
    public void otherPlayerLeftGame(Game game) {
        findRange(game);
    }


    @Override
    public void beginTurn(Game game) {
        this.landsPlayed = 0;
        findRange(game);
    }

    @Override
    public RangeOfInfluence getRange() {
        return range;
    }

    protected void findRange(Game game) {
        //20100423 - 801.2c
        inRange.clear();
        if (range == RangeOfInfluence.ALL) {
            for (Player player: game.getPlayers().values()) {
                if (!player.hasLeft()) {
                    inRange.add(player.getId());
                }
            }
        }
        else {
            if ((range.getRange() * 2) + 1 >= game.getPlayers().size()) {
                for (Player player: game.getPlayers().values()) {
                    if (!player.hasLeft()) {
                        inRange.add(player.getId());
                    }
                }
            }
            else {
                inRange.add(playerId);
                PlayerList players = game.getState().getPlayerList(playerId);
                for (int i = 0; i < range.getRange(); i++) {
                    Player player = players.getNext(game);
                    while (player.hasLeft()) {
                        player = players.getNext(game);
                    }
                    inRange.add(player.getId());
                }
                players = game.getState().getPlayerList(playerId);
                for (int i = 0; i < range.getRange(); i++) {
                    Player player = players.getPrevious(game);
                    while (player.hasLeft()) {
                        player = players.getPrevious(game);
                    }
                    inRange.add(player.getId());
                }
            }
        }
    }

    @Override
    public Set<UUID> getInRange() {
        return inRange;
    }

    @Override
    public Set<UUID> getPlayersUnderYourControl() {
        return this.playersUnderYourControl;
    }

    @Override
    public void controlPlayersTurn(Game game, UUID playerId) {
        if (!playerId.equals(this.getId())) {
            this.playersUnderYourControl.add(playerId);
            Player player = game.getPlayer(playerId);
            if (!player.hasLeft()&& !player.hasLost()) {
                player.setGameUnderYourControl(false);
                player.setTurnControlledBy(this.getId());
            }
            DelayedTriggeredAbility ability = new AtTheEndOfTurnStepPostDelayedTriggeredAbility(new LoseControlOnOtherPlayersControllerEffect());
            ability.setSourceId(getId());
            ability.setControllerId(getId());
            game.addDelayedTriggeredAbility(ability);
        }
    }

    @Override
    public void setTurnControlledBy(UUID playerId) {
        this.turnController = playerId;
    }

    @Override
    public UUID getTurnControlledBy() {
        return this.turnController;
    }

    @Override
    public void resetOtherTurnsControlled() {
        playersUnderYourControl.clear();
    }

    @Override
    public boolean isGameUnderControl() {
        return isGameUnderControl;
    }

    @Override
    public void setGameUnderYourControl(boolean value) {
        this.isGameUnderControl = value;
    }

    @Override
    public void endOfTurn(Game game) {
        this.passedTurn = false;
    }

    @Override
    public boolean canBeTargetedBy(MageObject source, Game game) {
        if (this.hasLost() || this.hasLeft()) {
            return false;
        }
        if (source != null) {
            if (abilities.containsKey(ShroudAbility.getInstance().getId())) {
                return false;
            }
            if (abilities.containsKey(HexproofAbility.getInstance().getId())) {
                UUID controllerId = null;
                if (source instanceof Permanent) {
                    controllerId = ((Permanent) source).getControllerId();
                } else if (source instanceof StackObject) {
                    controllerId = ((StackObject) source).getControllerId();
                }
                if (controllerId != null && game.getOpponents(this.playerId).contains(controllerId) &&
                        !game.getContinuousEffects().asThough(this.getId(), AsThoughEffectType.HEXPROOF, game)) {
                    return false;
                }
            }
            if (hasProtectionFrom(source, game)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean hasProtectionFrom(MageObject source, Game game) {
        for (ProtectionAbility ability: abilities.getProtectionAbilities()) {
            if (!ability.canTarget(source, game)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int drawCards(int num, Game game) {
        return game.doAction(new MageDrawAction(this, num, null));
    }

    @Override
    public int drawCards(int num, Game game, ArrayList<UUID> appliedEffects) {
        return game.doAction(new MageDrawAction(this, num, appliedEffects));
    }

    @Override
    public void discardToMax(Game game) {
        int cardsStart = hand.size();
        if (cardsStart > this.maxHandSize) {
            while (hand.size() > this.maxHandSize) {
                TargetDiscard target = new TargetDiscard(playerId);
                target.setTargetName(new StringBuilder(" card to discard (").append(hand.size() - this.maxHandSize).append(" in total)").toString());
                choose(Outcome.Discard, target, null, game);
                discard(hand.get(target.getFirstTarget(), game), null, game);
            }
            int discarded =cardsStart - hand.size();
            game.informPlayers(new StringBuilder(getName()).append(" discards ").append(discarded).append(discarded == 1?" card":" cards").append(" (cleanup)").toString());
        }
    }

    @Override
    public boolean putInHand(Card card, Game game) {
        if (card.getOwnerId().equals(playerId)) {
            this.hand.add(card);
            game.setZone(card.getId(), Zone.HAND);
        } else {
            return game.getPlayer(card.getOwnerId()).putInHand(card, game);
        }
        return true;
    }

    @Override
    public boolean removeFromHand(Card card, Game game) {
        hand.remove(card);
        return true;
    }

    @Override
    public boolean removeFromLibrary(Card card, Game game) {
        if (card == null) {
            return false;
        }
        library.remove(card.getId(), game);
        return true;
    }

    @Override
    public void discard(int amount, Ability source, Game game) {
        if (amount >= hand.size()) {
            int discardAmount = hand.size();
            while (hand.size() > 0) {
                discard(hand.get(hand.iterator().next(), game), source, game);
            }
            game.fireInformEvent(name + " discards " + Integer.toString(discardAmount) + " card" + (discardAmount > 1?"s":""));
            return;
        }
        int numDiscarded = 0;
        while (numDiscarded < amount) {
            if (hand.size() == 0) {
                break;
            }
            TargetDiscard target = new TargetDiscard(playerId);
            choose(Outcome.Discard, target, source.getSourceId(), game);
            Card card = hand.get(target.getFirstTarget(), game);
            if (card != null && discard(card, source, game)) {
                numDiscarded++;
            }
        }
        game.fireInformEvent(name + " discards " + Integer.toString(numDiscarded) + " card" + (numDiscarded > 1?"s":""));
    }

    @Override
    public boolean discard(Card card, Ability source, Game game) {
        //20100716 - 701.7
        if (card != null) {
            removeFromHand(card, game);
            card.moveToZone(Zone.GRAVEYARD, source==null?null:source.getId(), game, false);

            game.fireEvent(GameEvent.getEvent(GameEvent.EventType.DISCARDED_CARD, card.getId(), source==null?null:source.getId(), playerId));
            return true;
        }
        return false;
    }

    @Override
    public List<UUID> getAttachments() {
        return attachments;
    }

    @Override
    public boolean addAttachment(UUID permanentId, Game game) {
        if (!this.attachments.contains(permanentId)) {
            Permanent aura = game.getPermanent(permanentId);
            if (aura != null) {
                if (!game.replaceEvent(new GameEvent(GameEvent.EventType.ENCHANT_PLAYER, playerId, permanentId, aura.getControllerId()))) {
                    this.attachments.add(permanentId);
                    aura.attachTo(playerId, game);
                    game.fireEvent(new GameEvent(GameEvent.EventType.ENCHANTED_PLAYER, playerId, permanentId, aura.getControllerId()));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean removeAttachment(UUID permanentId, Game game) {
        if (this.attachments.contains(permanentId)) {
            Permanent aura = game.getPermanent(permanentId);
            if (aura != null) {
                if (!game.replaceEvent(new GameEvent(GameEvent.EventType.UNATTACH, playerId, permanentId, aura.getControllerId()))) {
                    this.attachments.remove(permanentId);
                    aura.attachTo(null, game);
                }
                game.fireEvent(new GameEvent(GameEvent.EventType.UNATTACHED, playerId, permanentId, aura.getControllerId()));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeFromBattlefield(Permanent permanent, Game game) {
        permanent.removeFromCombat(game);
        game.getBattlefield().removePermanent(permanent.getId());
        if (permanent.getAttachedTo() != null) {
            Permanent attachedTo = game.getPermanent(permanent.getAttachedTo());
            if (attachedTo != null) {
                attachedTo.removeAttachment(permanent.getId(), game);
            }
        }
        return true;
    }

    @Override
    public boolean putInGraveyard(Card card, Game game, boolean fromBattlefield) {
        if (card.getOwnerId().equals(playerId)) {
            this.graveyard.add(card);
        } else {
            return game.getPlayer(card.getOwnerId()).putInGraveyard(card, game, fromBattlefield);
        }
        return true;
    }

    @Override
    public boolean removeFromGraveyard(Card card, Game game) {
        this.graveyard.remove(card);
        return true;
    }


    @Override
    public boolean putCardsOnBottomOfLibrary(Cards cards, Game game, Ability source, boolean anyOrder) {
        if (cards.size() != 0) {
            if (!anyOrder) {
                for (UUID cardId : cards) {
                    Card card =game.getCard(cardId);
                    if (card != null) {
                        card.moveToZone(Zone.LIBRARY, source.getSourceId(), game, false);
                    }
                }
            } else {
                TargetCard target = new TargetCard(Zone.PICK, new FilterCard("card to put on the bottom of your library"));
                target.setRequired(true);
                while (cards.size() > 1) {
                    this.choose(Outcome.Neutral, cards, target, game);
                    Card chosenCard = cards.get(target.getFirstTarget(), game);
                    if (chosenCard != null) {
                        cards.remove(chosenCard);
                        chosenCard.moveToZone(Zone.LIBRARY, source.getSourceId(), game, false);
                    }
                    target.clearChosen();
                }
                if (cards.size() == 1) {
                    Card chosenCard = cards.get(cards.iterator().next(), game);
                    chosenCard.moveToZone(Zone.LIBRARY, source.getSourceId(), game, false);
                }
            }
        }
        return true;
    }


    @Override
    public boolean cast(SpellAbility ability, Game game, boolean noMana) {
        if (!ability.getSpellAbilityType().equals(SpellAbilityType.BASE)) {
            ability = chooseSpellAbilityForCast(ability, game, noMana);
        }
        //20091005 - 601.2a
        Card card = game.getCard(ability.getSourceId());
        if (card != null) {
            if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.CAST_SPELL, ability.getId(), ability.getSourceId(), playerId))) {
                int bookmark = game.bookmarkState();
                Zone fromZone = game.getState().getZone(card.getId());
                card.cast(game, fromZone, ability, playerId);
                Spell spell = game.getStack().getSpell(ability.getId());
                if (spell.activate(game, noMana)) {
                    GameEvent event = GameEvent.getEvent(GameEvent.EventType.SPELL_CAST, spell.getSpellAbility().getId(), spell.getSpellAbility().getSourceId(), playerId);
                    event.setZone(fromZone);
                    game.fireEvent(event);
                    game.fireInformEvent(new StringBuilder(name).append(spell.getActivatedMessage(game)).toString());
                    game.removeBookmark(bookmark);
                    resetStoredBookmark(game);
                    return true;
                }
                game.restoreState(bookmark);
            }
        }
        return false;
    }

    @Override
    public SpellAbility chooseSpellAbilityForCast(SpellAbility ability, Game game, boolean noMana) {
        return ability;
    }

    @Override
    public boolean playLand(Card card, Game game) {
        //20091005 - 305.1
        if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.PLAY_LAND, card.getId(), playerId))) {
            int bookmark = game.bookmarkState();
            Zone zone = game.getState().getZone(card.getId());
            if (card.putOntoBattlefield(game, zone, null, playerId)) {
                landsPlayed++;
                game.fireEvent(GameEvent.getEvent(GameEvent.EventType.LAND_PLAYED, card.getId(), playerId));
                game.fireInformEvent(name + " plays " + card.getName());
                game.removeBookmark(bookmark);
                resetStoredBookmark(game);
                return true;
            }
            game.restoreState(bookmark);
        }
        return false;
    }

    protected boolean playManaAbility(ManaAbility ability, Game game) {
        if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.ACTIVATE_ABILITY, ability.getId(), ability.getSourceId(), playerId))) {
            int bookmark = game.bookmarkState();
            if (ability.activate(game, false)) {
                ability.resolve(game);
                // #169
                if (storedBookmark == -1) {
                    setStoredBookmark(bookmark);
                }
                //game.removeBookmark(bookmark);
                return true;
            }
            game.restoreState(bookmark);
        }
        return false;
    }

    protected boolean playAbility(ActivatedAbility ability, Game game) {
        //20091005 - 602.2a
        if (ability.isUsesStack()) {
            if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.ACTIVATE_ABILITY, ability.getId(), ability.getSourceId(), playerId))) {
                int bookmark = game.bookmarkState();
                ability.newId();
                game.getStack().push(new StackAbility(ability, playerId));
                if (ability.activate(game, false)) {
                    game.fireEvent(GameEvent.getEvent(GameEvent.EventType.ACTIVATED_ABILITY, ability.getId(), ability.getSourceId(), playerId));
                    game.fireInformEvent(name + ability.getActivatedMessage(game));
                    game.removeBookmark(bookmark);
                    resetStoredBookmark(game);
                    return true;
                }
                game.restoreState(bookmark);
            }
        } else {
            int bookmark = game.bookmarkState();
            if (ability.activate(game, false)) {
                ability.resolve(game);
                game.removeBookmark(bookmark);
                resetStoredBookmark(game);
                return true;
            }
            game.restoreState(bookmark);
        }
        return false;
    }

    protected boolean specialAction(SpecialAction action, Game game) {
        //20091005 - 114
        if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.ACTIVATE_ABILITY, action.getSourceId(), action.getId(), playerId))) {
            int bookmark = game.bookmarkState();
            if (action.activate(game, false)) {
                game.fireEvent(GameEvent.getEvent(GameEvent.EventType.ACTIVATED_ABILITY, action.getSourceId(), action.getId(), playerId));
                game.fireInformEvent(name + action.getActivatedMessage(game));
                if (action.resolve(game)) {
                    game.removeBookmark(bookmark);
                    resetStoredBookmark(game);
                    return true;
                }
            }
            game.restoreState(bookmark);
        }
        return false;
    }

    @Override
    public boolean activateAbility(ActivatedAbility ability, Game game) {
        boolean result;
        if (!ability.canActivate(this.playerId, game)) {
            return false;
        }

        if (ability instanceof PassAbility) {
            pass(game);
            return true;
        }
        else if (ability instanceof PlayLandAbility) {
            Card card = hand.get(ability.getSourceId(), game);
            if (card == null) {
                card = game.getCard(ability.getSourceId());
            }
            result = playLand(card, game);
        }
        else if (ability instanceof SpecialAction) {
            result = specialAction((SpecialAction)ability.copy(), game);
        }
        else if (ability instanceof ManaAbility) {
            result = playManaAbility((ManaAbility)ability.copy(), game);
        }
        else if (ability instanceof SpellAbility) {
            result = cast((SpellAbility)ability, game, false);
        }
        else {
            result = playAbility((ActivatedAbility)ability.copy(), game);
        }

        //if player has taken an action then reset all player passed flags
        if (result) {
            game.getPlayers().resetPassed();
        }
        return result;
    }

    @Override
    public boolean triggerAbility(TriggeredAbility source, Game game) {
        if (source == null) {
            log.warn("Null source in triggerAbility method");
            throw new IllegalArgumentException("source TriggeredAbility  must not be null");
        }
        //20091005 - 603.3c, 603.3d
        int bookmark = game.bookmarkState();
        //FIXME: remove try\catch once we find out the reason for NPE on server
        TriggeredAbility ability = null;
        try {
            ability = source.copy();
        } catch (NullPointerException npe) {
            log.fatal("NPE for source=" + source);
            log.fatal("NPE for source=" + source.getRule());
            throw npe;
        }
        if (ability != null && ability.canChooseTarget(game)) {
            if (ability.isUsesStack()) {
                game.getStack().push(new StackAbility(ability, playerId));
                if (ability.activate(game, false)) {
                    game.removeBookmark(bookmark);
                    return true;
                }
            } else {
                if (ability.activate(game, false)) {
                    ability.resolve(game);
                    game.removeBookmark(bookmark);
                    return true;
                }
            }
        }
        game.restoreState(bookmark);
        return false;
    }

    protected LinkedHashMap<UUID, ActivatedAbility> getSpellAbilities(MageObject object, Zone zone, Game game) {
        LinkedHashMap<UUID, ActivatedAbility> useable = new LinkedHashMap<UUID, ActivatedAbility>();
        for (Ability ability: object.getAbilities()) {
            if (ability instanceof SpellAbility) {
                if (((SpellAbility) ability).getSpellAbilityType().equals(SpellAbilityType.SPLIT_FUSED)) {
                    if (zone.equals(Zone.HAND)) {
                        // Fix so you don't need to choose Fuse twice
                        useable.clear();
                        useable.put(ability.getId(), (SpellAbility) ability);
                        return useable;
                    } else {
                        // Fuse only allowed from hand
                        continue;
                    }
                }
                if (((SpellAbility) ability).getSpellAbilityType().equals(SpellAbilityType.SPLIT)) {
                    continue;
                }
                useable.put(ability.getId(), (SpellAbility) ability);
            }
        }
        return useable;
    }


    protected LinkedHashMap<UUID, ActivatedAbility> getUseableActivatedAbilities(MageObject object, Zone zone, Game game) {
        LinkedHashMap<UUID, ActivatedAbility> useable = new LinkedHashMap<UUID, ActivatedAbility>();
        if (!(object instanceof Permanent) || ((Permanent)object).canUseActivatedAbilities(game)) {
            for (ActivatedAbility ability: object.getAbilities().getActivatedAbilities(zone)) {

                if (ability.canActivate(playerId, game)) {
                    useable.put(ability.getId(), ability);
                }
            }
            if (zone != Zone.HAND) {
                if (zone != Zone.BATTLEFIELD && game.getContinuousEffects().asThough(object.getId(), AsThoughEffectType.CAST, game)) {
                    for (ActivatedAbility ability: object.getAbilities().getActivatedAbilities(Zone.HAND)) {
                        ability.setControllerId(this.getId());
                        useable.put(ability.getId(), ability);
                    }
                }
            }

            Abilities<ActivatedAbility> otherAbilities = game.getState().getOtherAbilities(object.getId(), zone);
            if (otherAbilities != null) {
                for (ActivatedAbility ability: otherAbilities) {
                    Card card = game.getCard(ability.getSourceId());
                    if (card.isSplitCard() && ability instanceof FlashbackAbility) {
                        FlashbackAbility flashbackAbility;
                        if (card.getCardType().contains(CardType.INSTANT)) {
                            flashbackAbility = new FlashbackAbility(((SplitCard) card).getLeftHalfCard().getManaCost(), TimingRule.INSTANT);
                        }
                        else {
                            flashbackAbility = new FlashbackAbility(((SplitCard) card).getLeftHalfCard().getManaCost(), TimingRule.SORCERY);
                        }
                        flashbackAbility.setSourceId(card.getId());
                        flashbackAbility.setControllerId(card.getOwnerId());
                        flashbackAbility.setSpellAbilityType(SpellAbilityType.SPLIT_LEFT);
                        flashbackAbility.setAbilityName(((SplitCard) card).getLeftHalfCard().getName());
                        useable.put(flashbackAbility.getId(), flashbackAbility);
                        if (card.getCardType().contains(CardType.INSTANT)) {
                            flashbackAbility = new FlashbackAbility(((SplitCard) card).getRightHalfCard().getManaCost(), TimingRule.INSTANT);
                        }
                        else {
                            flashbackAbility = new FlashbackAbility(((SplitCard) card).getRightHalfCard().getManaCost(), TimingRule.SORCERY);
                        }
                        flashbackAbility.setSourceId(card.getId());
                        flashbackAbility.setControllerId(card.getOwnerId());
                        flashbackAbility.setSpellAbilityType(SpellAbilityType.SPLIT_RIGHT);
                        flashbackAbility.setAbilityName(((SplitCard) card).getRightHalfCard().getName());
                        useable.put(flashbackAbility.getId(), flashbackAbility);

                    } else {
                        useable.put(ability.getId(), ability);
                    }
                }
            }
        }
        return useable;
    } 

    protected LinkedHashMap<UUID, ManaAbility> getUseableManaAbilities(MageObject object, Zone zone, Game game) {
        LinkedHashMap<UUID, ManaAbility> useable = new LinkedHashMap<UUID, ManaAbility>();
        for (ManaAbility ability: object.getAbilities().getManaAbilities(zone)) {
            if (ability.canActivate(playerId, game)) {
                useable.put(ability.getId(), ability);
            }
        }
        return useable;
    }

    @Override
    public int getLandsPlayed() {
        return landsPlayed;
    }

    @Override
    public boolean canPlayLand() {
        //20091005 - 114.2a
        return landsPlayed < landsPerTurn;
    }

    protected boolean isActivePlayer(Game game) {
        return game.getActivePlayerId().equals(this.playerId);
    }

    @Override
    public void shuffleLibrary(Game game) {
        if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.SHUFFLE_LIBRARY, playerId, playerId))) {
            this.library.shuffle();
            game.fireEvent(GameEvent.getEvent(GameEvent.EventType.LIBRARY_SHUFFLED, playerId, playerId));
        }
    }

    @Override
    public void revealCards(String name, Cards cards, Game game) {
        game.getState().getRevealed().add(name, cards);
    }

    @Override
    public void lookAtCards(String name, Cards cards, Game game) {
        game.getState().getLookedAt(this.playerId).add(name, cards);
        game.fireUpdatePlayersEvent();
    }

    @Override
    public void phasing(Game game) {
        //20091005 - 502.1
        for (Permanent permanent: game.getBattlefield().getPhasedIn(playerId)) {
            permanent.phaseOut(game);
        }
        for (Permanent permanent: game.getBattlefield().getPhasedOut(playerId)) {
            permanent.phaseIn(game);
        }
    }

    @Override
    public void untap(Game game) {
        // create list of all "notMoreThan" effects to track which one are consumed
        HashMap<Entry<RestrictionUntapNotMoreThanEffect, HashSet<Ability>>, Integer> notMoreThanEffectsUsage = new HashMap<Entry<RestrictionUntapNotMoreThanEffect, HashSet<Ability>>, Integer>();
        for (Entry<RestrictionUntapNotMoreThanEffect, HashSet<Ability>> restrictionEffect: game.getContinuousEffects().getApplicableRestrictionUntapNotMoreThanEffects(this, game).entrySet()) {
            notMoreThanEffectsUsage.put(restrictionEffect, new Integer(restrictionEffect.getKey().getNumber()));
        }

        if (!notMoreThanEffectsUsage.isEmpty()) {
            // create list of all permanents that can be untapped generally
            List<Permanent> canBeUntapped = new ArrayList<Permanent>();
            for (Permanent permanent: game.getBattlefield().getAllActivePermanents(playerId)) {
                boolean untap = true;
                for (RestrictionEffect effect: game.getContinuousEffects().getApplicableRestrictionEffects(permanent, game).keySet()) {
                    untap &= effect.canBeUntapped(permanent, game);
                }
                if (untap) {
                    canBeUntapped.add(permanent);
                }
            }
            // selected permanents to untap
            List<Permanent> selectedToUntap = new ArrayList<Permanent>();

            // player can cancel the seletion of an effect to use a prefered order of restriction effects
            boolean playerCanceledSelection;
            do {
                playerCanceledSelection = false;
                // select permanents to untap to consume the "notMoreThan" effects
                for(Map.Entry<Entry<RestrictionUntapNotMoreThanEffect, HashSet<Ability>>, Integer> handledEntry: notMoreThanEffectsUsage.entrySet()) {
                    // select a permanent to untap for this entry
                    int numberToUntap = handledEntry.getValue().intValue();
                    if (numberToUntap > 0) {

                        List<Permanent> leftForUntap = getPermanentsThatCanBeUntapped(game, canBeUntapped, handledEntry.getKey().getKey(), notMoreThanEffectsUsage);

                        FilterControlledPermanent filter = handledEntry.getKey().getKey().getFilter().copy();
                        String message = filter.getMessage();
                        // omitt already from other untap effects selected permanents
                        for (Permanent permanent: selectedToUntap) {
                              filter.add(Predicates.not(new PermanentIdPredicate(permanent.getId())));
                        }
                        // while targets left and there is still allowed to untap
                        while (leftForUntap.size() > 0 && numberToUntap > 0) {
                            // player has to select the permanent he wants to untap for this restriction
                            Ability ability = handledEntry.getKey().getValue().iterator().next();
                            if (ability != null) {
                                StringBuilder sb = new StringBuilder(message).append(" to untap").append(" (").append(Math.min(leftForUntap.size(), numberToUntap)).append(" in total");
                                MageObject effectSource = game.getObject(ability.getSourceId());
                                if (effectSource != null) {
                                    sb.append(" from ").append(effectSource.getName()).toString();
                                }
                                sb.append(")");
                                filter.setMessage(sb.toString());
                                Target target = new TargetPermanent(filter);
                                if (!this.chooseTarget(Outcome.Untap, target, ability, game)) {
                                    // player canceled, go on with the next effect (if no other effect available, this effect will be active again)
                                    playerCanceledSelection = true;
                                    break;
                                }
                                Permanent selectedPermanent = game.getPermanent(target.getFirstTarget());
                                if (leftForUntap.contains(selectedPermanent)) {
                                    selectedToUntap.add(selectedPermanent);
                                    numberToUntap--;
                                    // don't allow to select same permanent twice
                                    filter.add(Predicates.not(new PermanentIdPredicate(selectedPermanent.getId())));
                                    // reduce available untap numbers from other "UntapNotMoreThan" effects if selected permanent applies to their filter too
                                    for (Entry<Entry<RestrictionUntapNotMoreThanEffect, HashSet<Ability>>, Integer> notMoreThanEffect : notMoreThanEffectsUsage.entrySet()) {
                                        if (notMoreThanEffect.getValue().intValue() > 0 && notMoreThanEffect.getKey().getKey().getFilter().match(selectedPermanent, game)) {
                                            notMoreThanEffect.setValue(new Integer(notMoreThanEffect.getValue().intValue() - 1));
                                        }
                                    }
                                    // update the left for untap list
                                    leftForUntap = getPermanentsThatCanBeUntapped(game, canBeUntapped, handledEntry.getKey().getKey(), notMoreThanEffectsUsage);
                                    // remove already selected permanents
                                    for (Permanent permanent :selectedToUntap) {
                                        if (leftForUntap.contains(permanent)) {
                                            leftForUntap.remove(permanent);
                                        }
                                    }

                                } else {
                                    // player selected an permanent that is restricted by another effect, disallow it (so AI can select another one)
                                    filter.add(Predicates.not(new PermanentIdPredicate(selectedPermanent.getId())));
                                    if (this.isHuman()) {
                                        game.informPlayer(this, "This permanent can't be untapped because of other restricting effect.");
                                    }
                                }
                            }
                        }
                    }
                }

            } while (playerCanceledSelection);

            // show in log which permanents were selected to untap
            for(Permanent permanent :selectedToUntap) {
                game.informPlayers(new StringBuilder(this.getName()).append(" untapped ").append(permanent.getName()).toString());
            }
            // untap if permanent is not concerned by notMoreThan effects or is included in the selectedToUntapList
            for (Permanent permanent: canBeUntapped) {
                boolean doUntap = true;
                if (!selectedToUntap.contains(permanent)) {
                    // if the permanent is covered by one of the restriction effects, don't untap it
                    for (Entry<Entry<RestrictionUntapNotMoreThanEffect, HashSet<Ability>>, Integer> notMoreThanEffect : notMoreThanEffectsUsage.entrySet()) {
                        if (notMoreThanEffect.getKey().getKey().getFilter().match(permanent, game)) {
                            doUntap = false;
                            break;
                        }
                    }
                }
                if (permanent != null && doUntap) {
                    permanent.untap(game);
                }

            }


        } else {
            //20091005 - 502.2
            for (Permanent permanent: game.getBattlefield().getAllActivePermanents(playerId)) {
                boolean untap = true;
                for (RestrictionEffect effect: game.getContinuousEffects().getApplicableRestrictionEffects(permanent, game).keySet()) {
                    untap &= effect.canBeUntapped(permanent, game);
                }
                if (untap) {
                    permanent.untap(game);
                }
            }
        }
    }

    private List<Permanent> getPermanentsThatCanBeUntapped(Game game, List<Permanent> canBeUntapped, RestrictionUntapNotMoreThanEffect handledEffect, HashMap<Entry<RestrictionUntapNotMoreThanEffect, HashSet<Ability>>, Integer> notMoreThanEffectsUsage) {
        List<Permanent> leftForUntap = new ArrayList<Permanent>();
        // select permanents that can still be untapped
        for (Permanent permanent: canBeUntapped) {
            if (handledEffect.getFilter().match(permanent, game)) { // matches the restricted permanents of handled entry
                boolean canBeSelected = true;
                // check if the permanent is restriced by another restriction that has left no permanent
                for (Entry<Entry<RestrictionUntapNotMoreThanEffect, HashSet<Ability>>, Integer> notMoreThanEffect : notMoreThanEffectsUsage.entrySet()) {
                    if (notMoreThanEffect.getKey().getKey().getFilter().match(permanent, game) && notMoreThanEffect.getValue().intValue() == 0) {
                        canBeSelected = false;
                        break;
                    }
                }
                if (canBeSelected) {
                    leftForUntap.add(permanent);
                }
            }
        }
        return leftForUntap;
    }

    @Override
    public UUID getId() {
        return playerId;
    }

    @Override
    public Cards getHand() {
        return hand;
    }

    @Override
    public Cards getGraveyard() {
        return graveyard;
    }

    @Override
    public ManaPool getManaPool() {
        return this.manaPool;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isHuman() {
        return human;
    }

    @Override
    public Library getLibrary() {
        return library;
    }

    @Override
    public Cards getSideboard() {
        return sideboard;
    }

    @Override
    public int getLife() {
        return life;
    }

    @Override
    public void setLife(int life, Game game) {
        // rule 118.5
        if (life > this.life) {
            gainLife(life - this.life, game);
        } else if (life < this.life) {
            loseLife(this.life - life, game);
        }
    }

    @Override
    public void setLifeTotalCanChange(boolean lifeTotalCanChange) {
        this.canGainLife = lifeTotalCanChange;
        this.canLoseLife = lifeTotalCanChange;
    }

    @Override
    public boolean isLifeTotalCanChange() {
        return canGainLife | canLoseLife;
    }

    @Override
    public boolean isCanLoseLife() {
        return canLoseLife;
    }

    @Override
    public void setCanLoseLife(boolean canLoseLife) {
        this.canLoseLife = canLoseLife;
    }

    @Override
    public int loseLife(int amount, Game game) {
        if (!canLoseLife) {
            return 0;
        }
        GameEvent event = new GameEvent(GameEvent.EventType.LOSE_LIFE, playerId, playerId, playerId, amount, false);
        if (!game.replaceEvent(event)) {
            this.life -= event.getAmount();
            game.fireEvent(GameEvent.getEvent(GameEvent.EventType.LOST_LIFE, playerId, playerId, playerId, amount));
            return amount;
        }
        return 0;
    }

    @Override
    public boolean isCanGainLife() {
        return canGainLife;
    }

    @Override
    public void setCanGainLife(boolean canGainLife) {
        this.canGainLife = canGainLife;
    }

    @Override
    public int gainLife(int amount, Game game) {
        if (!canGainLife) {
            return 0;
        }
        GameEvent event = new GameEvent(GameEvent.EventType.GAIN_LIFE, playerId, playerId, playerId, amount, false);
        if (!game.replaceEvent(event)) {
            this.life += event.getAmount();
            game.fireEvent(GameEvent.getEvent(GameEvent.EventType.GAINED_LIFE, playerId, playerId, playerId, event.getAmount()));
            return event.getAmount();
        }
        return 0;
    }
    @Override
    public int damage(int damage, UUID sourceId, Game game, boolean combatDamage, boolean preventable) {
        return doDamage(damage, sourceId, game, combatDamage, preventable, null);
    }

    @Override
    public int damage(int damage, UUID sourceId, Game game, boolean combatDamage, boolean preventable, ArrayList<UUID> appliedEffects) {
        return doDamage(damage, sourceId, game, combatDamage, preventable, appliedEffects);
    }


    @SuppressWarnings({"null", "ConstantConditions"})
    private int doDamage(int damage, UUID sourceId, Game game, boolean combatDamage, boolean preventable, ArrayList<UUID> appliedEffects) {
        if (damage > 0 && canDamage(game.getObject(sourceId), game)) {
            GameEvent event = new DamagePlayerEvent(playerId, sourceId, playerId, damage, preventable, combatDamage);
            event.setAppliedEffects(appliedEffects);
            if (!game.replaceEvent(event)) {
                int actualDamage = event.getAmount();
                if (actualDamage > 0) {
                    Permanent source = game.getPermanent(sourceId);
                    if(source == null){
                        MageObject lastKnownInformation = game.getLastKnownInformation(sourceId, Zone.BATTLEFIELD);
                        if(lastKnownInformation != null &&  lastKnownInformation instanceof Permanent){
                            source = (Permanent) lastKnownInformation;
                        }
                    }
                    if (source != null && (source.getAbilities().containsKey(InfectAbility.getInstance().getId()))) {
                        addCounters(CounterType.POISON.createInstance(actualDamage), game);
                    } else {
                        GameEvent damageToLifeLossEvent = new GameEvent(EventType.DAMAGE_CAUSES_LIFE_LOSS, playerId, sourceId, playerId, actualDamage, combatDamage);
                        if (!game.replaceEvent(damageToLifeLossEvent)) {
                            this.loseLife(damageToLifeLossEvent.getAmount(), game);
                        }
                    }
                    if (source != null && source.getAbilities().containsKey(LifelinkAbility.getInstance().getId())) {
                        Player player = game.getPlayer(source.getControllerId());
                        player.gainLife(actualDamage, game);
                    }
                    game.fireEvent(new DamagedPlayerEvent(playerId, sourceId, playerId, actualDamage, combatDamage));
                    return actualDamage;
                }
            }
        }
        return 0;
    }

    @Override
    public void addCounters(Counter counter, Game game) {
        int amount = counter.getCount();
        for (int i = 0; i < amount; i++) {
            Counter eventCounter = counter.copy();
            eventCounter.remove(amount - 1);
            if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.ADD_COUNTER, playerId, playerId, counter.getName(), counter.getCount()))) {
                counters.addCounter(eventCounter);
                game.fireEvent(GameEvent.getEvent(EventType.COUNTER_ADDED, playerId, playerId, counter.getName(), counter.getCount()));
            }
        }
    }

    protected boolean canDamage(MageObject source, Game game) {
        for (ProtectionAbility ability: abilities.getProtectionAbilities()) {
            if (!ability.canTarget(source, game)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Abilities getAbilities() {
        return this.abilities;
    }

    @Override
    public void addAbility(Ability ability) {
        ability.setSourceId(playerId);
        this.abilities.add(ability);
    }

    @Override
    public int getLandsPerTurn() {
        return this.landsPerTurn;
    }

    @Override
    public void setLandsPerTurn(int landsPerTurn) {
        this.landsPerTurn = landsPerTurn;
    }

    @Override
    public int getMaxHandSize() {
        return maxHandSize;
    }

    @Override
    public void setMaxHandSize(int maxHandSize) {
        this.maxHandSize = maxHandSize;
    }

    @Override
    public void setResponseString(String responseString) {}

    @Override
    public void setResponseUUID(UUID responseUUID) {}

    @Override
    public void setResponseBoolean(Boolean responseBoolean) {}

    @Override
    public void setResponseInteger(Integer responseInteger) {}

    @Override
    public boolean isPassed() {
        return passed;
    }

    @Override
    public void pass(Game game) {
        this.passed = true;
        resetStoredBookmark(game);
    }

    @Override
    public boolean isEmptyDraw() {
        return library.isEmptyDraw();
    }

    @Override
    public void resetPassed() {
        if (!this.loses && !this.hasLeft()) {
            this.passed = false;
        }
        else {
            this.passed = true;
        }
    }

    @Override
    public void quit(Game game) {
        quit = true;
        this.concede(game);
    }

    @Override
    public void concede(Game game) {
        game.leave(playerId);
    }

    @Override
    public void passPriorityUntilNextYourTurn(Game game) {
        passedTurn = true;
        passedAllTurns = true;
        this.skip();
        log.debug("Passed priority for turns");
    }

    @Override
    public void passTurnPriority(Game game) {
        passedTurn = true;
        this.skip();
        log.debug("Passed priority for turn");
    }

    @Override
    public void restorePriority(Game game) {
        passedAllTurns = false;
        passedTurn = false;
        log.debug("Restore priority");
    }

    @Override
    public void leave() {
        this.passed = true;
        this.abort();
        this.loses = true;
        this.left = true;
        //20100423 - 800.4a
        this.hand.clear();
        this.graveyard.clear();
        this.library.clear();
    }
    
    @Override
    public boolean hasLeft() {
        return this.left;
    }

    @Override
    public void lost(Game game) {
        if (canLose(game)) {
            this.loses = true;
            //20100423 - 603.9
            if (!this.wins) {
                game.fireEvent(GameEvent.getEvent(GameEvent.EventType.LOST, null, null, playerId));
                game.informPlayers(new StringBuilder(this.getName()).append(" has lost the game.").toString());
            }
            if (!hasLeft()) {
                game.leave(playerId);
            }
        }
    }

    @Override
    public boolean canLose(Game game) {
        return !game.replaceEvent(new GameEvent(GameEvent.EventType.LOSES, null, null, playerId));
    }

    @Override
    public void won(Game game) {
        if (!game.replaceEvent(new GameEvent(GameEvent.EventType.WINS, null, null, playerId))) {
            if (!this.loses) {
                //20130501 - 800.7, 801.16
                // all opponents in range loose the game
                for (UUID opponentId: game.getOpponents(playerId)) {
                    Player opponent = game.getPlayer(opponentId);
                    if (opponent != null && !opponent.hasLost()) {
                        opponent.lost(game);
                    }
                }
                // if no more opponents alive, you win and the game ends
                int opponentsAlive = 0;
                for (UUID opponentId: game.getOpponents(playerId)) {
                    Player opponent = game.getPlayer(opponentId);
                    if (!opponent.hasLost()) {
                        opponentsAlive++;
                    }
                }
                if (opponentsAlive == 0) {
                    game.informPlayers(new StringBuilder(this.getName()).append(" has won the game").toString());
                    this.wins = true;
                    game.end();
                }
            }
        }
    }

    @Override
    public boolean hasLost() {
        return this.loses;
    }
    @Override
    public boolean isInGame() {
        return !hasLost() && !hasWon() && !hasLeft();
    }

    @Override
    public boolean hasWon() {
        if (!this.loses) {
            return this.wins;
        }
        else {
            return false;
        }
    }

    @Override
    public void declareAttacker(UUID attackerId, UUID defenderId, Game game) {
        Permanent attacker = game.getPermanent(attackerId);
        if (attacker != null && attacker.canAttack(game) && attacker.getControllerId().equals(playerId)) {
            if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.DECLARE_ATTACKER, defenderId, attackerId, playerId))) {
                game.getCombat().declareAttacker(attackerId, defenderId, game);
            }
        }
    }

    @Override
    public void declareBlocker(UUID defenderId, UUID blockerId, UUID attackerId, Game game) {
        Permanent blocker = game.getPermanent(blockerId);
        CombatGroup group = game.getCombat().findGroup(attackerId);
        if (blocker != null && group != null && group.canBlock(blocker, game)) {
            group.addBlocker(blockerId, playerId, game);
            game.getCombat().addBlockingGroup(blockerId, attackerId, playerId, game);
        }
    }

    @Override
    public boolean searchLibrary(TargetCardInLibrary target, Game game) {
        return searchLibrary(target, game, playerId);
    }

    @Override
    public boolean searchLibrary(TargetCardInLibrary target, Game game, UUID targetPlayerId) {
        //20091005 - 701.14c
        if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.SEARCH_LIBRARY, targetPlayerId, playerId))) {
            TargetCardInLibrary newTarget = target.copy();
            int count;
            Integer cardLimit = (Integer) game.getState().getValue("LibrarySearchLimit");
            if (cardLimit != null) {
                newTarget.setCardLimit(cardLimit);
                game.getState().setValue("LibrarySearchLimit", null);
                count = Math.min(library.count(target.getFilter(), game),cardLimit.intValue());
            } else {
                count = library.count(target.getFilter(), game);
            }

            if (count < target.getNumberOfTargets()) {
                newTarget.setMinNumberOfTargets(count);
            }
            if (newTarget.choose(Outcome.Neutral, playerId, targetPlayerId, game)) {
                target.getTargets().clear();
                for(UUID targetId: newTarget.getTargets()){
                    target.add(targetId, game);
                }
                game.fireEvent(GameEvent.getEvent(GameEvent.EventType.LIBRARY_SEARCHED, targetPlayerId, playerId));
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean flipCoin(Game game) {
            return this.flipCoin(game, null);
    }
    
    /**
     *
     * @return true if player won the toss
     */
    @Override
    public boolean flipCoin(Game game, ArrayList<UUID> appliedEffects) {
        boolean result = rnd.nextBoolean();
        game.informPlayers("[Flip a coin] " + getName() + (result ? " won (head)." : " lost (tail)."));
        GameEvent event = new GameEvent(GameEvent.EventType.FLIP_COIN, playerId, null, playerId, 0, result);
        event.setAppliedEffects(appliedEffects);
        game.replaceEvent(event);
        return event.getFlag();
    }

    @Override
    public List<Permanent> getAvailableAttackers(Game game) {
        FilterCreatureForCombat filter = new FilterCreatureForCombat();
        List<Permanent> attackers = game.getBattlefield().getAllActivePermanents(filter, playerId, game);
        for (Iterator<Permanent> i = attackers.iterator(); i.hasNext();) {
            Permanent entry = i.next();
            if (!entry.canAttack(game)) {
                i.remove();
            }
        }
        return attackers;
    }

    @Override
    public List<Permanent> getAvailableBlockers(Game game) {
        FilterCreatureForCombatBlock blockFilter = new FilterCreatureForCombatBlock();
        List<Permanent> blockers = game.getBattlefield().getAllActivePermanents(blockFilter, playerId, game);
        return blockers;
    }

    protected ManaOptions getManaAvailable(Game game) {
        ManaOptions available = new ManaOptions();

        List<Permanent> manaPerms = this.getAvailableManaProducers(game);
        for (Permanent perm: manaPerms) {
            available.addMana(perm.getAbilities().getAvailableManaAbilities(Zone.BATTLEFIELD, game), game);
        }

        List<Permanent> manaPermsWithCost = this.getAvailableManaProducersWithCost(game);
        for (Permanent perm: manaPermsWithCost) {
            available.addManaWithCost(perm.getAbilities().getAvailableManaAbilities(Zone.BATTLEFIELD, game), game);
        }
        return available;
    }

    // returns only mana producers that don't require mana payment
    protected List<Permanent> getAvailableManaProducers(Game game) {
        List<Permanent> result = new ArrayList<Permanent>();
        for (Permanent permanent: game.getBattlefield().getAllActivePermanents(playerId)) {
            boolean canAdd = false;
            for (ManaAbility ability: permanent.getAbilities().getManaAbilities(Zone.BATTLEFIELD)) {
                if (ability.canActivate(playerId, game)) {
                    canAdd = true;
                }
                if (!ability.getManaCosts().isEmpty()) {
                    canAdd = false;
                    break;
                }
            }
            if (canAdd) {
                result.add(permanent);
            }
        }
        return result;
    }

    // returns only mana producers that require mana payment
    protected List<Permanent> getAvailableManaProducersWithCost(Game game) {
        List<Permanent> result = new ArrayList<Permanent>();
        for (Permanent permanent: game.getBattlefield().getAllActivePermanents(playerId)) {
            for (ManaAbility ability: permanent.getAbilities().getManaAbilities(Zone.BATTLEFIELD)) {
                if (ability.canActivate(playerId, game) && !ability.getManaCosts().isEmpty()) {
                    result.add(permanent);
                    break;
                }
            }
        }
        return result;
    }

    protected boolean canPlay(ActivatedAbility ability, ManaOptions available, Game game) {
        if (!(ability instanceof ManaAbility) && ability.canActivate(playerId, game)) {
            Ability copy = ability.copy();
            game.getContinuousEffects().costModification(copy, game);

            Card card = game.getCard(ability.getSourceId());
            if (card != null) {
                for (Ability ability0 : card.getAbilities()) {
                    if (ability0 instanceof AdjustingSourceCosts) {
                        ((AdjustingSourceCosts)ability0).adjustCosts(copy, game);
                    }
                }
            }

            ManaOptions abilityOptions = copy.getManaCostsToPay().getOptions();
            if (abilityOptions.size() == 0) {
                return true;
            }
            else {
                for (Mana mana: abilityOptions) {
                    for (Mana avail: available) {
                        if (mana.enough(avail)) {
                            return true;
                        }
                    }
                }
            }

            for (AlternativeCost cost: ability.getAlternativeCosts()) {
                if (cost.isAvailable(game, ability) && cost.canPay(ability.getSourceId(), playerId, game)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Ability> getPlayable(Game game, boolean hidden) {
        List<Ability> playable = new ArrayList<Ability>();

        ManaOptions available = getManaAvailable(game);
        available.addMana(manaPool.getMana());

        if (hidden) {
            for (Card card: hand.getUniqueCards(game)) {
                for (ActivatedAbility ability: card.getAbilities().getActivatedAbilities(Zone.HAND)) {
                    if (canPlay(ability, available, game)) {
                        playable.add(ability);
                    }
                }
            }
        }
        for (Card card: graveyard.getUniqueCards(game)) {
            for (ActivatedAbility ability: card.getAbilities().getActivatedAbilities(Zone.GRAVEYARD)) {
                if (canPlay(ability, available, game)) {
                    playable.add(ability);
                }
            }
            if (game.getContinuousEffects().asThough(card.getId(), AsThoughEffectType.CAST, game)) {
                for (ActivatedAbility ability: card.getAbilities().getActivatedAbilities(Zone.HAND)) {
                    if (ability instanceof SpellAbility || ability instanceof PlayLandAbility) {
                        playable.add(ability);
                    }
                }
            }
        }
        for (ExileZone exile: game.getExile().getExileZones()) {
            for (Card card: exile.getCards(game)) {
                if (game.getContinuousEffects().asThough(card.getId(), AsThoughEffectType.CAST, game)) {
                    for (ActivatedAbility ability: card.getAbilities().getActivatedAbilities(Zone.HAND)) {
                        if (ability instanceof SpellAbility || ability instanceof PlayLandAbility) {
                            playable.add(ability);
                        }
                    }
                }
            }
        }
        for (Cards cards: game.getState().getRevealed().values()) {
            for (Card card: cards.getCards(game)) {
                if (game.getContinuousEffects().asThough(card.getId(), AsThoughEffectType.CAST, game)) {
                    for (ActivatedAbility ability: card.getAbilities().getActivatedAbilities(Zone.HAND)) {
                        if (ability instanceof SpellAbility || ability instanceof PlayLandAbility) {
                            playable.add(ability);
                        }
                    }
                }
            }
        }
        // eliminate duplicate activated abilities
        Map<String, Ability> playableActivated = new HashMap<String, Ability>();
        for (Permanent permanent: game.getBattlefield().getAllActivePermanents(playerId)) {
            for (ActivatedAbility ability: permanent.getAbilities().getActivatedAbilities(Zone.BATTLEFIELD)) {
                if (!playableActivated.containsKey(ability.toString())) {
                    if (canPlay(ability, available, game)) {
                        playableActivated.put(ability.toString(), ability);
                    }
                }
            }
        }
        playable.addAll(playableActivated.values());
        return playable;
    }

    /**
     * Only used for AIs
     *
     * @param ability
     * @param game
     * @return
     */
    @Override
    public List<Ability> getPlayableOptions(Ability ability, Game game) {
        List<Ability> options = new ArrayList<Ability>();

        if (ability.isModal()) {
            addModeOptions(options, ability, game);
        } else if (ability.getTargets().getUnchosen().size() > 0) {
            // TODO: Handle other variable costs than mana costs
            if (ability.getManaCosts().getVariableCosts().size() > 0) {
                addVariableXOptions(options, ability, 0, game);
            } else {
                addTargetOptions(options, ability, 0, game);
            }
        } else if (ability.getChoices().getUnchosen().size() > 0) {
            addChoiceOptions(options, ability, 0, game);
        } else if (ability.getCosts().getTargets().getUnchosen().size() > 0) {
            addCostTargetOptions(options, ability, 0, game);
        }

        return options;
    }

    private void addModeOptions(List<Ability> options, Ability option, Game game) {
        for (Mode mode: option.getModes().values()) {
            Ability newOption = option.copy();
            newOption.getModes().setMode(mode);
            if (newOption.getTargets().getUnchosen().size() > 0) {
                if (newOption.getManaCosts().getVariableCosts().size() > 0) {
                    addVariableXOptions(options, newOption, 0, game);
                } else {
                    addTargetOptions(options, newOption, 0, game);
                }
            } else if (newOption.getChoices().getUnchosen().size() > 0) {
                addChoiceOptions(options, newOption, 0, game);
            } else if (newOption.getCosts().getTargets().getUnchosen().size() > 0) {
                addCostTargetOptions(options, newOption, 0, game);
            } else {
                options.add(newOption);
            }
        }
    }

    protected void addVariableXOptions(List<Ability> options, Ability option, int targetNum, Game game) {
        addTargetOptions(options, option, targetNum, game);
    }

    protected void addTargetOptions(List<Ability> options, Ability option, int targetNum, Game game) {
        for (Target target: option.getTargets().getUnchosen().get(targetNum).getTargetOptions(option, game)) {
            Ability newOption = option.copy();
            if (target instanceof TargetAmount) {
                for (UUID targetId: target.getTargets()) {
                    int amount = target.getTargetAmount(targetId);
                    newOption.getTargets().get(targetNum).addTarget(targetId, amount, newOption, game, true);
                }
            }
            else {
                for (UUID targetId: target.getTargets()) {
                    newOption.getTargets().get(targetNum).addTarget(targetId, newOption, game, true);
                }
            }
            if (targetNum < option.getTargets().size() - 2) {
                addTargetOptions(options, newOption, targetNum + 1, game);
            }
            else {
                if (option.getChoices().size() > 0) {
                    addChoiceOptions(options, newOption, 0, game);
                } else if (option.getCosts().getTargets().size() > 0) {
                    addCostTargetOptions(options, newOption, 0, game);
                } else {
                    options.add(newOption);
                }
            }
        }
    }

    private void addChoiceOptions(List<Ability> options, Ability option, int choiceNum, Game game) {
        for (String choice: option.getChoices().get(choiceNum).getChoices()) {
            Ability newOption = option.copy();
            newOption.getChoices().get(choiceNum).setChoice(choice);
            if (choiceNum < option.getChoices().size() - 1) {
                addChoiceOptions(options, newOption, choiceNum + 1, game);
            }
            else {
                if (option.getCosts().getTargets().size() > 0) {
                    addCostTargetOptions(options, newOption, 0, game);
                } else {
                    options.add(newOption);
                }
            }
        }
    }

    private void addCostTargetOptions(List<Ability> options, Ability option, int targetNum, Game game) {
        for (UUID targetId: option.getCosts().getTargets().get(targetNum).possibleTargets(option.getSourceId(), playerId, game)) {
            Ability newOption = option.copy();
            newOption.getCosts().getTargets().get(targetNum).addTarget(targetId, option, game, true);
            if (targetNum < option.getCosts().getTargets().size() - 1) {
                addCostTargetOptions(options, newOption, targetNum + 1, game);
            }
            else {
                options.add(newOption);
            }
        }
    }

    @Override
    public boolean isTestMode() {
        return isTestMode;
    }

    @Override
    public void setTestMode(boolean value) {
        this.isTestMode = value;
    }

    @Override
    public boolean isTopCardRevealed() {
        return topCardRevealed;
    }

    @Override
    public void setTopCardRevealed(boolean topCardRevealed) {
        this.topCardRevealed = topCardRevealed;
    }

    @Override
    public UserData getUserData() {
        return this.userData;
    }

    @Override
    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    @Override
    public void addAction(String action) {
        // do nothing
    }

    @Override
    public void setAllowBadMoves(boolean allowBadMoves) {
        // do nothing
    }

    @Override
    public boolean canPayLifeCost() {
        return isLifeTotalCanChange() && canPayLifeCost;
    }

    @Override
    public void setCanPayLifeCost(boolean canPayLifeCost) {
        this.canPayLifeCost = canPayLifeCost;
    }

    @Override
    public boolean canPaySacrificeCost() {
        return canPaySacrificeCost;
    }

    @Override
    public void setCanPaySacrificeCost(boolean canPaySacrificeCost) {
        this.canPaySacrificeCost = canPaySacrificeCost;
    }

    @Override
    public boolean autoLoseGame() {
        return false;
    }

    @Override
    public void becomesActivePlayer() {
        this.passedAllTurns = false;
        this.turns++;
    }

    @Override
    public int getTurns() {
        return turns;
    }

    @Override
    public int getStoredBookmark() {
        return storedBookmark;
    }

    @Override
    public void setStoredBookmark(int storedBookmark) {
        this.storedBookmark = storedBookmark;
    }

    @Override
    public synchronized void resetStoredBookmark(Game game) {
        if (this.storedBookmark != -1) {
            game.removeBookmark(this.storedBookmark);
        }
        setStoredBookmark(-1);
    }


    @Override
    public void revealFaceDownCard(Card card, Game game) {
        if (game.getContinuousEffects().asThough(card.getId(), AsThoughEffectType.REVEAL_FACE_DOWN, game)) {
            Cards cards = new CardsImpl(card);
            this.revealCards(name, cards, game);
        }
    }

    @Override
    public void setPriorityTimeLeft(int timeLeft) {
        priorityTimeLeft = timeLeft;
    }

    @Override
    public int getPriorityTimeLeft() {
        return priorityTimeLeft;
    }

    @Override
    public boolean hasQuit() {
        return quit;
    }

    @Override
    public void setReachedNextTurnAfterLeaving(boolean reachedNextTurnAfterLeaving) {
        this.reachedNextTurnAfterLeaving = reachedNextTurnAfterLeaving;
    }

    @Override
    public boolean hasReachedNextTurnAfterLeaving() {
        return reachedNextTurnAfterLeaving;
    }
    
    @Override
    public boolean canJoinTable(Table table) {
        return true;
    }
}
