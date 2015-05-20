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
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.CostImpl;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.common.TapTargetCost;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.permanent.TappedPredicate;
import mage.game.Game;
import mage.game.permanent.token.Token;
import mage.target.common.TargetControlledCreaturePermanent;

/**
 *
 * @author North
 */
public class SkirsdagHighPriest extends CardImpl {

    private static final FilterControlledCreaturePermanent filter = new FilterControlledCreaturePermanent("untapped creatures you control");

    static {
        filter.add(Predicates.not(new TappedPredicate()));
    }

    public SkirsdagHighPriest(UUID ownerId) {
        super(ownerId, 117, "Skirsdag High Priest", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}{B}");
        this.expansionSetCode = "ISD";
        this.subtype.add("Human");
        this.subtype.add("Cleric");

        this.power = new MageInt(1);
        this.toughness = new MageInt(2);

        // Morbid - {tap}, Tap two untapped creatures you control: Put a 5/5 black Demon creature token with flying onto the battlefield. Activate this ability only if a creature died this turn.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new CreateTokenEffect(new DemonToken()), new TapSourceCost());
        ability.addCost(new TapTargetCost(new TargetControlledCreaturePermanent(2,2,filter, false)));
        ability.addCost(new SkirsdagHighPriestCost());
        this.addAbility(ability);
    }

    public SkirsdagHighPriest(final SkirsdagHighPriest card) {
        super(card);
    }

    @Override
    public SkirsdagHighPriest copy() {
        return new SkirsdagHighPriest(this);
    }
}

class SkirsdagHighPriestCost extends CostImpl {

    public SkirsdagHighPriestCost() {
        this.text = "Activate this ability only if a creature died this turn";
    }

    public SkirsdagHighPriestCost(final SkirsdagHighPriestCost cost) {
        super(cost);
    }

    @Override
    public SkirsdagHighPriestCost copy() {
        return new SkirsdagHighPriestCost(this);
    }

    @Override
    public boolean canPay(Ability ability, UUID sourceId, UUID controllerId, Game game) {
        return game.getState().getWatchers().get("Morbid").conditionMet();
    }

    @Override
    public boolean pay(Ability ability, Game game, UUID sourceId, UUID controllerId, boolean noMana) {
        this.paid = true;
        return paid;
    }
}

class DemonToken extends Token {

    DemonToken() {
        super("Demon", "5/5 black Demon creature token with flying");
        cardType.add(CardType.CREATURE);
        subtype.add("Demon");

        color.setBlack(true);
        power = new MageInt(5);
        toughness = new MageInt(5);

        addAbility(FlyingAbility.getInstance());
    }
}
