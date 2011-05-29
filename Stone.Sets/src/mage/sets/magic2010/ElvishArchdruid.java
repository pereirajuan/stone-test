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

package mage.sets.magic2010;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Duration;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.MageInt;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.common.continious.BoostControlledEffect;
import mage.abilities.effects.common.ManaEffect;
import mage.abilities.mana.ManaAbility;
import mage.cards.CardImpl;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ElvishArchdruid extends CardImpl<ElvishArchdruid> {

	private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("Elf creatures");

	static {
		filter.getSubtype().add("Elf");
	}

	public ElvishArchdruid(UUID ownerId) {
		super(ownerId, 176, "Elvish Archdruid", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}{G}{G}");
		this.expansionSetCode = "M10";
		this.subtype.add("Elf");
		this.subtype.add("Druid");
		this.color.setGreen(true);
		this.power = new MageInt(2);
		this.toughness = new MageInt(2);

		this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new BoostControlledEffect(1, 1, Duration.WhileOnBattlefield, filter, true)));
		this.addAbility(new ElvishArchdruidAbility());
	}

	public ElvishArchdruid(final ElvishArchdruid card) {
		super(card);
	}

	@Override
	public ElvishArchdruid copy() {
		return new ElvishArchdruid(this);
	}

	class ElvishArchdruidAbility extends ManaAbility<ElvishArchdruidAbility> {

		public ElvishArchdruidAbility() {
			super(Zone.BATTLEFIELD, new ElvishArchdruidEffect(), new TapSourceCost());
		}

		public ElvishArchdruidAbility(final ElvishArchdruidAbility ability) {
			super(ability);
		}

		@Override
		public ElvishArchdruidAbility copy() {
			return new ElvishArchdruidAbility(this);
		}

		@Override
		public Mana getNetMana(Game game) {
			if (game == null)
				return new Mana();
			return Mana.GreenMana(game.getBattlefield().countAll(filter, controllerId));
		}

	}

	class ElvishArchdruidEffect extends ManaEffect {

		public ElvishArchdruidEffect() {
			super(new Mana());
		}

		public ElvishArchdruidEffect(final ElvishArchdruidEffect effect) {
			super(effect);
		}

		@Override
		public ElvishArchdruidEffect copy() {
			return new ElvishArchdruidEffect(this);
		}

		@Override
		public boolean apply(Game game, Ability source) {
			this.mana.clear();
			int amount = game.getBattlefield().countAll(filter, source.getControllerId());
			this.mana.setGreen(amount);
			return super.apply(game, source);
		}

		@Override
		public String getText(Ability source) {
			return "Add {G} to your mana pool for each Elf you control";
		}

	}
}

