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
import mage.abilities.Ability;
import mage.abilities.condition.common.KickedCondition;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.BoostControlledEffect;
import mage.abilities.effects.common.continuous.GainAbilityControlledEffect;
import mage.abilities.keyword.FirstStrikeAbility;
import mage.abilities.keyword.KickerAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.players.Player;

/**
 * @author nantuko, Loki
 */
public class BoldDefense extends CardImpl {

    public BoldDefense(UUID ownerId) {
        super(ownerId, 3, "Bold Defense", Rarity.COMMON, new CardType[]{CardType.INSTANT}, "{2}{W}");
        this.expansionSetCode = "ZEN";


        // Kicker {3}{W} (You may pay an additional {3}{W} as you cast this spell.)
        this.addAbility(new KickerAbility("{3}{W}"));

        // Creatures you control get +1/+1 until end of turn. If Bold Defense was kicked, instead creatures you control get +2/+2 and gain first strike until end of turn.
        this.getSpellAbility().addEffect(new BoldDefenseEffect());
    }

    public BoldDefense(final BoldDefense card) {
        super(card);
    }

    @Override
    public BoldDefense copy() {
        return new BoldDefense(this);
    }
}

class BoldDefenseEffect extends OneShotEffect {

    public BoldDefenseEffect() {
        super(Outcome.BoostCreature);
        this.staticText = "Creatures you control get +1/+1 until end of turn. If {this} was kicked, instead creatures you control get +2/+2 and gain first strike until end of turn";
    }

    public BoldDefenseEffect(final BoldDefenseEffect effect) {
        super(effect);
    }

    @Override
    public BoldDefenseEffect copy() {
        return new BoldDefenseEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            if (KickedCondition.getInstance().apply(game, source)) {
                game.addEffect(new BoostControlledEffect(2, 2, Duration.EndOfTurn), source);
                game.addEffect(new GainAbilityControlledEffect(FirstStrikeAbility.getInstance(), Duration.EndOfTurn, new FilterCreaturePermanent(), false), source);
            } else {
                game.addEffect(new BoostControlledEffect(1, 1, Duration.EndOfTurn), source);
            }
        }
        return false;
    }
}
