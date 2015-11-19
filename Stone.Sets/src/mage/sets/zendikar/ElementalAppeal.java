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
package mage.sets.zendikar;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.abilities.Ability;
import mage.abilities.common.delayed.AtTheBeginOfNextEndStepDelayedTriggeredAbility;
import mage.abilities.condition.LockedInCondition;
import mage.abilities.condition.common.KickedCondition;
import mage.abilities.decorator.ConditionalContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.abilities.effects.common.continuous.BoostTargetEffect;
import mage.abilities.keyword.KickerAbility;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author North
 */
public class ElementalAppeal extends CardImpl {

    public ElementalAppeal(UUID ownerId) {
        super(ownerId, 123, "Elemental Appeal", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{R}{R}{R}{R}");
        this.expansionSetCode = "ZEN";

        // Kicker {5}
        this.addAbility(new KickerAbility("{5}"));

        // Put a 7/1 red Elemental creature token with trample and haste onto the battlefield. Exile it at the beginning of the next end step.
        this.getSpellAbility().addEffect(new ElementalAppealEffect());
        // If Elemental Appeal was kicked, that creature gets +7/+0 until end of turn.
        this.getSpellAbility().addEffect(new ConditionalContinuousEffect(
                new BoostTargetEffect(7, 0, Duration.EndOfTurn),
                new LockedInCondition(KickedCondition.getInstance()),
                "If {this} was kicked, that creature gets +7/+0 until end of turn"));
    }

    public ElementalAppeal(final ElementalAppeal card) {
        super(card);
    }

    @Override
    public ElementalAppeal copy() {
        return new ElementalAppeal(this);
    }
}

class ElementalAppealEffect extends OneShotEffect {

    public ElementalAppealEffect() {
        super(Outcome.PutCreatureInPlay);
        this.staticText = "Put a 7/1 red Elemental creature token with trample and haste onto the battlefield. Exile it at the beginning of the next end step";
    }

    public ElementalAppealEffect(final ElementalAppealEffect effect) {
        super(effect);
    }

    @Override
    public ElementalAppealEffect copy() {
        return new ElementalAppealEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        ElementalToken token = new ElementalToken();
        token.putOntoBattlefield(1, game, source.getSourceId(), source.getControllerId());

        for (UUID tokenId : token.getLastAddedTokenIds()) {
            Permanent tokenPermanent = game.getPermanent(tokenId);
            if (tokenPermanent != null) {
                ExileTargetEffect exileEffect = new ExileTargetEffect();
                exileEffect.setTargetPointer(new FixedTarget(tokenPermanent, game));
                game.addDelayedTriggeredAbility(new AtTheBeginOfNextEndStepDelayedTriggeredAbility(exileEffect), source);
            }
        }
        return true;
    }
}
