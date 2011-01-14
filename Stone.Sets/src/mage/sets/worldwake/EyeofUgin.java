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

package mage.sets.worldwake;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Duration;
import mage.Constants.Layer;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.SubLayer;
import mage.Constants.Zone;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.common.SearchLibraryRevealPutInHandEffect;
import mage.cards.CardImpl;
import mage.filter.common.FilterCreatureCard;
import mage.game.Game;
import mage.game.stack.Spell;
import mage.game.stack.SpellStack;
import mage.game.stack.StackObject;
import mage.target.common.TargetCardInLibrary;

/**
 * TODO: Implement this better.
 *
 * @author maurer.it_at_gmail.com
 */
public class EyeofUgin extends CardImpl<EyeofUgin> {

	private static final FilterCreatureCard filter;

	static {
		filter = new FilterCreatureCard();
		filter.setColorless(true);
		filter.setUseColorless(true);
	}

    public EyeofUgin (UUID ownerId) {
        super(ownerId, 136, "Eye of Ugin", Rarity.MYTHIC, new CardType[]{CardType.LAND}, null);
        this.expansionSetCode = "WWK";
        this.supertype.add("Legendary");
        this.subtype.add("Land");

		this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new EyeofUginCostReductionEffect()));
		Ability searchAbility = new SimpleActivatedAbility(Zone.BATTLEFIELD, new SearchLibraryRevealPutInHandEffect(new TargetCardInLibrary(filter)), new TapSourceCost());
		searchAbility.addCost(new ManaCostsImpl("{7}"));
        this.addAbility(searchAbility);
    }

    public EyeofUgin (final EyeofUgin card) {
        super(card);
    }

    @Override
    public EyeofUgin copy() {
        return new EyeofUgin(this);
    }
}

class EyeofUginCostReductionEffect extends ContinuousEffectImpl<EyeofUginCostReductionEffect> {

	private static final String effectText = "Colorless Eldrazi spells you cast cost {2} less to cast";

	EyeofUginCostReductionEffect ( ) {
		super(Duration.WhileOnBattlefield, Layer.TextChangingEffects_3, SubLayer.NA, Outcome.Benefit);
	}

	EyeofUginCostReductionEffect(EyeofUginCostReductionEffect effect) {
		super(effect);
	}

	@Override
	public boolean apply(Game game, Ability source) {
		SpellStack stack = game.getStack();
		boolean applied = false;

		for ( int idx = 0; idx < stack.size(); idx++ ) {
			StackObject stackObject = stack.get(idx);

			if ( stackObject instanceof Spell &&
				 ((Spell)stackObject).getSubtype().contains("Eldrazi"))
			{
				SpellAbility spell = ((Spell)stackObject).getSpellAbility();
				int previousCost = spell.getManaCosts().convertedManaCost();
				int adjustedCost = 0;
				if ( (previousCost - 2) > 0 ) {
					adjustedCost = previousCost - 2;
				}
				spell.getManaCosts().load("{" + adjustedCost + "}");
				applied = true;
			}
		}

		return applied;
	}

	@Override
	public EyeofUginCostReductionEffect copy() {
		return new EyeofUginCostReductionEffect(this);
	}

	@Override
	public String getText(Ability source) {
		return effectText;
	}
}
