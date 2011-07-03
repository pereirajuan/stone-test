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
package mage.sets.worldwake;

import java.util.ArrayList;
import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTappedAbility;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.mana.BlackManaAbility;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.players.Player;
import mage.target.TargetPlayer;

/**
 *
 * @author North
 */
public class BojukaBog extends CardImpl<BojukaBog> {

    public BojukaBog(UUID ownerId) {
        super(ownerId, 132, "Bojuka Bog", Rarity.COMMON, new CardType[]{CardType.LAND}, "");
        this.expansionSetCode = "WWK";

        this.addAbility(new EntersBattlefieldTappedAbility());
        EntersBattlefieldTriggeredAbility ability = new EntersBattlefieldTriggeredAbility(new BojukaBogEffect());
        ability.addTarget(new TargetPlayer());
        this.addAbility(ability);
        this.addAbility(new BlackManaAbility());
    }

    public BojukaBog(final BojukaBog card) {
        super(card);
    }

    @Override
    public BojukaBog copy() {
        return new BojukaBog(this);
    }
}

class BojukaBogEffect extends OneShotEffect<BojukaBogEffect> {

    public BojukaBogEffect() {
        super(Outcome.Exile);
    }

    @Override
    public BojukaBogEffect copy() {
        return new BojukaBogEffect();
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player targetPlayer = game.getPlayer(source.getFirstTarget());
        if (targetPlayer != null) {
            ArrayList<UUID> graveyard = new ArrayList<UUID>(targetPlayer.getGraveyard());
            for (UUID cardId : graveyard) {
                game.getCard(cardId).moveToZone(Zone.EXILED, cardId, game, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getText(Ability source) {
        return "exile all cards from target player's graveyard";
    }
}
