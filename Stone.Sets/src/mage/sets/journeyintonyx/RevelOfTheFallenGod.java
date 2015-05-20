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
package mage.sets.journeyintonyx;

import java.util.UUID;
import mage.MageInt;
import mage.ObjectColor;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.keyword.HasteAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.game.permanent.token.Token;

/**
 *
 * @author LevelX2
 */
public class RevelOfTheFallenGod extends CardImpl {

    public RevelOfTheFallenGod(UUID ownerId) {
        super(ownerId, 155, "Revel of the Fallen God", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{3}{R}{R}{G}{G}");
        this.expansionSetCode = "JOU";


        // Put four 2/2 red and green Satyr creature tokens with haste onto the battlefield.
        this.getSpellAbility().addEffect(new CreateTokenEffect(new RevelOfTheFallenGodSatyrToken(), 4));

    }

    public RevelOfTheFallenGod(final RevelOfTheFallenGod card) {
        super(card);
    }

    @Override
    public RevelOfTheFallenGod copy() {
        return new RevelOfTheFallenGod(this);
    }
}

class RevelOfTheFallenGodSatyrToken extends Token {

    public RevelOfTheFallenGodSatyrToken() {
        super("Satyr", "2/2 red and green Satyr creature tokens with haste");
        this.setOriginalExpansionSetCode("THS");
        cardType.add(CardType.CREATURE);
        color.setColor(ObjectColor.RED);
        color.setColor(ObjectColor.GREEN);
        subtype.add("Satyr");
        power = new MageInt(2);
        toughness = new MageInt(2);
        addAbility(HasteAbility.getInstance());
    }
}
