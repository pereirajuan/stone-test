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
package mage.sets.championsofkamigawa;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.PreventionEffectData;
import mage.abilities.effects.PreventionEffectImpl;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.predicate.mageobject.SupertypePredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetControlledCreaturePermanent;

/**
 *
 * @author LevelX2
 */
public class VassalsDuty extends CardImpl {

    private static final FilterControlledCreaturePermanent filter = new FilterControlledCreaturePermanent("legendary creature you control");
    static {
        filter.add(new SupertypePredicate("Legendary"));
    }

    public VassalsDuty(UUID ownerId) {
        super(ownerId, 48, "Vassal's Duty", Rarity.RARE, new CardType[]{CardType.ENCHANTMENT}, "{3}{W}");
        this.expansionSetCode = "CHK";


        // {1}: The next 1 damage that would be dealt to target legendary creature you control this turn is dealt to you instead.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new VassalsDutyPreventDamageTargetEffect(Duration.EndOfTurn, 1), new GenericManaCost(1));
        ability.addTarget(new TargetControlledCreaturePermanent(1,1,filter, false));
        this.addAbility(ability);
    }

    public VassalsDuty(final VassalsDuty card) {
        super(card);
    }

    @Override
    public VassalsDuty copy() {
        return new VassalsDuty(this);
    }
}

class VassalsDutyPreventDamageTargetEffect extends PreventionEffectImpl {

    public VassalsDutyPreventDamageTargetEffect(Duration duration, int amount) {
        super(duration, amount, false);
        staticText = "The next " + amount + " damage that would be dealt to target legendary creature you control this turn is dealt to you instead";
    }

    public VassalsDutyPreventDamageTargetEffect(final VassalsDutyPreventDamageTargetEffect effect) {
        super(effect);
    }

    @Override
    public VassalsDutyPreventDamageTargetEffect copy() {
        return new VassalsDutyPreventDamageTargetEffect(this);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        PreventionEffectData preventionResult = preventDamageAction(event, source, game);
        // deal damage now
        if (preventionResult.getPreventedDamage() > 0) {
            UUID redirectTo = source.getControllerId();
            Player player = game.getPlayer(redirectTo);
            if (player != null) {
                game.informPlayers("Dealing " + preventionResult.getPreventedDamage() + " to " + player.getLogName() + " instead");
                // keep the original source id as it is redirecting
                player.damage(preventionResult.getPreventedDamage(), event.getSourceId(), game, false, true);
            }
        }
        // damage amount is reduced or set to 0 so complete replacement of damage event is never neccessary
        return false;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (!this.used && super.applies(event, source, game)) {
            if (event.getTargetId().equals(getTargetPointer().getFirst(game, source))) {
                return game.getPermanent(event.getTargetId()) != null;
            }
        }
        return false;
    }

}
