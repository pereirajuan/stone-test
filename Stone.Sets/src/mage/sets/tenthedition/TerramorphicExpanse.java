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

package mage.sets.tenthedition;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.abilities.ActivatedAbilityImpl;
import mage.abilities.costs.common.SacrificeSourceCost;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.common.search.SearchLibraryPutInPlayEffect;
import mage.cards.CardImpl;
import mage.filter.common.FilterBasicLandCard;
import mage.target.common.TargetCardInLibrary;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class TerramorphicExpanse extends CardImpl {

    public TerramorphicExpanse(UUID ownerId) {
        super(ownerId, 360, "Terramorphic Expanse", Rarity.COMMON, new CardType[]{CardType.LAND}, null);
        this.expansionSetCode = "10E";
        this.addAbility(new TerramorphicExpanseAbility());
    }

    public TerramorphicExpanse(final TerramorphicExpanse card) {
        super(card);
    }

    @Override
    public TerramorphicExpanse copy() {
        return new TerramorphicExpanse(this);
    }

}

class TerramorphicExpanseAbility extends ActivatedAbilityImpl {

    public TerramorphicExpanseAbility() {
        super(Zone.BATTLEFIELD, null);
        addCost(new TapSourceCost());
        addCost(new SacrificeSourceCost());
        TargetCardInLibrary target = new TargetCardInLibrary(new FilterBasicLandCard());
        addEffect(new SearchLibraryPutInPlayEffect(target, true, Outcome.PutLandInPlay));
    }

    public TerramorphicExpanseAbility(final TerramorphicExpanseAbility ability) {
        super(ability);
    }

    @Override
    public TerramorphicExpanseAbility copy() {
        return new TerramorphicExpanseAbility(this);
    }

}
