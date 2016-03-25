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
package mage.sets.shadowsoverinnistrad;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.condition.common.DeliriumCondition;
import mage.abilities.decorator.ConditionalContinuousEffect;
import mage.abilities.effects.common.continuous.GainAbilitySourceEffect;
import mage.abilities.keyword.DoubleStrikeAbility;
import mage.abilities.keyword.FirstStrikeAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Rarity;
import mage.constants.Zone;

/**
 *
 * @author fireshoes
 */
public class ScourgeWolf extends CardImpl {

    public ScourgeWolf(UUID ownerId) {
        super(ownerId, 179, "Scourge Wolf", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{R}{R}");
        this.expansionSetCode = "SOI";
        this.subtype.add("Wolf");
        this.subtype.add("Horror");
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // First strike
        this.addAbility(FirstStrikeAbility.getInstance());

        // <i>Delirium</i> &mdash; Scourge Wolf has double strike as long as there are four or more card types among cards in your graveyard.
        ConditionalContinuousEffect effect = new ConditionalContinuousEffect(new GainAbilitySourceEffect(DoubleStrikeAbility.getInstance(), Duration.WhileOnBattlefield),
                DeliriumCondition.getInstance(), "<i>Delirium</i> &mdash; {this} has double strike as long as there are four or more card types among cards in your graveyard.");
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, effect));
    }

    public ScourgeWolf(final ScourgeWolf card) {
        super(card);
    }

    @Override
    public ScourgeWolf copy() {
        return new ScourgeWolf(this);
    }
}
