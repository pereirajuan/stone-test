/*
 *  Copyright 2011 BetaSteward_at_googlemail.com. All rights reserved.
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

package mage.sets.scarsofmirrodin;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Duration;
import mage.Constants.Layer;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.SubLayer;
import mage.Constants.Zone;
import mage.MageInt;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.common.TapTargetCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.common.BoostControlledEffect;
import mage.abilities.effects.common.ManaEffect;
import mage.abilities.mana.SimpleManaAbility;
import mage.cards.CardImpl;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.common.TargetControlledCreaturePermanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class GrandArchitect extends CardImpl<GrandArchitect> {

	private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("blue creatures");
	private static final FilterControlledCreaturePermanent filter2 = new FilterControlledCreaturePermanent("untapped blue creature");

	static {
		filter.getColor().setBlue(true);
		filter.setUseColor(true);
		filter2.getColor().setBlue(true);
		filter2.setUseColor(true);
		filter2.setTapped(false);
		filter2.setUseTapped(true);
	}

	public GrandArchitect(UUID ownerId) {
		super(ownerId, 33, "Grand Architect", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}{U}{U}");
		this.expansionSetCode = "SOM";
		this.subtype.add("Vedalken");
		this.subtype.add("Artificer");
		this.color.setBlue(true);
		this.power = new MageInt(1);
		this.toughness = new MageInt(3);

		this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new BoostControlledEffect(1, 1, Duration.WhileOnBattlefield, filter, true)));
		this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new GrandArchitectEffect(), new ManaCostsImpl("{U}")));
		this.addAbility(new SimpleManaAbility(Zone.BATTLEFIELD, new ManaEffect(Mana.ColorlessMana(2)), new TapTargetCost(new TargetControlledCreaturePermanent(1, 1, filter2, true))));
		//TODO: add filter to mana
	}

	public GrandArchitect(final GrandArchitect card) {
		super(card);
	}

	@Override
	public GrandArchitect copy() {
		return new GrandArchitect(this);
	}

}

class GrandArchitectEffect extends ContinuousEffectImpl<GrandArchitectEffect> {

	public GrandArchitectEffect() {
		super(Duration.EndOfTurn, Layer.ColorChangingEffects_5, SubLayer.NA, Outcome.Detriment);
	}

	public GrandArchitectEffect(final GrandArchitectEffect effect) {
		super(effect);
	}

	@Override
	public GrandArchitectEffect copy() {
		return new GrandArchitectEffect(this);
	}

	@Override
	public boolean apply(Game game, Ability source) {
		Permanent permanent = game.getPermanent(source.getFirstTarget());
		if (permanent != null) {
			permanent.getColor().setRed(true);
			permanent.getColor().setWhite(false);
			permanent.getColor().setGreen(false);
			permanent.getColor().setBlue(false);
			permanent.getColor().setBlack(false);
			return true;
		}
		return false;
	}

	@Override
	public String getText(Ability source) {
		return "Target artifact creature becomes blue until end of turn";
	}
}
