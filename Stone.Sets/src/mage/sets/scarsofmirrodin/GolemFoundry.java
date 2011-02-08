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

package mage.sets.scarsofmirrodin;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.RemoveCountersSourceCost;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.cards.CardImpl;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.token.Token;
import mage.game.stack.Spell;

/**
 *
 * @author Loki
 */
public class GolemFoundry extends CardImpl<GolemFoundry> {

    public GolemFoundry (UUID ownerId) {
        super(ownerId, 160, "Golem Foundry", Rarity.COMMON, new CardType[]{CardType.ARTIFACT}, "{3}");
        this.expansionSetCode = "SOM";
        this.addAbility(new GolemFoundryAbility());
        this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new CreateTokenEffect(new GolemToken()), new RemoveCountersSourceCost(CounterType.CHARGE.createInstance(3))));
    }

    public GolemFoundry (final GolemFoundry card) {
        super(card);
    }

    @Override
    public GolemFoundry copy() {
        return new GolemFoundry(this);
    }

}

class GolemFoundryAbility extends TriggeredAbilityImpl<GolemFoundryAbility> {

	public GolemFoundryAbility() {
		super(Zone.BATTLEFIELD, new AddCountersSourceEffect(CounterType.CHARGE.createInstance()), true);
	}

	public GolemFoundryAbility(final GolemFoundryAbility ability) {
		super(ability);
	}

	@Override
	public GolemFoundryAbility copy() {
		return new GolemFoundryAbility(this);
	}

	@Override
	public boolean checkTrigger(GameEvent event, Game game) {
		if (event.getType() == GameEvent.EventType.SPELL_CAST) {
			Spell spell = game.getStack().getSpell(event.getTargetId());
			if (spell != null && spell.getCardType().contains(CardType.ARTIFACT)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getRule() {
		return "Whenever you cast an artifact spell, you may put a charge counter on Golem Foundry.";
	}
}

class GolemToken extends Token {
    public GolemToken() {
        super("Golem", "a 3/3 colorless Golem artifact creature token");
        cardType.add(CardType.ARTIFACT);
        cardType.add(CardType.CREATURE);
		subtype.add("Golem");
		power = new MageInt(3);
		toughness = new MageInt(3);
    }
}