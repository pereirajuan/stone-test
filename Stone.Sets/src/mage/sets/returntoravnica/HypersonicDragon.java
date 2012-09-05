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
package mage.sets.returntoravnica;

import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.AsThoughEffectImpl;
import mage.abilities.keyword.FlyingAbility;
import mage.abilities.keyword.HasteAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.game.Game;

import java.util.UUID;

/**
 * @author magenoxx_at_gmail.com
 */
public class HypersonicDragon extends CardImpl<HypersonicDragon> {

    public HypersonicDragon(UUID ownerId) {
        super(ownerId, 170, "Hypersonic Dragon", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{3}{U}{R}");
        this.expansionSetCode = "RTR";
        this.subtype.add("Dragon");

        this.color.setRed(true);
        this.color.setBlue(true);
        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // Flying
        this.addAbility(FlyingAbility.getInstance());
        // Haste
        this.addAbility(HasteAbility.getInstance());
        // You may cast sorcery spells as though they had flash.
        this.addAbility(new SimpleStaticAbility(Constants.Zone.BATTLEFIELD, new HypersonicDragonEffect()));
    }

    public HypersonicDragon(final HypersonicDragon card) {
        super(card);
    }

    @Override
    public HypersonicDragon copy() {
        return new HypersonicDragon(this);
    }
}

class HypersonicDragonEffect extends AsThoughEffectImpl<HypersonicDragonEffect> {

    public HypersonicDragonEffect() {
        super(Constants.AsThoughEffectType.CAST, Constants.Duration.WhileOnBattlefield, Constants.Outcome.Benefit);
        staticText = "You may cast sorcery spells as though they had flash";
    }

    public HypersonicDragonEffect(final HypersonicDragonEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public HypersonicDragonEffect copy() {
        return new HypersonicDragonEffect(this);
    }

    @Override
    public boolean applies(UUID sourceId, Ability source, Game game) {
        Card card = game.getCard(sourceId);
        if (card != null) {
            if (card.getCardType().contains(CardType.SORCERY) && card.getOwnerId().equals(source.getControllerId())) {
                return true;
            }
        }
        return false;
    }
}
