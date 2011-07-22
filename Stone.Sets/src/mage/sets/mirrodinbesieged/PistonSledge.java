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

package mage.sets.mirrodinbesieged;

import java.util.UUID;

import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.AlternativeCost;
import mage.abilities.costs.common.SacrificeTargetCost;
import mage.abilities.effects.common.AttachEffect;
import mage.abilities.effects.common.continious.BoostEquippedEffect;
import mage.abilities.keyword.EquipAbility;
import mage.cards.CardImpl;
import mage.filter.common.FilterControlledPermanent;
import mage.target.common.TargetControlledCreaturePermanent;
import mage.target.common.TargetControlledPermanent;

/**
 *
 * @author Viserion
 */
public class PistonSledge extends CardImpl<PistonSledge> {


    public PistonSledge (UUID ownerId) {
        super(ownerId, 124, "Piston Sledge", Rarity.UNCOMMON, new CardType[]{CardType.ARTIFACT}, "{3}");
        this.expansionSetCode = "MBS";
        this.subtype.add("Equipment");
        this.addAbility(new EquipAbility(Constants.Outcome.AddAbility, new PistonSledgeEquipCost()));
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new BoostEquippedEffect(3, 1)));

		Ability ability = new EntersBattlefieldTriggeredAbility(new PistonSledgeEffect(Constants.Outcome.AddAbility), false);
		ability.addTarget(new TargetControlledCreaturePermanent());
		this.addAbility(ability);
    }

    public PistonSledge (final PistonSledge card) {
        super(card);
    }

    @Override
    public PistonSledge copy() {
        return new PistonSledge(this);
    }
}

class PistonSledgeEquipCost extends AlternativeCost<PistonSledgeEquipCost> {
	private static FilterControlledPermanent filter = new FilterControlledPermanent("artifact");

	static {
		filter.getCardType().add(CardType.ARTIFACT);
	}

	public PistonSledgeEquipCost() {
		super("sacrifice an artifact");
		this.add(new SacrificeTargetCost(new TargetControlledPermanent(1, 1, filter, false)));
	}

	public PistonSledgeEquipCost(final PistonSledgeEquipCost cost) {
		super(cost);
	}

	@Override
	public PistonSledgeEquipCost copy() {
		return new PistonSledgeEquipCost(this);
	}

	@Override
	public String getText() {
		return " sacrifice an artifact";
	}

}

class PistonSledgeEffect extends AttachEffect{
	public PistonSledgeEffect(Outcome outcome) {
		super(outcome);
		staticText = "attach it to target creature you control";
	}

	public PistonSledgeEffect(final AttachEffect effect) {
		super(effect);
	}

}