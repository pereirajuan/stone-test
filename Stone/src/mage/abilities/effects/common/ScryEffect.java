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

package mage.abilities.effects.common;

import mage.constants.Outcome;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.filter.FilterCard;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.players.Player;
import mage.target.TargetCard;
import mage.util.CardUtil;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ScryEffect extends OneShotEffect<ScryEffect> {

    protected static FilterCard filter1 = new FilterCard("card to put on the bottom of your library");
    protected static FilterCard filter2 = new FilterCard("card to put on the top of your library (last chosen will be on top)");

    protected int scryNumber;

    public ScryEffect(int scryNumber) {
        super(Outcome.Benefit);
        this.scryNumber = scryNumber;
        this.setText();
    }

    public ScryEffect(final ScryEffect effect) {
        super(effect);
        this.scryNumber = effect.scryNumber;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player != null) {
            Cards cards = new CardsImpl(Zone.PICK);
            int count = Math.min(scryNumber, player.getLibrary().size());
            if (count == 0) {
                return false;
            }
            for (int i = 0; i < count; i++) {
                Card card = player.getLibrary().removeFromTop(game);
                cards.add(card);
                game.setZone(card.getId(), Zone.PICK);
            }
            TargetCard target1 = new TargetCard(Zone.PICK, filter1);
            // move cards to the bottom of the library
            while (cards.size() > 0 && player.choose(Outcome.Detriment, cards, target1, game)) {
                Card card = cards.get(target1.getFirstTarget(), game);
                if (card != null) {
                    cards.remove(card);
                    card.moveToZone(Zone.LIBRARY, source.getId(), game, false);
                }
                target1.clearChosen();
            }
            // move cards to the top of the library
            int onBottom = scryNumber - cards.size();
            if (cards.size() > 1) {
                TargetCard target2 = new TargetCard(Zone.PICK, filter2);
                target2.setRequired(true);
                while (cards.size() > 1) {
                    player.choose(Outcome.Benefit, cards, target2, game);
                    Card card = cards.get(target2.getFirstTarget(), game);
                    if (card != null) {
                        cards.remove(card);
                        card.moveToZone(Zone.LIBRARY, source.getId(), game, true);
                    }
                    target2.clearChosen();
                }
            }
            if (cards.size() == 1) {
                Card card = cards.get(cards.iterator().next(), game);
                card.moveToZone(Zone.LIBRARY, source.getId(), game, true);
            }
            game.informPlayers(new StringBuilder(player.getName()).append(" puts ")
                    .append(onBottom).append(onBottom == 1 ?" card":" cards")
                    .append(" on the bottom of his or her library (scry ")
                    .append(scryNumber).append(")").toString());
            game.fireEvent(new GameEvent(GameEvent.EventType.SCRY, source.getControllerId(), source.getSourceId(), source.getControllerId()));
            return true;
        }
        return false;
    }

    @Override
    public ScryEffect copy() {
        return new ScryEffect(this);
    }

    private void setText() {
        StringBuilder sb = new StringBuilder("Scry ").append(scryNumber);
        if (scryNumber == 1) {
            sb.append(". <i>(Look at the top card of your library. You may put that card on the bottom of your library.)</i>");
        } else {
            sb.append(". <i>(Look at the top ");
            sb.append(CardUtil.numberToText(scryNumber));
            sb.append(" cards of your library, then put any number of them on the bottom of your library and the rest on top in any order.)</i>");
        }
        staticText = sb.toString();
    }
}
