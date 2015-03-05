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
package mage.sets.magic2012;


import mage.constants.*;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.common.EntersBattlefieldAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.condition.common.SourceHasCounterCondition;
import mage.abilities.decorator.ConditionalContinuousEffect;
import mage.abilities.effects.EntersBattlefieldEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.GainAbilitySourceEffect;
import mage.abilities.keyword.TrampleAbility;
import mage.cards.CardImpl;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.UUID;

/**
 *
 * @author Loki
 */
public class PrimordialHydra extends CardImpl {

    private static final String staticText = "{this} has trample as long as it has ten or more +1/+1 counters on it";

    public PrimordialHydra(UUID ownerId) {
        super(ownerId, 189, "Primordial Hydra", Rarity.MYTHIC, new CardType[]{CardType.CREATURE}, "{X}{G}{G}");
        this.expansionSetCode = "M12";
        this.subtype.add("Hydra");

        this.color.setGreen(true);
        this.power = new MageInt(0);
        this.toughness = new MageInt(0);

        this.addAbility(new EntersBattlefieldAbility(new PrimordialHydraEntersEffect(), "{this} enters the battlefield with X +1/+1 counters on it"));
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(new PrimordialHydraDoubleEffect(), TargetController.YOU, false));
        ConditionalContinuousEffect effect = new ConditionalContinuousEffect(new GainAbilitySourceEffect(TrampleAbility.getInstance()), new SourceHasCounterCondition(CounterType.P1P1, 10), staticText);
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, effect));

    }

    public PrimordialHydra(final PrimordialHydra card) {
        super(card);
    }

    @Override
    public PrimordialHydra copy() {
        return new PrimordialHydra(this);
    }
}

class PrimordialHydraEntersEffect extends OneShotEffect {
    public PrimordialHydraEntersEffect() {
        super(Outcome.BoostCreature);
    }

    public PrimordialHydraEntersEffect(final PrimordialHydraEntersEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(source.getSourceId());
        if (permanent != null) {
            Object obj = getValue(EntersBattlefieldEffect.SOURCE_CAST_SPELL_ABILITY);
            if (obj != null && obj instanceof SpellAbility) {
                int amount = ((SpellAbility) obj).getManaCostsToPay().getX();
                if (amount > 0) {
                    permanent.addCounters(CounterType.P1P1.createInstance(amount), game);
                }
            }
        }
        return true;
    }

    @Override
    public PrimordialHydraEntersEffect copy() {
        return new PrimordialHydraEntersEffect(this);
    }
}

class PrimordialHydraDoubleEffect extends OneShotEffect {
    PrimordialHydraDoubleEffect() {
        super(Outcome.BoostCreature);
        staticText = "double the number of +1/+1 counters on {this}";
    }

    PrimordialHydraDoubleEffect(final PrimordialHydraDoubleEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent sourcePermanent = game.getPermanent(source.getSourceId());
        if (sourcePermanent != null) {
            int amount = sourcePermanent.getCounters().getCount(CounterType.P1P1);
            if (amount > 0) {
                sourcePermanent.addCounters(CounterType.P1P1.createInstance(amount), game);
            }
            return true;
        }
        return false;
    }

    @Override
    public PrimordialHydraDoubleEffect copy() {
        return new PrimordialHydraDoubleEffect(this);
    }
}