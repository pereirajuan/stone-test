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
package mage.sets.innistrad;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.TargetController;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.filter.common.FilterControlledPermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetControlledPermanent;
import mage.target.common.TargetOpponent;

/**
 *
 * @author North
 */
public class TributeToHunger extends CardImpl<TributeToHunger> {

    public TributeToHunger(UUID ownerId) {
        super(ownerId, 119, "Tribute to Hunger", Rarity.UNCOMMON, new CardType[]{CardType.INSTANT}, "{2}{B}");
        this.expansionSetCode = "ISD";

        this.color.setBlack(true);

        // Target opponent sacrifices a creature. You gain life equal to that creature's toughness.
        this.getSpellAbility().addTarget(new TargetOpponent());
        this.getSpellAbility().addEffect(new TributeToHungerEffect());
    }

    public TributeToHunger(final TributeToHunger card) {
        super(card);
    }

    @Override
    public TributeToHunger copy() {
        return new TributeToHunger(this);
    }
}

class TributeToHungerEffect extends OneShotEffect<TributeToHungerEffect> {

    TributeToHungerEffect() {
        super(Outcome.Sacrifice);
        staticText = "Target opponent sacrifices a creature. You gain life equal to that creature's toughness";
    }

    TributeToHungerEffect(TributeToHungerEffect effect) {
        super(effect);
    }

    @Override
    public TributeToHungerEffect copy() {
        return new TributeToHungerEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getTargets().getFirstTarget());
        Player controller = game.getPlayer(source.getControllerId());

        FilterControlledPermanent filter = new FilterControlledPermanent("creature");
        filter.getCardType().add(CardType.CREATURE);
        filter.setTargetController(TargetController.YOU);
        TargetControlledPermanent target = new TargetControlledPermanent(1, 1, filter, false);

        if (target.canChoose(player.getId(), game)) {
            player.choose(Outcome.Sacrifice, target, source.getSourceId(), game);

            Permanent permanent = game.getPermanent(target.getFirstTarget());
            if (permanent != null) {
                controller.gainLife(permanent.getToughness().getValue(), game);
                return permanent.sacrifice(source.getId(), game);
            }
            return true;
        }
        return false;
    }
}
