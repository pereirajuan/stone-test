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
package mage.sets.returntoravnica;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.PopulateEffect;
import mage.cards.CardImpl;
import mage.game.permanent.token.CentaurToken;

/**
 *
 * @author LevleX2
 */
public class CoursersAccord extends CardImpl {

    public CoursersAccord(UUID ownerId) {
        super(ownerId, 154, "Coursers' Accord", Rarity.COMMON, new CardType[]{CardType.SORCERY}, "{4}{G}{W}");
        this.expansionSetCode = "RTR";


        // Put a 3/3 green Centaur creature token onto the battlefield, then populate.
        // (Put a token onto the battlefield that's a copy of a creature token you control.)
        this.getSpellAbility().addEffect(new CreateTokenEffect(new CentaurToken()));
        this.getSpellAbility().addEffect(new PopulateEffect("then"));
    }

    public CoursersAccord(final CoursersAccord card) {
        super(card);
    }

    @Override
    public CoursersAccord copy() {
        return new CoursersAccord(this);
    }
}
