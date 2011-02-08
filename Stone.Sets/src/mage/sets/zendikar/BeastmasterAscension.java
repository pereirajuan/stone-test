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

package mage.sets.zendikar;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Duration;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.effects.common.BoostControlledEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.Permanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class BeastmasterAscension extends CardImpl<BeastmasterAscension> {

	public BeastmasterAscension(UUID ownerId) {
		super(ownerId, 159, "Beastmaster Ascension", Rarity.RARE, new CardType[]{CardType.ENCHANTMENT}, "{2}{G}");
		this.expansionSetCode = "ZEN";
		this.color.setGreen(true);

		this.addAbility(new BeastmasterAscensionAbility());
		this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new BeastmasterAscensionEffect()));
	}

	public BeastmasterAscension(final BeastmasterAscension card) {
		super(card);
	}

	@Override
	public BeastmasterAscension copy() {
		return new BeastmasterAscension(this);
	}

	@Override
	public String getArt() {
		return "125085_typ_reg_sty_010.jpg";
	}

}

class BeastmasterAscensionAbility extends TriggeredAbilityImpl<BeastmasterAscensionAbility> {

	public BeastmasterAscensionAbility() {
		super(Zone.BATTLEFIELD, new AddCountersSourceEffect("quest", 1), true);
	}

	public BeastmasterAscensionAbility(final BeastmasterAscensionAbility ability) {
		super(ability);
	}

	@Override
	public BeastmasterAscensionAbility copy() {
		return new BeastmasterAscensionAbility(this);
	}

	@Override
	public boolean checkTrigger(GameEvent event, Game game) {
		if (event.getType() == EventType.ATTACKER_DECLARED) {
			Permanent source = game.getPermanent(event.getSourceId());
			if (source != null && source.getControllerId().equals(controllerId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getRule() {
		return "Whenever a creature you control attacks, you may put a quest counter on Beastmaster Ascension.";
	}

}

class BeastmasterAscensionEffect extends BoostControlledEffect {

	public BeastmasterAscensionEffect() {
		super(5, 5, Duration.WhileOnBattlefield);
	}

	public BeastmasterAscensionEffect(final BeastmasterAscensionEffect effect) {
		super(effect);
	}

	@Override
	public boolean apply(Game game, Ability source) {
		Permanent permanent = game.getPermanent(source.getSourceId());
		if (permanent != null && permanent.getCounters().getCount("quest") > 6) {
			super.apply(game, source);
		}
		return false;
	}

	@Override
	public BeastmasterAscensionEffect copy() {
		return new BeastmasterAscensionEffect(this);
	}

	@Override
	public String getText(Ability source) {
		return "As long as Beastmaster Ascension has seven or more quest counters on it, creatures you control get +5/+5";
	}
}