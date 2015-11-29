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
import mage.abilities.Mode;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.game.Game;
import mage.players.Player;

import java.util.UUID;

/**
 * @author LevelX
 */
public class PutOntoBattlefieldTargetEffect extends OneShotEffect {

    boolean tapped;
    boolean optional;

    public PutOntoBattlefieldTargetEffect(boolean tapped) {
        this(tapped, false);
    }

    public PutOntoBattlefieldTargetEffect(boolean tapped, boolean optional) {
        super(Outcome.PutCreatureInPlay);
        this.tapped = tapped;
        this.optional = optional;
    }

    public PutOntoBattlefieldTargetEffect(final PutOntoBattlefieldTargetEffect effect) {
        super(effect);
        this.tapped = effect.tapped;
        this.optional = effect.optional;
    }

    @Override
    public PutOntoBattlefieldTargetEffect copy() {
        return new PutOntoBattlefieldTargetEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        boolean result = false;
        if (optional) {
            Player controller = game.getPlayer(source.getControllerId());
            if (controller == null || !controller.chooseUse(Outcome.PutCreatureInPlay,
                    new StringBuilder("Put ")
                    .append(source.getTargets() != null ? source.getTargets().get(0).getTargetName() : "target")
                    .append(" onto the battlefield?").toString(), source, game)) {
                return false;
            }
        }
        for (UUID targetId : targetPointer.getTargets(game, source)) {
            Card card = game.getCard(targetId);
            if (card != null) {
                switch (game.getState().getZone(targetId)) {
                    case GRAVEYARD:
                        for (Player player : game.getPlayers().values()) {
                            if (player.getGraveyard().contains(card.getId())) {
                                player.getGraveyard().remove(card);
                                result |= card.moveToZone(Zone.BATTLEFIELD, source.getSourceId(), game, tapped);
                            }
                        }
                    case HAND:
                        for (Player player : game.getPlayers().values()) {
                            if (player.getHand().contains(card.getId())) {
                                player.getHand().remove(card);
                                result |= card.moveToZone(Zone.BATTLEFIELD, source.getSourceId(), game, tapped);
                            }
                        }
                }
            }
        }
        return result;
    }

    @Override
    public String getText(Mode mode) {

        // You may put an artifact card from your hand onto the battlefield.
        StringBuilder sb = new StringBuilder();
        if (optional) {
            sb.append("You may put ");
        } else {
            sb.append("Put ");
        }
        if (mode.getTargets().get(0).getMaxNumberOfTargets() == 0) {
            sb.append("any number of ");
        }
        if (mode.getTargets() != null) {
            sb.append(mode.getTargets().get(0).getTargetName());
        }
        sb.append(tapped ? "tapped" : "").append(" onto the battlefield");
        return sb.toString();

    }
}
