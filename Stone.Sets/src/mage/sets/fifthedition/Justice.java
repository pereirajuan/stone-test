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
package mage.sets.fifthedition;

import java.util.UUID;
import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.MageObject;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.SacrificeSourceUnlessPaysEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.game.stack.Spell;
import mage.players.Player;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author jeffwadsworth
 */
public class Justice extends CardImpl<Justice> {

    public Justice(UUID ownerId) {
        super(ownerId, 6, "Justice", Rarity.UNCOMMON, new CardType[]{CardType.ENCHANTMENT}, "{2}{W}{W}");
        this.expansionSetCode = "5ED";

        this.color.setWhite(true);

        // At the beginning of your upkeep, sacrifice Justice unless you pay {W}{W}.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(Constants.Zone.BATTLEFIELD, new SacrificeSourceUnlessPaysEffect(new ManaCostsImpl("{W}{W}")), Constants.TargetController.YOU, false));
        
        // Whenever a red creature or spell deals damage, Justice deals that much damage to that creature's or spell's controller.
        this.addAbility(new JusticeTriggeredAbility(new JusticeEffect()));
    }

    public Justice(final Justice card) {
        super(card);
    }

    @Override
    public Justice copy() {
        return new Justice(this);
    }
}

class JusticeTriggeredAbility extends TriggeredAbilityImpl<JusticeTriggeredAbility> {

    public JusticeTriggeredAbility(Effect effect) {
        super(Constants.Zone.BATTLEFIELD, effect);
    }

    public JusticeTriggeredAbility(final JusticeTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public JusticeTriggeredAbility copy() {
        return new JusticeTriggeredAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        //if anyone can perform these checks more efficiently, have at it.
        if (event.getType() == GameEvent.EventType.DAMAGED_CREATURE || 
                event.getType() == GameEvent.EventType.DAMAGED_PLAYER  || 
                event.getType() == GameEvent.EventType.DAMAGED_PLANESWALKER) {
            if (game.getObject(event.getSourceId()) instanceof Spell || 
                    game.getObject(event.getSourceId()) instanceof Permanent) {
                MageObject damageObject = game.getObject(event.getSourceId());
                if (damageObject.getColor().contains(ObjectColor.RED)) {
                    if (damageObject.getCardType().contains(CardType.CREATURE) ||
                            damageObject.getCardType().contains(CardType.SORCERY) ||
                            damageObject.getCardType().contains(CardType.INSTANT)) {
                                this.getEffects().get(0).setValue("damageAmount", event.getAmount());
                                this.getEffects().get(0).setTargetPointer(new FixedTarget(game.getControllerId(damageObject.getId())));
                                return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever a red creature or spell deals damage, {this} deals that much damage to that creature's or spell's controller.";
    }
}

class JusticeEffect extends OneShotEffect<JusticeEffect> {

    public JusticeEffect() {
        super(Constants.Outcome.Damage);
        //this.staticText = "it deals that much damage to its controller";
    }

    public JusticeEffect(final JusticeEffect effect) {
        super(effect);
    }

    @Override
    public JusticeEffect copy() {
        return new JusticeEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Integer damageAmount = (Integer) this.getValue("damageAmount");
        UUID targetId = this.targetPointer.getFirst(game, source);
        if (damageAmount != null && targetId != null) {
            Player player = game.getPlayer(targetId);
            if (player != null) {
                    player.damage(damageAmount, targetId, game, false, true);
                    return true;
            }
        }
        return false;
    }
}