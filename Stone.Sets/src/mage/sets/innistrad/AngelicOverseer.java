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
package mage.sets.innistrad;

import java.util.UUID;
import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.MageInt;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.condition.common.ControlsPermanentCondition;
import mage.abilities.decorator.ConditionalContinousEffect;
import mage.abilities.effects.common.continious.GainAbilitySourceEffect;
import mage.abilities.keyword.*;
import mage.cards.CardImpl;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.predicate.mageobject.SubtypePredicate;

/**
 *
 * @author nantuko
 */
public class AngelicOverseer extends CardImpl<AngelicOverseer> {

    private static final String rule1 = "As long as you control a Human, {this} has hexproof.";
    private static final String rule2 = "As long as you control a Human, {this} is indestructible.";
    private static final FilterControlledPermanent filter = new FilterControlledPermanent("Human");

    static {
        filter.add(new SubtypePredicate("Human"));
    }

    public AngelicOverseer(UUID ownerId) {
        super(ownerId, 3, "Angelic Overseer", Rarity.MYTHIC, new CardType[]{CardType.CREATURE}, "{3}{W}{W}");
        this.expansionSetCode = "ISD";
        this.subtype.add("Angel");

        this.color.setWhite(true);
        this.power = new MageInt(5);
        this.toughness = new MageInt(3);

        this.addAbility(FlyingAbility.getInstance());

        // As long as you control a Human, Angelic Overseer has hexproof and is indestructible.
        ConditionalContinousEffect effect1 = new ConditionalContinousEffect(new GainAbilitySourceEffect(HexproofAbility.getInstance()), new ControlsPermanentCondition(filter), rule1);
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, effect1));
        ConditionalContinousEffect effect2 = new ConditionalContinousEffect(new GainAbilitySourceEffect(new IndestructibleAbility()), new ControlsPermanentCondition(filter), rule2);
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, effect2));

    }

    public AngelicOverseer(final AngelicOverseer card) {
        super(card);
    }

    @Override
    public AngelicOverseer copy() {
        return new AngelicOverseer(this);
    }
}
