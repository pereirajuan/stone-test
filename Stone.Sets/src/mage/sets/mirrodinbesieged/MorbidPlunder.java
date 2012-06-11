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
import mage.Constants.CardType;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.filter.common.FilterCreatureCard;
import mage.game.Game;
import mage.target.common.TargetCardInYourGraveyard;

/**
 *
 * @author North
 */
public class MorbidPlunder extends CardImpl<MorbidPlunder> {

    public MorbidPlunder(UUID ownerId) {
        super(ownerId, 47, "Morbid Plunder", Rarity.COMMON, new CardType[]{CardType.SORCERY}, "{1}{B}{B}");
        this.expansionSetCode = "MBS";

        this.color.setBlack(true);

        this.getSpellAbility().addEffect(new MorbidPlunderEffect());
        this.getSpellAbility().addTarget(new TargetCardInYourGraveyard(0, 2, new FilterCreatureCard("creature cards from your graveyard")));
    }

    public MorbidPlunder(final MorbidPlunder card) {
        super(card);
    }

    @Override
    public MorbidPlunder copy() {
        return new MorbidPlunder(this);
    }
}

class MorbidPlunderEffect extends OneShotEffect<MorbidPlunderEffect> {

    public MorbidPlunderEffect() {
        super(Outcome.ReturnToHand);
        staticText = "Return up to two target creature cards from your graveyard to your hand";
    }

    public MorbidPlunderEffect(final MorbidPlunderEffect effect) {
        super(effect);
    }

    @Override
    public MorbidPlunderEffect copy() {
        return new MorbidPlunderEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        boolean result = false;
        for (UUID target : targetPointer.getTargets(game, source)) {
            Card card = game.getCard(target);
            if (card != null) {
                result |= card.moveToZone(Zone.HAND, source.getId(), game, true);
            }
        }
        return result;
    }
}
