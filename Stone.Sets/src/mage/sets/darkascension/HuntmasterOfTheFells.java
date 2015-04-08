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
package mage.sets.darkascension;

import mage.constants.*;
import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbility;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.condition.common.NoSpellsWereCastLastTurnCondition;
import mage.abilities.decorator.ConditionalTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.GainLifeEffect;
import mage.abilities.effects.common.TransformSourceEffect;
import mage.abilities.keyword.TransformAbility;
import mage.cards.CardImpl;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.Token;
import mage.game.permanent.token.WolfToken;
import mage.game.stack.StackObject;
import mage.players.Player;
import mage.target.Target;
import mage.target.TargetPermanent;
import mage.target.common.TargetOpponent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author BetaSteward
 */
public class HuntmasterOfTheFells extends CardImpl {

    public HuntmasterOfTheFells(UUID ownerId) {
        super(ownerId, 140, "Huntmaster of the Fells", Rarity.MYTHIC, new CardType[]{CardType.CREATURE}, "{2}{R}{G}");
        this.expansionSetCode = "DKA";
        this.subtype.add("Human");
        this.subtype.add("Werewolf");

        this.canTransform = true;
        this.secondSideCard = new RavagerOfTheFells(ownerId);

        this.color.setRed(true);
        this.color.setGreen(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Whenever this creature enters the battlefield or transforms into Huntmaster of the Fells, put a 2/2 green Wolf creature token onto the battlefield and you gain 2 life.
        this.addAbility(new HuntmasterOfTheFellsAbility());

        // Whenever this creature transforms into Ravager of the Fells, it deals 2 damage to target opponent and 2 damage to up to one target creature that player controls.
        this.addAbility(new RavagerOfTheFellsAbility());

        // At the beginning of each upkeep, if no spells were cast last turn, transform Huntmaster of the Fells.
        this.addAbility(new TransformAbility());
        TriggeredAbility ability = new BeginningOfUpkeepTriggeredAbility(new TransformSourceEffect(true), TargetController.ANY, false);
        this.addAbility(new ConditionalTriggeredAbility(ability, NoSpellsWereCastLastTurnCondition.getInstance(), TransformAbility.NO_SPELLS_TRANSFORM_RULE));
    }

    public HuntmasterOfTheFells(final HuntmasterOfTheFells card) {
        super(card);
    }

    @Override
    public HuntmasterOfTheFells copy() {
        return new HuntmasterOfTheFells(this);
    }
}

class HuntmasterOfTheFellsAbility extends TriggeredAbilityImpl {

    public HuntmasterOfTheFellsAbility() {
        super(Zone.BATTLEFIELD, new CreateTokenEffect(new WolfToken(Token.Type.SECOND)), false);
        this.addEffect(new GainLifeEffect(2));
    }

    public HuntmasterOfTheFellsAbility(final HuntmasterOfTheFellsAbility ability) {
        super(ability);
    }

    @Override
    public HuntmasterOfTheFellsAbility copy() {
        return new HuntmasterOfTheFellsAbility(this);
    }
    


    @Override
    public boolean isInUseableZone(Game game, MageObject source, GameEvent event) {
        if (event.getType() == GameEvent.EventType.TRANSFORMED) {
            Permanent currentSourceObject = (Permanent) getSourceObjectIfItStillExists(game);
            if (currentSourceObject != null && !currentSourceObject.isNightCard()) {
                return true;
            }
        }
        return super.isInUseableZone(game, source, event);
    }
    
    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.TRANSFORMED || event.getType() == GameEvent.EventType.ENTERS_THE_BATTLEFIELD;
    }
    
    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.TRANSFORMED && event.getTargetId().equals(this.getSourceId())) {
            Permanent permanent = game.getPermanent(sourceId);
            if (permanent != null && !permanent.isTransformed()) {
                return true;
            }
        }
        if (event.getType() == GameEvent.EventType.ENTERS_THE_BATTLEFIELD && event.getTargetId().equals(this.getSourceId())) {
            return true;
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever this creature enters the battlefield or transforms into {this}, put a 2/2 green Wolf creature token onto the battlefield and you gain 2 life.";
    }

}

class RavagerOfTheFellsAbility extends TriggeredAbilityImpl {

    public RavagerOfTheFellsAbility() {
        super(Zone.BATTLEFIELD, new RavagerOfTheFellsEffect(), false);
        Target target1 = new TargetOpponent();
        this.addTarget(target1);
        this.addTarget(new RavagerOfTheFellsTarget());
        // Rule only shown on the night side
        this.setRuleVisible(false);
    }

    public RavagerOfTheFellsAbility(final RavagerOfTheFellsAbility ability) {
        super(ability);
    }

    @Override
    public RavagerOfTheFellsAbility copy() {
        return new RavagerOfTheFellsAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.TRANSFORMED;
    }

    @Override
    public boolean isInUseableZone(Game game, MageObject source, GameEvent event) {
        Permanent currentSourceObject = (Permanent) getSourceObjectIfItStillExists(game);
        if (currentSourceObject != null && currentSourceObject.isNightCard()) {
            return true;
        }
        return super.isInUseableZone(game, source, event);
    }

    
    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getTargetId().equals(sourceId)) {
            Permanent permanent = game.getPermanent(sourceId);
            if (permanent != null && permanent.isTransformed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever this creature transforms into Ravager of the Fells, it deals 2 damage to target opponent and 2 damage to up to one target creature that player controls.";
    }

}

class RavagerOfTheFellsEffect extends OneShotEffect {

    public RavagerOfTheFellsEffect() {
        super(Outcome.Damage);
    }

    public RavagerOfTheFellsEffect(final RavagerOfTheFellsEffect effect) {
        super(effect);
    }

    @Override
    public RavagerOfTheFellsEffect copy() {
        return new RavagerOfTheFellsEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getTargets().get(0).getFirstTarget());
        if (player != null) {
            player.damage(2, source.getSourceId(), game, false, true);
        }
        Permanent creature = game.getPermanent(source.getTargets().get(1).getFirstTarget());
        if (creature != null) {
            creature.damage(2, source.getSourceId(), game, false, true);
        }
        return true;
    }

}

class RavagerOfTheFellsTarget extends TargetPermanent {

    public RavagerOfTheFellsTarget() {
        super(0, 1, new FilterCreaturePermanent(), false);
    }

    public RavagerOfTheFellsTarget(final RavagerOfTheFellsTarget target) {
        super(target);
    }

    @Override
    public boolean canTarget(UUID id, Ability source, Game game) {
        UUID firstTarget = source.getFirstTarget();
        Permanent permanent = game.getPermanent(id);
        if (firstTarget != null && permanent != null && permanent.getControllerId().equals(firstTarget)) {
            return super.canTarget(id, source, game);
        }
        return false;
    }

    @Override
    public Set<UUID> possibleTargets(UUID sourceId, UUID sourceControllerId, Game game) {
        Set<UUID> availablePossibleTargets = super.possibleTargets(sourceId, sourceControllerId, game);
        Set<UUID> possibleTargets = new HashSet<>();
        MageObject object = game.getObject(sourceId);

        for (StackObject item: game.getState().getStack()) {
            if (item.getId().equals(sourceId)) {
                object = item;
            }
            if (item.getSourceId().equals(sourceId)) {
                object = item;
            }
        }

        if (object instanceof StackObject) {
            UUID playerId = ((StackObject)object).getStackAbility().getFirstTarget();
            for (UUID targetId : availablePossibleTargets) {
                Permanent permanent = game.getPermanent(targetId);
                if(permanent != null && permanent.getControllerId().equals(playerId)){
                    possibleTargets.add(targetId);
                }
            }
        }
        return possibleTargets;
    }

    @Override
    public RavagerOfTheFellsTarget copy() {
        return new RavagerOfTheFellsTarget(this);
    }
}
