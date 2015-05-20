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
package mage.sets.ravnica;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.common.SpellCastControllerTriggeredAbility;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.continuous.BecomesCreatureSourceEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Rarity;
import mage.filter.FilterSpell;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.game.permanent.token.Token;

/**
 *
 * @author Loki
 */
public class HalcyonGlaze extends CardImpl {

    private static final FilterSpell filter = new FilterSpell("a creature spell");

    static {
        filter.add(new CardTypePredicate(CardType.CREATURE));
    }

    public HalcyonGlaze(UUID ownerId) {
        super(ownerId, 54, "Halcyon Glaze", Rarity.UNCOMMON, new CardType[]{CardType.ENCHANTMENT}, "{1}{U}{U}");
        this.expansionSetCode = "RAV";


        // Whenever you cast a creature spell, Halcyon Glaze becomes a 4/4 Illusion creature with flying in addition to its other types until end of turn.
        Effect effect = new BecomesCreatureSourceEffect(new HalcyonGlazeToken(), "enchantment", Duration.EndOfTurn);
        effect.setText("Whenever you cast a creature spell, {this} becomes a 4/4 Illusion creature with flying in addition to its other types until end of turn");
        this.addAbility(new SpellCastControllerTriggeredAbility(effect, filter, false));
    }

    public HalcyonGlaze(final HalcyonGlaze card) {
        super(card);
    }

    @Override
    public HalcyonGlaze copy() {
        return new HalcyonGlaze(this);
    }
}

class HalcyonGlazeToken extends Token {
    HalcyonGlazeToken() {
        super("", "4/4 Illusion creature with flying");
        cardType.add(CardType.CREATURE);
        subtype.add("Illusion");
        power = new MageInt(4);
        toughness = new MageInt(4);
        this.addAbility(FlyingAbility.getInstance());
    }
}
