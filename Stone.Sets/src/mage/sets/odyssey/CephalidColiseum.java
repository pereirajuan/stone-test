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
package mage.sets.odyssey;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.condition.common.CardsInControllerGraveCondition;
import mage.abilities.costs.common.SacrificeSourceCost;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.decorator.ConditionalGainActivatedAbility;
import mage.abilities.effects.common.DamageControllerEffect;
import mage.abilities.effects.common.DrawCardTargetEffect;
import mage.abilities.effects.common.discard.DiscardTargetEffect;
import mage.abilities.mana.BlueManaAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.target.TargetPlayer;

/**
 *
 * @author LevelX2
 */
public class CephalidColiseum extends CardImpl {

    public CephalidColiseum(UUID ownerId) {
        super(ownerId, 317, "Cephalid Coliseum", Rarity.UNCOMMON, new CardType[]{CardType.LAND}, "");
        this.expansionSetCode = "ODY";

        // {tap}: Add {U} to your mana pool. Cephalid Coliseum deals 1 damage to you.
        Ability manaAbility = new BlueManaAbility();
        manaAbility.addEffect(new DamageControllerEffect(1));
        this.addAbility(manaAbility);

        // Threshold - {U}, {tap}, Sacrifice Cephalid Coliseum: Target player draws three cards, then discards three cards. Activate this ability only if seven or more cards are in your graveyard.
        Ability thresholdAbility = new ConditionalGainActivatedAbility(Zone.BATTLEFIELD,
            new DrawCardTargetEffect(3),
            new ManaCostsImpl("{U}"),
            new CardsInControllerGraveCondition(7),
            "<i>Threshold</i> - {U}, {T}, Sacrifice {this}: Target player draws three cards, then discards three cards. Activate this ability only if seven or more cards are in your graveyard.");
        thresholdAbility.addEffect(new DiscardTargetEffect(3));
        thresholdAbility.addCost(new TapSourceCost());
        thresholdAbility.addCost(new SacrificeSourceCost());
        thresholdAbility.addTarget(new TargetPlayer());
        this.addAbility(thresholdAbility);
    }

    public CephalidColiseum(final CephalidColiseum card) {
        super(card);
    }

    @Override
    public CephalidColiseum copy() {
        return new CephalidColiseum(this);
    }
}
