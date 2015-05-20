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
package mage.sets.shadowmoor;

import java.util.UUID;

import mage.constants.CardType;
import mage.constants.Rarity;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.DiscardSourceCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.game.Game;
import mage.target.common.TargetCardInGraveyard;

/**
 *
 * @author Plopman
 */
public class FaerieMacabre extends CardImpl {

    public FaerieMacabre(UUID ownerId) {
        super(ownerId, 66, "Faerie Macabre", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{1}{B}{B}");
        this.expansionSetCode = "SHM";
        this.subtype.add("Faerie");
        this.subtype.add("Rogue");

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Flying
        this.addAbility(FlyingAbility.getInstance());
        // Discard Faerie Macabre: Exile up to two target cards from graveyards.
        Ability ability = new SimpleActivatedAbility(Zone.HAND, new FaerieMacabreExileTargetEffect(), new DiscardSourceCost());
        ability.addTarget(new TargetCardInGraveyard(0, 2, new FilterCard("cards from graveyards")));
        this.addAbility(ability);
    }

    public FaerieMacabre(final FaerieMacabre card) {
        super(card);
    }

    @Override
    public FaerieMacabre copy() {
        return new FaerieMacabre(this);
    }
}

class FaerieMacabreExileTargetEffect extends OneShotEffect {

    public FaerieMacabreExileTargetEffect() {
        super(Outcome.Exile);
    }

    public FaerieMacabreExileTargetEffect(final FaerieMacabreExileTargetEffect effect) {
        super(effect);
    }

    @Override
    public FaerieMacabreExileTargetEffect copy() {
        return new FaerieMacabreExileTargetEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        for(UUID uuid : source.getTargets().get(0).getTargets()){
            Card card = game.getCard(uuid);
            if (card != null) {
                card.moveToExile(null, "Faerie Macabre", source.getSourceId(), game);
            }
        }
        return true;
    }

    @Override
    public String getText(Mode mode) {
        return "Exile up to two target cards from graveyards";
    }
}
