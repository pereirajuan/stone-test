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
package mage.sets.shardsofalara;

import java.util.UUID;

import mage.constants.CardType;
import mage.constants.Rarity;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.common.DiesAndDealtDamageThisTurnTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author Plopman
 */
public class VeinDrinker extends CardImpl {

    public VeinDrinker(UUID ownerId) {
        super(ownerId, 91, "Vein Drinker", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{4}{B}{B}");
        this.expansionSetCode = "ALA";
        this.subtype.add("Vampire");

        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // Flying
        this.addAbility(FlyingAbility.getInstance());
        // {R}, {tap}: Vein Drinker deals damage equal to its power to target creature. That creature deals damage equal to its power to Vein Drinker.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new VeinDrinkerEffect(), new ManaCostsImpl("{R}"));
        ability.addCost(new TapSourceCost());
        ability.addTarget(new TargetCreaturePermanent());
        this.addAbility(ability);
        // Whenever a creature dealt damage by Vein Drinker this turn dies, put a +1/+1 counter on Vein Drinker.
        this.addAbility(new DiesAndDealtDamageThisTurnTriggeredAbility(new AddCountersSourceEffect(CounterType.P1P1.createInstance())));
    }

    public VeinDrinker(final VeinDrinker card) {
        super(card);
    }

    @Override
    public VeinDrinker copy() {
        return new VeinDrinker(this);
    }
}

class VeinDrinkerEffect extends OneShotEffect {

    public VeinDrinkerEffect() {
        super(Outcome.Damage);
    }

    public VeinDrinkerEffect(final VeinDrinkerEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent sourceCreature = game.getPermanent(source.getSourceId());
        Permanent targetCreature = game.getPermanent(source.getTargets().get(0).getFirstTarget());
        if (sourceCreature != null && targetCreature != null) {
            if (sourceCreature.getCardType().contains(CardType.CREATURE) && targetCreature.getCardType().contains(CardType.CREATURE)) {
                sourceCreature.damage(targetCreature.getPower().getValue(), targetCreature.getId(), game, false, true);
                targetCreature.damage(sourceCreature.getPower().getValue(), sourceCreature.getId(), game, false, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public VeinDrinkerEffect copy() {
        return new VeinDrinkerEffect(this);
    }

    @Override
    public String getText(Mode mode) {
        return "{this} deals damage equal to its power to target creature. That creature deals damage equal to its power to {this}";
    }

}
