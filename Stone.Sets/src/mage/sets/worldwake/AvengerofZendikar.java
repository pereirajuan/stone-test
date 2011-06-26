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

package mage.sets.worldwake;

import java.util.UUID;

import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.MageInt;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.LandfallAbility;
import mage.abilities.dynamicvalue.common.PermanentsOnBattlefieldCount;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.counter.AddCountersAllEffect;
import mage.cards.CardImpl;
import mage.counters.CounterType;
import mage.filter.common.FilterControlledPermanent;
import mage.game.permanent.token.PlantToken;

/**
 *
 * @author Loki, nantuko, North
 */
public class AvengerofZendikar extends CardImpl<AvengerofZendikar> {
    private static final FilterControlledPermanent filter = new FilterControlledPermanent("Plant creature you control");
    private static final FilterControlledPermanent filterLand = new FilterControlledPermanent("land you control");

    static {
        filter.getCardType().add(CardType.CREATURE);
        filter.getSubtype().add("Plant");

        filterLand.getCardType().add(CardType.LAND);
    }

    public AvengerofZendikar (UUID ownerId) {
        super(ownerId, 96, "Avenger of Zendikar", Rarity.MYTHIC, new CardType[]{CardType.CREATURE}, "{5}{G}{G}");
        this.expansionSetCode = "WWK";
        this.subtype.add("Elemental");

		this.color.setGreen(true);
        this.power = new MageInt(5);
        this.toughness = new MageInt(5);

        this.addAbility(new EntersBattlefieldTriggeredAbility(new CreateTokenEffect(new PlantToken(), new PermanentsOnBattlefieldCount(filterLand)), false));
        this.addAbility(new LandfallAbility(new AddCountersAllEffect(CounterType.P1P1.createInstance(), filter), true));
    }

    public AvengerofZendikar (final AvengerofZendikar card) {
        super(card);
    }

    @Override
    public AvengerofZendikar copy() {
        return new AvengerofZendikar(this);
    }
}
