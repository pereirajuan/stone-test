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
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.common.SacrificeSourceEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.Permanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class PhantomBeast extends CardImpl<PhantomBeast> {

	public PhantomBeast(UUID ownerId) {
		super(ownerId, "Phantom Beast", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{3}{U}");
		this.expansionSetCode = "M11";
		this.subtype.add("Illusion");
		this.subtype.add("Beast");
		this.color.setBlue(true);
		this.power = new MageInt(4);
		this.toughness = new MageInt(5);

		this.addAbility(new PhantomBeastAbility());
	}

	public PhantomBeast(final PhantomBeast card) {
		super(card);
	}

	@Override
	public PhantomBeast copy() {
		return new PhantomBeast(this);
	}

	@Override
	public String getArt() {
		return "129107_typ_reg_sty_010.jpg";
	}

	class PhantomBeastAbility extends TriggeredAbilityImpl<PhantomBeastAbility> {

		public PhantomBeastAbility() {
			super(Zone.BATTLEFIELD, new SacrificeSourceEffect());
		}

		public PhantomBeastAbility(final PhantomBeastAbility ability) {
			super(ability);
		}

		@Override
		public PhantomBeastAbility copy() {
			return new PhantomBeastAbility(this);
		}

		@Override
		public boolean checkTrigger(GameEvent event, Game game) {
			Permanent perm = game.getPermanent(sourceId);
			if (perm != null) {
				if (event.getTargetId().equals(perm.getId()) && event.getType() == EventType.TARGETED) {
					trigger(game, event.getPlayerId());
					return true;
				}
			}
			return false;
		}

		@Override
		public String getRule() {
			return "When Phantom Beast becomes the target of a spell or ability, sacrifice it";
		}

	}

}
