/*
* Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are
* permitted provided that the following conditions are met:
*
*    1. Redistributions of source code must retain the above copyright notice, this list of
*       conditions and the following disclaimer.
*
*    2. Redistributions in binary form must reproduce the above copyright notice, this list
*       of conditions and the following disclaimer in the documentation and/or other materials
*       provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
* FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation are those of the
* authors and should not be interpreted as representing official policies, either expressed
* or implied, of BetaSteward_at_googlemail.com.
*/
package mage.abilities.mana;

import mage.Mana;
import mage.abilities.costs.Cost;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.common.AddConditionalManaOfAnyColorEffect;
import mage.abilities.mana.builder.ConditionalManaBuilder;
import mage.choices.ChoiceColor;
import mage.constants.Zone;

/**
 * For cards like:
 *   {tap}: Add three mana of any one color to your mana pool. Spend this mana only to cast creature spells.
 *
 * @author noxx
 */
public class ConditionalAnyColorManaAbility extends ManaAbility {

    public ConditionalAnyColorManaAbility(int amount, ConditionalManaBuilder manaBuilder) {
        this(new TapSourceCost(), amount, manaBuilder);
    }

    public ConditionalAnyColorManaAbility(Cost cost, int amount, ConditionalManaBuilder manaBuilder) {
        this(cost, amount, manaBuilder, false);
    }

    public ConditionalAnyColorManaAbility(Cost cost, int amount, ConditionalManaBuilder manaBuilder, boolean oneChoice) {
        super(Zone.BATTLEFIELD, new AddConditionalManaOfAnyColorEffect(oneChoice ? 1 :amount, manaBuilder), cost);
        int choices = amount;
        if (oneChoice) {
            for (int i = 1; i < amount; i++) {
                this.addEffect(new AddConditionalManaOfAnyColorEffect(1 , manaBuilder));
            }
            choices = 1;
        }
        for (int i = 0; i < choices; i++) {
            this.addChoice(new ChoiceColor());
        }
        this.netMana.add(new Mana(0,0,0,0,0,0,amount));
    }

    public ConditionalAnyColorManaAbility(final ConditionalAnyColorManaAbility ability) {
        super(ability);
    }

    @Override
    public ConditionalAnyColorManaAbility copy() {
        return new ConditionalAnyColorManaAbility(this);
    }
}
