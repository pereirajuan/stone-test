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

package mage.sets.conflux;

import java.util.UUID;
import mage.MageObject;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.SpecialAction;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.RemoveDelayedTriggeredAbilityEffect;
import mage.abilities.effects.common.RemoveSpecialActionEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;

import mage.target.TargetPlayer;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class QuenchableFire extends CardImpl {

    public QuenchableFire(UUID ownerId) {
        super(ownerId, 70, "Quenchable Fire", Rarity.COMMON, new CardType[]{CardType.SORCERY}, "{3}{R}");
        this.expansionSetCode = "CON";

        // Quenchable Fire deals 3 damage to target player.
        // It deals an additional 3 damage to that player at the beginning of your next upkeep step unless he or she pays {U} before that step.
        this.getSpellAbility().addTarget(new TargetPlayer());
        this.getSpellAbility().addEffect(new DamageTargetEffect(3));
        this.getSpellAbility().addEffect(new QuenchableFireEffect());
    }

    public QuenchableFire(final QuenchableFire card) {
        super(card);
    }

    @Override
    public QuenchableFire copy() {
        return new QuenchableFire(this);
    }
}

class QuenchableFireEffect extends OneShotEffect {

    public QuenchableFireEffect() {
        super(Outcome.Damage);
        staticText = "{this} deals an additional 3 damage to that player at the beginning of your next upkeep step unless he or she pays {U} before that step."
                + "<br><i>Use the Special button to pay the {U} with a special action before the beginning of your next upkeep step.</i>";
    }

    public QuenchableFireEffect(final QuenchableFireEffect effect) {
        super(effect);
    }

    @Override
    public QuenchableFireEffect copy() {
        return new QuenchableFireEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        MageObject sourceObject = source.getSourceObject(game);
        if (sourceObject != null) {
            
            //create special action
            QuenchableFireSpecialAction newAction = new QuenchableFireSpecialAction();

            //create delayed triggered ability
            QuenchableFireDelayedTriggeredAbility delayedAbility = new QuenchableFireDelayedTriggeredAbility();
            delayedAbility.setSourceId(source.getSourceId());
            delayedAbility.setControllerId(source.getControllerId());
            delayedAbility.setSourceObject(sourceObject, game);
            delayedAbility.getTargets().addAll(source.getTargets());
            delayedAbility.setSpecialActionId(newAction.getId());            
            UUID delayedAbilityId = game.addDelayedTriggeredAbility(delayedAbility);

            // update special action
            newAction.addCost(new ManaCostsImpl("{U}"));
            Effect effect = new RemoveDelayedTriggeredAbilityEffect(delayedAbilityId);
            newAction.addEffect(effect);
            effect.setText(sourceObject.getIdName() + " - Pay {U} to remove the triggered ability that deals 3 damage to you at the beginning of your next upkeep step");           
            newAction.addEffect(new RemoveSpecialActionEffect(newAction.getId()));            
            newAction.setSourceId(source.getSourceId());
            newAction.setControllerId(source.getFirstTarget());
            newAction.getTargets().addAll(source.getTargets());
            game.getState().getSpecialActions().add(newAction);
            return true;
        }
        return false;
    }

}

class QuenchableFireDelayedTriggeredAbility extends DelayedTriggeredAbility {

    private UUID specialActionId;

    public QuenchableFireDelayedTriggeredAbility() {
        super(new DamageTargetEffect(3));
    }

    public void setSpecialActionId(UUID specialActionId) {
        this.specialActionId = specialActionId;
    }

    public QuenchableFireDelayedTriggeredAbility(final QuenchableFireDelayedTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public QuenchableFireDelayedTriggeredAbility copy() {
        return new QuenchableFireDelayedTriggeredAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getType() == EventType.UPKEEP_STEP_PRE && event.getPlayerId().equals(this.controllerId)) {
            for (SpecialAction action: game.getState().getSpecialActions()) {
                if (action.getId().equals(specialActionId)) {
                    game.getState().getSpecialActions().remove(action);
                    break;
                }
            }
            return true;
        }
        return false;
    }

}

class QuenchableFireSpecialAction extends SpecialAction {

    public QuenchableFireSpecialAction() {
        super();
    }

    public QuenchableFireSpecialAction(final QuenchableFireSpecialAction ability) {
        super(ability);
    }

    @Override
    public QuenchableFireSpecialAction copy() {
        return new QuenchableFireSpecialAction(this);
    }

}