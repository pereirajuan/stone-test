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
package mage.sets.onslaught;

import java.util.UUID;
import mage.abilities.common.CycleTriggeredAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.DamageAllEffect;
import mage.abilities.keyword.CyclingAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.filter.common.FilterCreaturePermanent;

/**
 *
 * @author LevelX2
 */
public class SliceAndDice extends CardImpl {

    public SliceAndDice(UUID ownerId) {
        super(ownerId, 232, "Slice and Dice", Rarity.UNCOMMON, new CardType[]{CardType.SORCERY}, "{4}{R}{R}");
        this.expansionSetCode = "ONS";


        // Slice and Dice deals 4 damage to each creature.
        this.getSpellAbility().addEffect(new DamageAllEffect(4, new FilterCreaturePermanent()));
        // Cycling {2}{R}
        this.addAbility(new CyclingAbility(new ManaCostsImpl("{2}{R}")));
        // When you cycle Slice and Dice, you may have it deal 1 damage to each creature.
        this.addAbility(new CycleTriggeredAbility(new DamageAllEffect(1, new FilterCreaturePermanent()), true));

    }

    public SliceAndDice(final SliceAndDice card) {
        super(card);
    }

    @Override
    public SliceAndDice copy() {
        return new SliceAndDice(this);
    }
}
