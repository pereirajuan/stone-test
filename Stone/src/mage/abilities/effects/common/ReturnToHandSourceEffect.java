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

import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.constants.Outcome;
import mage.constants.Zone;
import static mage.constants.Zone.BATTLEFIELD;
import static mage.constants.Zone.GRAVEYARD;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

/**
 * @author Loki
 */
public class ReturnToHandSourceEffect extends OneShotEffect {

    boolean fromBattlefieldOnly = false;

    public ReturnToHandSourceEffect() {
        this(false);
    }

    public ReturnToHandSourceEffect(boolean fromBattlefieldOnly) {
        super(Outcome.ReturnToHand);
        this.fromBattlefieldOnly = fromBattlefieldOnly;
        staticText = "return {this} to it's owner's hand";
    }

    public ReturnToHandSourceEffect(final ReturnToHandSourceEffect effect) {
        super(effect);
        this.fromBattlefieldOnly = effect.fromBattlefieldOnly;
    }

    @Override
    public ReturnToHandSourceEffect copy() {
        return new ReturnToHandSourceEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            MageObject mageObject = source.getSourceObjectIfItStillExists(game);
            if (mageObject != null) {
                switch (game.getState().getZone(mageObject.getId())) {
                    case BATTLEFIELD:
                        Permanent permanent = game.getPermanent(source.getSourceId());
                        if (permanent != null) {
                            return controller.moveCardToHandWithInfo(permanent, source.getSourceId(), game, Zone.BATTLEFIELD);
                        }
                        break;
                    case GRAVEYARD:
                        Card card = (Card) mageObject;
                        if (!fromBattlefieldOnly) {
                            return controller.moveCardToHandWithInfo(card, source.getSourceId(), game, Zone.GRAVEYARD);
                        }
                }
            }
            return true;
        }
        return false;
    }
}
