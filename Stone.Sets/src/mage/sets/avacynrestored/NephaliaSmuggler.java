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
package mage.sets.avacynrestored;

import mage.constants.CardType;
import mage.constants.Rarity;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.ExileTargetForSourceEffect;
import mage.abilities.effects.common.ReturnToBattlefieldUnderYourControlTargetEffect;
import mage.cards.CardImpl;
import mage.constants.Zone;
import mage.target.common.TargetControlledCreaturePermanent;

import java.util.UUID;

/**
 * @author noxx
 */
public class NephaliaSmuggler extends CardImpl {

    public NephaliaSmuggler(UUID ownerId) {
        super(ownerId, 69, "Nephalia Smuggler", Rarity.UNCOMMON, new CardType[]{CardType.CREATURE}, "{U}");
        this.expansionSetCode = "AVR";
        this.subtype.add("Human");
        this.subtype.add("Rogue");

        this.power = new MageInt(1);
        this.toughness = new MageInt(1);

        // {3}{U}, {tap}: Exile another target creature you control, then return that card to the battlefield under your control.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new ExileTargetForSourceEffect(), new ManaCostsImpl("{3}{U}"));
        ability.addCost(new TapSourceCost());
        ability.addEffect(new ReturnToBattlefieldUnderYourControlTargetEffect(true));
        ability.addTarget(new TargetControlledCreaturePermanent());
        this.addAbility(ability);
    }

    public NephaliaSmuggler(final NephaliaSmuggler card) {
        super(card);
    }

    @Override
    public NephaliaSmuggler copy() {
        return new NephaliaSmuggler(this);
    }
}
