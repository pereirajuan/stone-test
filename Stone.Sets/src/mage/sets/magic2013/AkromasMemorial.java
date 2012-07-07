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
package mage.sets.magic2013;

import java.util.UUID;

import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.continious.GainAbilityControlledEffect;
import mage.abilities.keyword.*;
import mage.cards.CardImpl;
import mage.filter.Filter;
import mage.filter.FilterCard;
import mage.filter.common.FilterControlledCreaturePermanent;

/**
 *
 * @author Loki
 */
public class AkromasMemorial extends CardImpl<AkromasMemorial> {
    private static final FilterCard filterBlack = new FilterCard("Black");
    private static final FilterCard filterRed = new FilterCard("Red");

    static {
        filterBlack.setUseColor(true);
        filterBlack.getColor().setBlack(true);
        filterBlack.setScopeColor(Filter.ComparisonScope.Any);
        filterRed.setUseColor(true);
        filterRed.getColor().setRed(true);
        filterRed.setScopeColor(Filter.ComparisonScope.Any);
    }

    public AkromasMemorial(UUID ownerId) {
        super(ownerId, 200, "Akroma's Memorial", Rarity.MYTHIC, new CardType[]{CardType.ARTIFACT}, "{7}");
        this.expansionSetCode = "M13";
        this.supertype.add("Legendary");
    }

    @Override
    public void build() {
        // Creatures you control have flying, first strike, vigilance, trample, haste, and protection from black and from red.
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, new GainAbilityControlledEffect(FlyingAbility.getInstance(), Constants.Duration.WhileOnBattlefield, new FilterControlledCreaturePermanent("Creatures you control"))));
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, new GainAbilityControlledEffect(FirstStrikeAbility.getInstance(), Constants.Duration.WhileOnBattlefield, new FilterControlledCreaturePermanent("Creatures you control"))));
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, new GainAbilityControlledEffect(VigilanceAbility.getInstance(), Constants.Duration.WhileOnBattlefield, new FilterControlledCreaturePermanent("Creatures you control"))));
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, new GainAbilityControlledEffect(TrampleAbility.getInstance(), Constants.Duration.WhileOnBattlefield, new FilterControlledCreaturePermanent("Creatures you control"))));
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, new GainAbilityControlledEffect(HasteAbility.getInstance(), Constants.Duration.WhileOnBattlefield, new FilterControlledCreaturePermanent("Creatures you control"))));
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, new GainAbilityControlledEffect(new ProtectionAbility(filterBlack), Constants.Duration.WhileOnBattlefield, new FilterControlledCreaturePermanent("Creatures you control"))));
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, new GainAbilityControlledEffect(new ProtectionAbility(filterRed), Constants.Duration.WhileOnBattlefield, new FilterControlledCreaturePermanent("Creatures you control"))));
    }

    public AkromasMemorial(final AkromasMemorial card) {
        super(card);
    }

    @Override
    public AkromasMemorial copy() {
        return new AkromasMemorial(this);
    }
}
