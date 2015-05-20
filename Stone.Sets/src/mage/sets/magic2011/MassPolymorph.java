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

import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class MassPolymorph extends CardImpl {

    public MassPolymorph(UUID ownerId) {
        super(ownerId, 64, "Mass Polymorph", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{5}{U}");
        this.expansionSetCode = "M11";

        this.getSpellAbility().addEffect(new MassPolymorphEffect());
    }

    public MassPolymorph(final MassPolymorph card) {
        super(card);
    }

    @Override
    public MassPolymorph copy() {
        return new MassPolymorph(this);
    }
}

class MassPolymorphEffect extends OneShotEffect {

    public MassPolymorphEffect() {
        super(Outcome.PutCreatureInPlay);
        staticText = "Exile all creatures you control, then reveal cards from the top of your library until you reveal that many creature cards. Put all creature cards revealed this way onto the battlefield, then shuffle the rest of the revealed cards into your library";
    }

    public MassPolymorphEffect(final MassPolymorphEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        int count;
        List<Permanent> creatures = game.getBattlefield().getAllActivePermanents(new FilterCreaturePermanent(), source.getControllerId(), game);
        count = creatures.size();
        for (Permanent creature: creatures) {
            creature.moveToExile(null, null, source.getSourceId(), game);
        }
        Cards revealed = new CardsImpl();
        Cards creatureCards = new CardsImpl();
        Cards nonCreatureCards = new CardsImpl();
        Player player = game.getPlayer(source.getControllerId());
        while (creatureCards.size() < count && player.getLibrary().size() > 0) {
            Card card = player.getLibrary().removeFromTop(game);
            revealed.add(card);
            if (card.getCardType().contains(CardType.CREATURE)) {
                creatureCards.add(card);
            } else {
                nonCreatureCards.add(card);
            }
        }
        player.revealCards("Mass Polymorph", revealed, game);
        for (Card creatureCard: creatureCards.getCards(game)) {
            creatureCard.putOntoBattlefield(game, Zone.LIBRARY, source.getSourceId(), source.getControllerId());
        }
        for (Card card: nonCreatureCards.getCards(game)) {
            player.moveCardToLibraryWithInfo(card, source.getSourceId(), game, Zone.GRAVEYARD, true, true);
        }             
        player.shuffleLibrary(game);
        return true;
    }

    @Override
    public MassPolymorphEffect copy() {
        return new MassPolymorphEffect(this);
    }

}