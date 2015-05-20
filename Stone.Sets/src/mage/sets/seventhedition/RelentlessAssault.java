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
package mage.sets.seventhedition;

import java.util.Set;
import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.TurnPhase;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.game.turn.TurnMod;
import mage.watchers.Watcher;
import mage.watchers.common.AttackedThisTurnWatcher;

/**
 *
 * @author Quercitron
 */
public class RelentlessAssault extends CardImpl {

    public RelentlessAssault(UUID ownerId) {
        super(ownerId, 214, "Relentless Assault", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{2}{R}{R}");
        this.expansionSetCode = "7ED";


        // Untap all creatures that attacked this turn. After this main phase, there is an additional combat phase followed by an additional main phase.
        this.getSpellAbility().addWatcher(new AttackedThisTurnWatcher());
        this.getSpellAbility().addEffect(new RelentlessAssaultUntapEffect());
        this.getSpellAbility().addEffect(new RelentlessAssaultAddPhasesEffect());
    }

    public RelentlessAssault(final RelentlessAssault card) {
        super(card);
    }

    @Override
    public RelentlessAssault copy() {
        return new RelentlessAssault(this);
    }
}

class RelentlessAssaultUntapEffect extends OneShotEffect {

    public RelentlessAssaultUntapEffect() {
        super(Outcome.Benefit);
        staticText = "Untap all creatures that attacked this turn";
    }

    public RelentlessAssaultUntapEffect(final RelentlessAssaultUntapEffect effect) {
        super(effect);
    }

    @Override
    public RelentlessAssaultUntapEffect copy() {
        return new RelentlessAssaultUntapEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Watcher watcher = game.getState().getWatchers().get("AttackedThisTurn");
        if (watcher != null && watcher instanceof AttackedThisTurnWatcher) {
            Set<UUID> attackedThisTurn = ((AttackedThisTurnWatcher) watcher).getAttackedThisTurnCreatures();
            for (UUID uuid : attackedThisTurn) {
                Permanent permanent = game.getPermanent(uuid);
                if (permanent != null && permanent.getCardType().contains(CardType.CREATURE)) {
                    permanent.untap(game);
                }
            }
            return true;
        }
        return false;
    }
}

class RelentlessAssaultAddPhasesEffect extends OneShotEffect {

    public RelentlessAssaultAddPhasesEffect() {
        super(Outcome.Benefit);
        staticText = "After this main phase, there is an additional combat phase followed by an additional main phase";
    }

    public RelentlessAssaultAddPhasesEffect(final RelentlessAssaultAddPhasesEffect effect) {
        super(effect);
    }

    @Override
    public RelentlessAssaultAddPhasesEffect copy() {
        return new RelentlessAssaultAddPhasesEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        // 15.07.2006 If it's somehow not a main phase when Fury of the Horde resolves, all it does is untap all creatures that attacked that turn. No new phases are created.
        if (TurnPhase.PRECOMBAT_MAIN.equals(game.getTurn().getPhaseType()) || TurnPhase.POSTCOMBAT_MAIN.equals(game.getTurn().getPhaseType())) {
            // we can't add two turn modes at once, will add additional post combat on delayed trigger resolution
            TurnMod combat = new TurnMod(source.getControllerId(), TurnPhase.COMBAT, TurnPhase.POSTCOMBAT_MAIN, false);
            game.getState().getTurnMods().add(combat);
            RelentlessAssaultDelayedAddMainPhaseAbility delayedTriggeredAbility = new RelentlessAssaultDelayedAddMainPhaseAbility();
            delayedTriggeredAbility.setSourceId(source.getSourceId());
            delayedTriggeredAbility.setControllerId(source.getControllerId());
            delayedTriggeredAbility.setConnectedTurnMod(combat.getId());
            game.addDelayedTriggeredAbility(delayedTriggeredAbility);
            return true;
        }
        return false;
    }
}

class RelentlessAssaultDelayedAddMainPhaseAbility extends DelayedTriggeredAbility {

    private UUID connectedTurnMod;
    private boolean enabled;

    public RelentlessAssaultDelayedAddMainPhaseAbility() {
        super(null, Duration.EndOfTurn);
        this.usesStack = false; // don't show this to the user
    }

    public RelentlessAssaultDelayedAddMainPhaseAbility(RelentlessAssaultDelayedAddMainPhaseAbility ability) {
        super(ability);
        this.connectedTurnMod = ability.connectedTurnMod;
        this.enabled = ability.enabled;
    }

    @Override
    public RelentlessAssaultDelayedAddMainPhaseAbility copy() {
        return new RelentlessAssaultDelayedAddMainPhaseAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.PHASE_CHANGED && this.connectedTurnMod.equals(event.getSourceId())) {
            enabled = true;
        }
        if (event.getType() == GameEvent.EventType.COMBAT_PHASE_PRE && enabled) {
            // add additional post combat main phase after that - after phase == null because add it after this combat
            game.getState().getTurnMods().add(new TurnMod(getControllerId(), TurnPhase.POSTCOMBAT_MAIN, null, false));
            enabled = false;
        }
        return false;
    }

    public void setConnectedTurnMod(UUID connectedTurnMod) {
        this.connectedTurnMod = connectedTurnMod;
    }

    @Override
    public String getRule() {
        return "add additional post combat main phase";
    }
}
