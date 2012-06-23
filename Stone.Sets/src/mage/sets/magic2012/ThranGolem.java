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
package mage.sets.magic2012;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Duration;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.condition.common.EnchantedCondition;
import mage.abilities.decorator.ConditionalContinousEffect;
import mage.abilities.effects.common.continious.BoostSourceEffect;
import mage.abilities.effects.common.continious.GainAbilitySourceEffect;
import mage.abilities.keyword.FirstStrikeAbility;
import mage.abilities.keyword.FlyingAbility;
import mage.abilities.keyword.TrampleAbility;
import mage.cards.CardImpl;

/**
 *
 * @author North
 */
public class ThranGolem extends CardImpl<ThranGolem> {

    private static final String rule1 = "As long as {this} is enchanted, it gets +2/+2 ";
    private static final String rule2 = "As long as {this} is enchanted, it has flying";
    private static final String rule3 = "As long as {this} is enchanted, it has first strike";
    private static final String rule4 = "As long as {this} is enchanted, it has flying trample";

    public ThranGolem(UUID ownerId) {
        super(ownerId, 220, "Thran Golem", Rarity.UNCOMMON, new CardType[]{CardType.ARTIFACT, CardType.CREATURE}, "{5}");
        this.expansionSetCode = "M12";
        this.subtype.add("Golem");

        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        ConditionalContinousEffect effect1 = new ConditionalContinousEffect(new BoostSourceEffect(2, 2, Duration.WhileOnBattlefield), EnchantedCondition.getInstance(), rule1);
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, effect1));
        ConditionalContinousEffect effect2 = new ConditionalContinousEffect(new GainAbilitySourceEffect(FlyingAbility.getInstance()), EnchantedCondition.getInstance(), rule2);
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, effect2));
        ConditionalContinousEffect effect3 = new ConditionalContinousEffect(new GainAbilitySourceEffect(FirstStrikeAbility.getInstance()), EnchantedCondition.getInstance(), rule3);
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, effect3));
        ConditionalContinousEffect effect4 = new ConditionalContinousEffect(new GainAbilitySourceEffect(TrampleAbility.getInstance()), EnchantedCondition.getInstance(), rule4);
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, effect4));
    }

    public ThranGolem(final ThranGolem card) {
        super(card);
    }

    @Override
    public ThranGolem copy() {
        return new ThranGolem(this);
    }
}
