/*
 *  Copyright 2011 BetaSteward_at_googlemail.com. All rights reserved.
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
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardsImpl;
import mage.cards.repository.CardRepository;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.game.Game;
import mage.players.Player;
import mage.target.TargetPlayer;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class Memoricide extends CardImpl {

    public Memoricide(UUID ownerId) {
        super(ownerId, 69, "Memoricide", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{3}{B}");
        this.expansionSetCode = "SOM";


        // Name a nonland card. Search target player's graveyard, hand, and library for any number of cards with
        // that name and exile them. Then that player shuffles his or her library
        this.getSpellAbility().addTarget(new TargetPlayer());
        this.getSpellAbility().addEffect(new MemoricideEffect());
    }

    public Memoricide(final Memoricide card) {
        super(card);
    }

    @Override
    public Memoricide copy() {
        return new Memoricide(this);
    }

}

class MemoricideEffect extends OneShotEffect {

    public MemoricideEffect() {
        super(Outcome.Exile);
        staticText = "Name a nonland card. Search target player's graveyard, hand, and library for any number of cards with that name and exile them. Then that player shuffles his or her library";
    }

    public MemoricideEffect(final MemoricideEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(targetPointer.getFirst(game, source));
        Player controller = game.getPlayer(source.getControllerId());
        if (player != null && controller != null) {
            Choice cardChoice = new ChoiceImpl();
            cardChoice.setChoices(CardRepository.instance.getNonLandNames());
            cardChoice.clearChoice();

            while (!controller.choose(Outcome.Exile, cardChoice, game)) {
                if (!controller.isInGame()) {
                    return false;
                }
            }

            String cardName = cardChoice.getChoice();
            game.informPlayers("Memoricide, named card: [" + cardName + "]");
            for (Card card: player.getGraveyard().getCards(game)) {
                if (card.getName().equals(cardName)) {
                    card.moveToExile(null, "", source.getSourceId(), game);                    
                }
            }
            for (Card card: player.getHand().getCards(game)) {
                if (card.getName().equals(cardName)) {
                    card.moveToExile(null, "", source.getSourceId(), game);                    
                }
            }
            for (Card card: player.getLibrary().getCards(game)) {
                if (card.getName().equals(cardName)) {
                    card.moveToExile(null, "", source.getSourceId(), game);                    
                }
            }
            controller.lookAtCards("Memoricide Hand", player.getHand(), game);
            controller.lookAtCards("Memoricide Library", new CardsImpl(Zone.PICK, player.getLibrary().getCards(game)), game);
            player.shuffleLibrary(game);
        }
        return true;
    }

    @Override
    public MemoricideEffect copy() {
        return new MemoricideEffect(this);
    }

}