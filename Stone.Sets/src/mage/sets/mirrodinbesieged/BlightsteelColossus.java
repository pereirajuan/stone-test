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

package mage.sets.mirrodinbesieged;

import java.util.UUID;

import mage.constants.CardType;
import mage.constants.Rarity;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.PutIntoGraveFromAnywhereTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.IndestructibleAbility;
import mage.abilities.keyword.InfectAbility;
import mage.abilities.keyword.TrampleAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.players.Player;

/**
 * @author Loki
 */
public class BlightsteelColossus extends CardImpl<BlightsteelColossus> {

    public BlightsteelColossus(UUID ownerId) {
        super(ownerId, 99, "Blightsteel Colossus", Rarity.MYTHIC, new CardType[]{CardType.ARTIFACT, CardType.CREATURE}, "{12}");
        this.expansionSetCode = "MBS";
        this.subtype.add("Golem");
        this.power = new MageInt(11);
        this.toughness = new MageInt(11);
        this.addAbility(TrampleAbility.getInstance());
        this.addAbility(InfectAbility.getInstance());
        this.addAbility(IndestructibleAbility.getInstance());
        this.addAbility(new PutIntoGraveFromAnywhereTriggeredAbility(new BlightsteelColossusEffect(), false));
    }

    public BlightsteelColossus(final BlightsteelColossus card) {
        super(card);
    }

    @Override
    public BlightsteelColossus copy() {
        return new BlightsteelColossus(this);
    }

}

class BlightsteelColossusEffect extends OneShotEffect<BlightsteelColossusEffect> {
    BlightsteelColossusEffect() {
        super(Outcome.Benefit);
        staticText = "reveal {this} and shuffle it into its owner's library";
    }

    BlightsteelColossusEffect(final BlightsteelColossusEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card c = game.getCard(source.getSourceId());
        if (c != null) {
            Player player = game.getPlayer(c.getOwnerId());
            if (player != null) {
                Cards cards = new CardsImpl();
                cards.add(c);
                player.revealCards("Blightsteel Colossus", cards, game);
                c.moveToZone(Zone.LIBRARY, source.getSourceId(), game, true);
                player.shuffleLibrary(game);
                return true;
            }
        }
        return false;
    }

    @Override
    public BlightsteelColossusEffect copy() {
        return new BlightsteelColossusEffect(this);
    }

}