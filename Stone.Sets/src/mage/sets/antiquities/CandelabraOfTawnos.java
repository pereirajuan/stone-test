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
package mage.sets.antiquities;

import java.util.UUID;
import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.abilities.Ability;
import mage.abilities.ActivatedAbilityImpl;
import mage.abilities.SpellAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.AdjustingSourceCosts;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.costs.mana.VariableManaCost;
import mage.abilities.effects.common.TapTargetEffect;
import mage.abilities.effects.common.UntapTargetEffect;
import mage.cards.CardImpl;
import mage.filter.common.FilterLandPermanent;
import mage.game.Game;
import mage.target.common.TargetLandPermanent;
import mage.util.CardUtil;

/**
 *
 * @author Plopman
 */
public class CandelabraOfTawnos extends CardImpl<CandelabraOfTawnos> {

    public CandelabraOfTawnos(UUID ownerId) {
        super(ownerId, 8, "Candelabra of Tawnos", Rarity.RARE, new CardType[]{CardType.ARTIFACT}, "{1}");
        this.expansionSetCode = "ATQ";

        // {X}, {tap}: Untap X target lands.
        this.addAbility(new CandelabraOfTawnosAbility());
    }

    public CandelabraOfTawnos(final CandelabraOfTawnos card) {
        super(card);
    }

    @Override
    public CandelabraOfTawnos copy() {
        return new CandelabraOfTawnos(this);
    }
}




class CandelabraOfTawnosAbility extends ActivatedAbilityImpl<CandelabraOfTawnosAbility> implements AdjustingSourceCosts{
    public CandelabraOfTawnosAbility(){
        super(Constants.Zone.BATTLEFIELD, new UntapTargetEffect(), new TapSourceCost());
        addTarget(new TargetLandPermanent(0, Integer.MAX_VALUE, new FilterLandPermanent(), false));
    }

     public CandelabraOfTawnosAbility(CandelabraOfTawnosAbility ability) {
        super(ability);
    }

    @Override
    public CandelabraOfTawnosAbility copy() {
        return new CandelabraOfTawnosAbility(this);
    }
    
    @Override
    public void adjustCosts(Ability ability, Game game) {
        if(ability instanceof CandelabraOfTawnosAbility){
            int numTargets = ability.getTargets().get(0).getTargets().size();
            if (numTargets > 0) {
                ability.getManaCostsToPay().add(new GenericManaCost(numTargets));
            }
        }
    }

    @Override
    public String getRule() {
        return "{X}, {T}: Untap X target lands";
    }
    
    @Override
    public String getRule(boolean all) {
        return "{X}, {T}: Untap X target lands";
    }

    @Override
    public String getRule(String source) {
        return "{X}, {T}: Untap X target lands";
    }



    
    
    
}