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

package mage.sets.magic2011;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Duration;
import mage.Constants.Layer;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.SubLayer;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.cards.CardImpl;
import mage.filter.common.FilterLandPermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class EarthServant extends CardImpl<EarthServant> {

	public EarthServant(UUID ownerId) {
		super(ownerId, "Earth Servant", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{5}{R}");
		this.expansionSetCode = "M11";
		this.subtype.add("Elemental");
		this.color.setRed(true);
		this.power = new MageInt(4);
		this.toughness = new MageInt(4);

		this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new EarthServantEffect()));
	}

	public EarthServant(final EarthServant card) {
		super(card);
	}

	@Override
	public EarthServant copy() {
		return new EarthServant(this);
	}

	@Override
	public String getArt() {
		return "129080_typ_reg_sty_010.jpg";
	}

}

class EarthServantEffect extends ContinuousEffectImpl<EarthServantEffect> {

	private static FilterLandPermanent filter = new FilterLandPermanent("Mountain");

	static {
		filter.getSubtype().add("Mountain");
	}

	public EarthServantEffect() {
		super(Duration.WhileOnBattlefield, Outcome.BoostCreature);
	}

	public EarthServantEffect(final EarthServantEffect effect) {
		super(effect);
	}

	@Override
	public EarthServantEffect copy() {
		return new EarthServantEffect(this);
	}

	@Override
	public boolean apply(Layer layer, SubLayer sublayer, Ability source, Game game) {
		Permanent creature = game.getPermanent(source.getSourceId());
		if (creature  != null) {
			switch (layer) {
				case PTChangingEffects_7:
					if (sublayer == SubLayer.ModifyPT_7c) {
						int amount = game.getBattlefield().countAll(filter, source.getControllerId());
						creature.addToughness(amount);
					}
					break;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean apply(Game game, Ability source) {
		return false;
	}

	@Override
	public boolean hasLayer(Layer layer) {
		return layer == layer.PTChangingEffects_7;
	}

	@Override
	public String getText(Ability source) {
		return "Earth Servant gets +0/+1 for each Mountain you control";
	}

}