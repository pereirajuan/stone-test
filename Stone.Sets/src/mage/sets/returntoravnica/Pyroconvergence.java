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

package mage.sets.returntoravnica;

import java.util.UUID;

import mage.constants.CardType;
import mage.constants.Rarity;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.cards.CardImpl;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.stack.Spell;
import mage.target.common.TargetCreatureOrPlayer;

/**
 *
 * @author LevelX2
 */
public class Pyroconvergence extends CardImpl {

    public Pyroconvergence(UUID ownerId) {
        super(ownerId, 103, "Pyroconvergence", Rarity.UNCOMMON, new CardType[]{CardType.ENCHANTMENT}, "{4}{R}");
        this.expansionSetCode = "RTR";


        // Whenever you cast a multicolored spell, Pyroconvergence deals 2 damage to target creature or player.
        this.addAbility(new PyroconvergenceTriggeredAbility());
    }

    public Pyroconvergence(final Pyroconvergence card) {
        super(card);
    }

    @Override
    public Pyroconvergence copy() {
        return new Pyroconvergence(this);
    }
}
class PyroconvergenceTriggeredAbility extends TriggeredAbilityImpl {
    public PyroconvergenceTriggeredAbility() {
        super(Zone.BATTLEFIELD, new DamageTargetEffect(2));
        TargetCreatureOrPlayer target = new TargetCreatureOrPlayer();
        this.addTarget(target);
    }

    public PyroconvergenceTriggeredAbility(final PyroconvergenceTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public PyroconvergenceTriggeredAbility copy() {
        return new PyroconvergenceTriggeredAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
         if (event.getType() == GameEvent.EventType.SPELL_CAST) {
            Spell spell = game.getStack().getSpell(event.getTargetId());
            if (spell != null && spell.getColor(game).isMulticolored() && event.getPlayerId().equals(getControllerId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever you cast a multicolored spell, {this} deals 2 damage to target creature or player.";
    }
}