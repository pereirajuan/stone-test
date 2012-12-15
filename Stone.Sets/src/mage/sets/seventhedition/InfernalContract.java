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
package mage.sets.seventhedition;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DrawCardControllerEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author Plopman
 */
public class InfernalContract extends CardImpl<InfernalContract> {

    public InfernalContract(UUID ownerId) {
        super(ownerId, 143, "Infernal Contract", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{B}{B}{B}");
        this.expansionSetCode = "7ED";

        this.color.setBlack(true);

        // Draw four cards. You lose half your life, rounded up.
        this.getSpellAbility().addEffect(new DrawCardControllerEffect(4));
        this.getSpellAbility().addEffect(new InfernalContractLoseLifeEffect());
    }

    public InfernalContract(final InfernalContract card) {
        super(card);
    }

    @Override
    public InfernalContract copy() {
        return new InfernalContract(this);
    }
}

class InfernalContractLoseLifeEffect extends OneShotEffect<InfernalContractLoseLifeEffect> {

    public InfernalContractLoseLifeEffect() {
        super(Outcome.LoseLife);
        staticText = "You lose half your life, rounded up";
    }

    public InfernalContractLoseLifeEffect(final InfernalContractLoseLifeEffect effect) {
        super(effect);
    }

    @Override
    public InfernalContractLoseLifeEffect copy() {
        return new InfernalContractLoseLifeEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player != null) {
            int amount = (player.getLife() + 1) / 2;
            if (amount > 0) {
                player.loseLife(amount, game);
                return true;
            }
        }
        return false;
    }
}