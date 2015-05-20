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
package mage.sets.planarchaos;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldAbility;
import mage.abilities.common.EntersOrLeavesTheBattlefieldSourceTriggeredAbility;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.keyword.VanishingSacrificeAbility;
import mage.abilities.keyword.VanishingUpkeepAbility;
import mage.cards.CardImpl;
import mage.counters.CounterType;
import mage.target.TargetPlayer;

/**
 *
 * @author jonubuu
 */
public class KeldonMarauders extends CardImpl {

    public KeldonMarauders(UUID ownerId) {
        super(ownerId, 102, "Keldon Marauders", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{1}{R}");
        this.expansionSetCode = "PLC";
        this.subtype.add("Human");
        this.subtype.add("Warrior");

        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Vanishing 2
        this.addAbility(new EntersBattlefieldAbility(new AddCountersSourceEffect(CounterType.TIME.createInstance(2))));
        this.addAbility(new VanishingUpkeepAbility(2));
        this.addAbility(new VanishingSacrificeAbility());
        
        // When Keldon Marauders enters the battlefield or leaves the battlefield, it deals 1 damage to target player.
        Ability ability = new EntersOrLeavesTheBattlefieldSourceTriggeredAbility(new DamageTargetEffect(1), false);
        ability.addTarget(new TargetPlayer());
        this.addAbility(ability);
    }

    public KeldonMarauders(final KeldonMarauders card) {
        super(card);
    }

    @Override
    public KeldonMarauders copy() {
        return new KeldonMarauders(this);
    }
}
