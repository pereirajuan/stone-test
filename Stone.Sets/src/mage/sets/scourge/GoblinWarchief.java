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
package mage.sets.scourge;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Duration;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.CostModificationEffectImpl;
import mage.abilities.effects.common.continious.GainAbilityControlledEffect;
import mage.abilities.keyword.HasteAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.game.Game;
import mage.util.CardUtil;

/**
 *
 * @author jonubuu
 */
public class GoblinWarchief extends CardImpl<GoblinWarchief> {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("Goblin creatures");

    static {
        filter.add(new SubtypePredicate("Goblin"));
    }

    public GoblinWarchief(UUID ownerId) {
        super(ownerId, 97, "Goblin Warchief", Rarity.UNCOMMON, new CardType[]{CardType.CREATURE}, "{1}{R}{R}");
        this.expansionSetCode = "SCG";
        this.subtype.add("Goblin");

        this.color.setRed(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Goblin spells you cast cost {1} less to cast.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new GoblinWarchiefEffect()));
        // Goblin creatures you control have haste.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new GainAbilityControlledEffect(HasteAbility.getInstance(), Duration.WhileOnBattlefield, filter, true)));
    }

    public GoblinWarchief(final GoblinWarchief card) {
        super(card);
    }

    @Override
    public GoblinWarchief copy() {
        return new GoblinWarchief(this);
    }
}

class GoblinWarchiefEffect extends CostModificationEffectImpl<GoblinWarchiefEffect> {

    GoblinWarchiefEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "Goblin spells you cast cost {1} less to cast";
    }

    GoblinWarchiefEffect(GoblinWarchiefEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source, Ability abilityToModify) {
        SpellAbility spellAbility = (SpellAbility) abilityToModify;
        CardUtil.adjustCost(spellAbility, 1);
        return true;
    }

    @Override
    public boolean applies(Ability abilityToModify, Ability source, Game game) {
        if (abilityToModify instanceof SpellAbility) {
            Card sourceCard = game.getCard(((SpellAbility) abilityToModify).getSourceId());
            if (sourceCard != null && sourceCard.getSubtype().contains("Goblin") && sourceCard.getOwnerId().equals(source.getControllerId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public GoblinWarchiefEffect copy() {
        return new GoblinWarchiefEffect(this);
    }
}
