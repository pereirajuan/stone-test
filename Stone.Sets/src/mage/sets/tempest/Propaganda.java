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

package mage.sets.tempest;

import java.util.UUID;

import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.players.Player;

/**
 *
 * @author Loki
 */
public class Propaganda extends CardImpl<Propaganda> {

    public Propaganda (UUID ownerId) {
        super(ownerId, 80, "Propaganda", Rarity.UNCOMMON, new CardType[]{CardType.ENCHANTMENT}, "{2}{U}");
        this.expansionSetCode = "TMP";
		this.color.setBlue(true);
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, new PropagandaReplacementEffect()));
    }

    public Propaganda (final Propaganda card) {
        super(card);
    }

    @Override
    public Propaganda copy() {
        return new Propaganda(this);
    }
}

class PropagandaReplacementEffect extends ReplacementEffectImpl<PropagandaReplacementEffect> {

	private static final String effectText = "Creatures can't attack you unless their controller pays {2} for each creature he or she controls that's attacking you";

	PropagandaReplacementEffect ( ) {
		super(Constants.Duration.WhileOnBattlefield, Constants.Outcome.Neutral);
	}

	PropagandaReplacementEffect ( PropagandaReplacementEffect effect ) {
		super(effect);
	}

	@Override
	public boolean apply(Game game, Ability source) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean replaceEvent(GameEvent event, Ability source, Game game) {
		if ( event.getType() == GameEvent.EventType.DECLARE_ATTACKER) {
			Player player = game.getPlayer(event.getPlayerId());

			if ( player != null ) {
				ManaCostsImpl propagandaTax = new ManaCostsImpl("{2}");
				if ( propagandaTax.canPay(source.getSourceId(), event.getPlayerId(), game) &&
					 player.chooseUse(Constants.Outcome.Neutral, "Pay {2} to declare attacker?", game) )
				{
					propagandaTax.pay(game, this.getId(), event.getPlayerId(), false);

					if ( propagandaTax.isPaid() ) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean applies(GameEvent event, Ability source, Game game) {
		if ( event.getType() == GameEvent.EventType.DECLARE_ATTACKER ) {
			return true;
		}
		return false;
	}

	@Override
	public PropagandaReplacementEffect copy() {
		return new PropagandaReplacementEffect(this);
	}

	@Override
	public String getText(Ability source) {
		return effectText;
	}
}
