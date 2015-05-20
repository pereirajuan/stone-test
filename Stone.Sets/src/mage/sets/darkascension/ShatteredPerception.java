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
package mage.sets.darkascension;

import java.util.Set;
import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.TimingRule;
import mage.abilities.Ability;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.FlashbackAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author North
 */
public class ShatteredPerception extends CardImpl {

    public ShatteredPerception(UUID ownerId) {
        super(ownerId, 104, "Shattered Perception", Rarity.UNCOMMON, new CardType[]{CardType.SORCERY}, "{2}{R}");
        this.expansionSetCode = "DKA";


        // Discard all the cards in your hand, then draw that many cards.
        this.getSpellAbility().addEffect(new ShatteredPerceptionEffect());
        // Flashback {5}{R}
        this.addAbility(new FlashbackAbility(new ManaCostsImpl("{5}{R}"), TimingRule.SORCERY));
    }

    public ShatteredPerception(final ShatteredPerception card) {
        super(card);
    }

    @Override
    public ShatteredPerception copy() {
        return new ShatteredPerception(this);
    }
}

class ShatteredPerceptionEffect extends OneShotEffect {

    public ShatteredPerceptionEffect() {
        super(Outcome.DrawCard);
        this.staticText = "Discard all the cards in your hand, then draw that many cards";
    }

    public ShatteredPerceptionEffect(final ShatteredPerceptionEffect effect) {
        super(effect);
    }

    @Override
    public ShatteredPerceptionEffect copy() {
        return new ShatteredPerceptionEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player != null) {
            Set<Card> cardsInHand = player.getHand().getCards(game);
            int amount = cardsInHand.size();
            for (Card card : cardsInHand) {
                player.discard(card, source, game);
            }
            player.drawCards(amount, game);
            return true;
        }
        return false;
    }
}
