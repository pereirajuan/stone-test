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
package mage.sets.avacynrestored;

import mage.constants.CardType;
import mage.constants.Rarity;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.common.TargetControlledPermanent;

import java.util.UUID;

/**
 * @author noxx
 */
public class GhostlyFlicker extends CardImpl {

    private static final FilterControlledPermanent filter = new FilterControlledPermanent("artifacts, creatures, and/or lands you control");

    static {
        filter.add(Predicates.or(
                new CardTypePredicate(CardType.CREATURE),
                new CardTypePredicate(CardType.LAND),
                new CardTypePredicate(CardType.ARTIFACT)));
    }

    public GhostlyFlicker(UUID ownerId) {
        super(ownerId, 57, "Ghostly Flicker", Rarity.COMMON, new CardType[]{CardType.INSTANT}, "{2}{U}");
        this.expansionSetCode = "AVR";


        // Exile two target artifacts, creatures, and/or lands you control, then return those cards to the battlefield under your control.
        this.getSpellAbility().addTarget(new TargetControlledPermanent(2, 2, filter, false));
        this.getSpellAbility().addEffect(new GhostlyFlickerEffect());
    }

    public GhostlyFlicker(final GhostlyFlicker card) {
        super(card);
    }

    @Override
    public GhostlyFlicker copy() {
        return new GhostlyFlicker(this);
    }
}

class GhostlyFlickerEffect extends OneShotEffect {

    public GhostlyFlickerEffect() {
        super(Outcome.Benefit);
        staticText = "Exile two target artifacts, creatures, and/or lands you control, then return those cards to the battlefield under your control";
    }

    public GhostlyFlickerEffect(final GhostlyFlickerEffect effect) {
        super(effect);
    }

    @Override
    public GhostlyFlickerEffect copy() {
        return new GhostlyFlickerEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        UUID exileId = source.getSourceId();
        for (UUID permanentId : targetPointer.getTargets(game, source)) {
            Permanent target = game.getPermanent(permanentId);
            if (target != null) {
                target.moveToExile(exileId, "Ghostly Flicker", source.getSourceId(), game);
                Card card = game.getCard(target.getId());
                if (card != null) {
                    Zone currentZone = game.getState().getZone(card.getId());
                    card.putOntoBattlefield(game, currentZone, source.getSourceId(), source.getControllerId());
                }
            }
        }

        return true;
    }
}

