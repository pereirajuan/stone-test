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
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.abilities.Ability;
import mage.abilities.costs.common.SacrificeTargetCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetControlledCreaturePermanent;
import mage.target.common.TargetCreaturePermanent;
import mage.watchers.Watcher;
import mage.watchers.WatcherImpl;

/**
 *
 * @author nantuko
 */
public class FleshAllergy extends CardImpl<FleshAllergy> {

    public FleshAllergy (UUID ownerId) {
        super(ownerId, 62, "Flesh Allergy", Rarity.UNCOMMON, new CardType[]{CardType.SORCERY}, "{2}{B}{B}");
        this.expansionSetCode = "SOM";
		this.color.setBlack(true);
		this.getSpellAbility().addCost(new SacrificeTargetCost(new TargetControlledCreaturePermanent()));
		this.getSpellAbility().addEffect(new DestroyTargetEffect());
		this.getSpellAbility().addTarget(new TargetCreaturePermanent());
		this.getSpellAbility().addEffect(new FleshAllergyEffect());
		this.addWatcher(new FleshAllergyWatcher());
    }

    public FleshAllergy (final FleshAllergy card) {
        super(card);
    }

    @Override
    public FleshAllergy copy() {
        return new FleshAllergy(this);
    }
}

class FleshAllergyWatcher extends WatcherImpl<FleshAllergyWatcher> {

	public int creaturesDiedThisTurn = 0;

	public FleshAllergyWatcher() {
		super("CreaturesDiedFleshAllergy");
	}

	public FleshAllergyWatcher(final FleshAllergyWatcher watcher) {
		super(watcher);
	}

	@Override
	public FleshAllergyWatcher copy() {
		return new FleshAllergyWatcher(this);
	}

	@Override
	public void watch(GameEvent event, Game game) {
		if (event.getType() == EventType.ZONE_CHANGE) {
			if (((ZoneChangeEvent)event).getFromZone() == Zone.BATTLEFIELD &&
					 ((ZoneChangeEvent)event).getToZone() == Zone.GRAVEYARD) {
				Card card = game.getLastKnownInformation(event.getTargetId(), Zone.BATTLEFIELD);
				if (card != null && card.getCardType().contains(CardType.CREATURE)) {
					creaturesDiedThisTurn++;
				}
			}
		}
	}

	@Override
	public void reset() {
		super.reset();
		creaturesDiedThisTurn = 0;
	}

}

class FleshAllergyEffect extends OneShotEffect<FleshAllergyEffect> {

	public FleshAllergyEffect() {
		super(Outcome.DestroyPermanent);
		staticText = "Target creature gets +2/+2 until end of turn.\nLandfall - If you had a land enter the battlefield under your control this turn, that creature gets +4/+4 until end of turn instead";
	}

	public FleshAllergyEffect(final FleshAllergyEffect effect) {
		super(effect);
	}

	@Override
	public FleshAllergyEffect copy() {
		return new FleshAllergyEffect(this);
	}

	@Override
	public boolean apply(Game game, Ability source) {
		Watcher watcher = game.getState().getWatchers().get(source.getControllerId(), "CreaturesDiedFleshAllergy");
		Card card = game.getLastKnownInformation(source.getFirstTarget(), Zone.BATTLEFIELD);
		if (card != null && watcher != null) {
			Player player = game.getPlayer(((Permanent)card).getControllerId());
			if (player != null) {
				int amount = ((FleshAllergyWatcher)watcher).creaturesDiedThisTurn;
				if (amount > 0) {
					player.loseLife(amount, game);
					return true;
				}
			}
		}
		return false;
	}

}
