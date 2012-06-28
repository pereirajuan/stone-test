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

package mage.players;

import mage.Constants.Outcome;
import mage.Constants.RangeOfInfluence;
import mage.MageItem;
import mage.MageObject;
import mage.abilities.*;
import mage.abilities.costs.mana.ManaCost;
import mage.abilities.costs.mana.ManaCosts;
import mage.abilities.costs.mana.VariableManaCost;
import mage.abilities.effects.ReplacementEffect;
import mage.cards.Card;
import mage.cards.Cards;
import mage.cards.decks.Deck;
import mage.choices.Choice;
import mage.counters.Counter;
import mage.counters.Counters;
import mage.game.Game;
import mage.game.draft.Draft;
import mage.game.match.Match;
import mage.game.permanent.Permanent;
import mage.game.tournament.Tournament;
import mage.players.net.UserData;
import mage.target.Target;
import mage.target.TargetAmount;
import mage.target.TargetCard;
import mage.target.common.TargetCardInLibrary;
import mage.util.Copyable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public interface Player extends MageItem, Copyable<Player> {

    public boolean isHuman();
    public String getName();
    public RangeOfInfluence getRange();
    public Library getLibrary();
    public Cards getGraveyard();
    public Abilities<Ability> getAbilities();
    public void addAbility(Ability ability);
    public Counters getCounters();
    public int getLife();
    public void setLife(int life, Game game);
    public int loseLife(int amount, Game game);
    public boolean isCanLoseLife();
    public void setCanLoseLife(boolean canLoseLife);
    public int gainLife(int amount, Game game);
    public boolean isCanGainLife();
    public void setCanGainLife(boolean canGainLife);
    public boolean isLifeTotalCanChange();
    public void setCanPayLifeCost(boolean canPayLifeCost);
    public boolean canPayLifeCost();
    public void setCanPaySacrificeCost(boolean canPaySacrificeCost);
    public boolean canPaySacrificeCost();
    public void setLifeTotalCanChange(boolean lifeTotalCanChange);
    public int damage(int damage, UUID sourceId, Game game, boolean combatDamage, boolean preventable);
    public Cards getHand();
    public int getLandsPlayed();
    public int getLandsPerTurn();
    public void setLandsPerTurn(int landsPerTurn);
    public int getMaxHandSize();
    public void setMaxHandSize(int maxHandSize);
    public boolean isPassed();
    public boolean isEmptyDraw();
    public void pass();
    public void resetPassed();
    public boolean hasLost();
    public boolean hasWon();
    public boolean hasLeft();
    public ManaPool getManaPool();
    public Set<UUID> getInRange();
    public boolean isTopCardRevealed();
    public void setTopCardRevealed(boolean topCardRevealed);
    public UserData getUserData();
    public void setUserData(UserData userData);
    public boolean canLose(Game game);
    public boolean autoLoseGame();

    /**
     * Returns a set of players which turns under you control.
     * Doesn't include yourself.
     *
     * @return
     */
    public Set<UUID> getPlayersUnderYourControl();

    /**
     * Defines player whose turn this player controls at the moment.
     * @param playerId
     */
    public void controlPlayersTurn(Game game, UUID playerId);

    /**
     * Sets player {@link UUID} who controls this player's turn.
     *
     * @param playerId
     */
    public void setTurnControlledBy(UUID playerId);

    public UUID getTurnControlledBy();

    /**
     * Resets players whose turns you control at the moment.
     */
    public void resetOtherTurnsControlled();

    /**
     * Returns false in case player don't control the game.
     *
     * Note: For effects like "You control target player during that player's next turn".
     *
     * @return
     */
    public boolean isGameUnderControl();

    /**
     * Returns false in case you don't control the game.
     *
     * Note: For effects like "You control target player during that player's next turn".
     *
     * @param value
     */
    public void setGameUnderYourControl(boolean value);

    public boolean isTestMode();
    public void setTestMode(boolean value);
    public void addAction(String action);
    public void setAllowBadMoves(boolean allowBadMoves);

    public void init(Game game);
    public void init(Game game, boolean testMode);
    public void useDeck(Deck deck, Game game);
    public void reset();
    public void shuffleLibrary(Game game);
    public int drawCards(int num, Game game);
    public boolean cast(SpellAbility ability, Game game, boolean noMana);
    public boolean putInHand(Card card, Game game);
    public boolean removeFromHand(Card card, Game game);
    public boolean removeFromBattlefield(Permanent permanent, Game game);
    public boolean putInGraveyard(Card card, Game game, boolean fromBattlefield);
    public boolean removeFromGraveyard(Card card, Game game);
    public boolean removeFromLibrary(Card card, Game game);
    public boolean searchLibrary(TargetCardInLibrary target, Game game);
    /**
    *
    * @param target
    * @param game
    * @param targetPlayerId player whose library will be searched
    * @return true if search was successful
    */
    public boolean searchLibrary(TargetCardInLibrary target, Game game, UUID targetPlayerId);
    public boolean canPlayLand();
    public boolean playLand(Card card, Game game);
    public boolean activateAbility(ActivatedAbility ability, Game game);
    public boolean triggerAbility(TriggeredAbility ability, Game game);
    public boolean canBeTargetedBy(MageObject source, Game game);
    public boolean hasProtectionFrom(MageObject source, Game game);
    public boolean flipCoin(Game game);
    public void discard(int amount, Ability source, Game game);
    public void discardToMax(Game game);
    public boolean discard(Card card, Ability source, Game game);
    public void lost(Game game);
    public void won(Game game);
    public void leave();
    public void concede(Game game);
    public void abort();

    public void revealCards(String name, Cards cards, Game game);
    public void lookAtCards(String name, Cards cards, Game game);

    @Override
    public Player copy();
    public void restore(Player player);

    public void setResponseString(String responseString);
    public void setResponseUUID(UUID responseUUID);
    public void setResponseBoolean(Boolean responseBoolean);
    public void setResponseInteger(Integer data);

    public abstract boolean priority(Game game);
    public abstract boolean choose(Outcome outcome, Target target, UUID sourceId, Game game);
    public abstract boolean choose(Outcome outcome, Target target, UUID sourceId, Game game, Map<String, Serializable> options);
    public abstract boolean choose(Outcome outcome, Cards cards, TargetCard target, Game game);
    public abstract boolean chooseTarget(Outcome outcome, Target target, Ability source, Game game);
    public abstract boolean chooseTarget(Outcome outcome, Cards cards, TargetCard target, Ability source, Game game);
    public abstract boolean chooseTargetAmount(Outcome outcome, TargetAmount target, Ability source, Game game);
    public abstract boolean chooseMulligan(Game game);
    public abstract boolean chooseUse(Outcome outcome, String message, Game game);
    public abstract boolean choose(Outcome outcome, Choice choice, Game game);
    public abstract boolean choosePile(Outcome outcome, String message, List<? extends Card> pile1, List<? extends Card> pile2, Game game);
    public abstract boolean playMana(ManaCost unpaid, Game game);
    public abstract boolean playXMana(VariableManaCost cost, ManaCosts<ManaCost> costs, Game game);
    public abstract int chooseEffect(List<ReplacementEffect> rEffects, Game game);
    public abstract TriggeredAbility chooseTriggeredAbility(List<TriggeredAbility> abilities, Game game);
    public abstract Mode chooseMode(Modes modes, Ability source, Game game);
    public abstract void selectAttackers(Game game);
    public abstract void selectBlockers(Game game);
    public abstract UUID chooseAttackerOrder(List<Permanent> attacker, Game game);
    public abstract UUID chooseBlockerOrder(List<Permanent> blockers, Game game);
    public abstract void assignDamage(int damage, List<UUID> targets, String singleTargetName, UUID sourceId, Game game);
    public abstract int getAmount(int min, int max, String message, Game game);
    public abstract void sideboard(Match match, Deck deck);
    public abstract void construct(Tournament tournament, Deck deck);
    public abstract void pickCard(List<Card> cards, Deck deck, Draft draft);

    public void declareAttacker(UUID attackerId, UUID defenderId, Game game);
    public void declareBlocker(UUID blockerId, UUID attackerId, Game game);
    public List<Permanent> getAvailableAttackers(Game game);
    public List<Permanent> getAvailableBlockers(Game game);

    public void beginTurn(Game game);
    public void endOfTurn(Game game);
    public void phasing(Game game);
    public void untap(Game game);

    public List<Ability> getPlayable(Game game, boolean hidden);
    public List<Ability> getPlayableOptions(Ability ability, Game game);

    public void addCounters(Counter counter, Game game);
    public List<UUID> getAttachments();
    public boolean addAttachment(UUID permanentId, Game game);
    public boolean removeAttachment(UUID permanentId, Game game);
}
