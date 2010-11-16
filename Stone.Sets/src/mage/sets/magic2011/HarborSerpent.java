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
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.keyword.IslandwalkAbility;
import mage.cards.CardImpl;
import mage.filter.Filter.ComparisonScope;
import mage.filter.common.FilterLandPermanent;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class HarborSerpent extends CardImpl<HarborSerpent> {

	public HarborSerpent(UUID ownerId) {
		super(ownerId, 56, "Harbor Serpent", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{4}{U}{U}");
		this.expansionSetCode = "M11";
		this.subtype.add("Serpent");
		this.color.setBlue(true);
		this.power = new MageInt(5);
		this.toughness = new MageInt(5);

		this.addAbility(IslandwalkAbility.getInstance());
		this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new HarborSerpentEffect()));
	}

	public HarborSerpent(final HarborSerpent card) {
		super(card);
	}

	@Override
	public HarborSerpent copy() {
		return new HarborSerpent(this);
	}

	@Override
	public String getArt() {
		return "129069_typ_reg_sty_010.jpg";
	}

}

class HarborSerpentEffect extends ReplacementEffectImpl<HarborSerpentEffect> {

	private static FilterLandPermanent filter = new FilterLandPermanent("Island");

	static {
		filter.getSubtype().add("Island");
		filter.setScopeSubtype(ComparisonScope.Any);
	}

	public HarborSerpentEffect() {
		super(Duration.WhileOnBattlefield, Outcome.Benefit);
	}

	public HarborSerpentEffect(final HarborSerpentEffect effect) {
		super(effect);
	}

	@Override
	public HarborSerpentEffect copy() {
		return new HarborSerpentEffect(this);
	}

	@Override
	public boolean apply(Game game, Ability source) {
		return true;
	}

	@Override
	public boolean replaceEvent(GameEvent event, Ability source, Game game) {
		return true;
	}

	@Override
	public boolean applies(GameEvent event, Ability source, Game game) {
		if (event.getType() == EventType.DECLARE_ATTACKER && source.getSourceId().equals(event.getSourceId())) {
			int count = game.getBattlefield().countAll(filter);
			if (count < 5)
				return true;
		}
		return false;
	}

	@Override
	public String getText(Ability source) {
		return "Harbor Serpent can't attack unless there are five or more Islands on the battlefield";
	}

}
