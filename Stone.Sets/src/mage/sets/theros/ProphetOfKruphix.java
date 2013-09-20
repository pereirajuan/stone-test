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
package mage.sets.theros;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.BeginningOfUntapTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.AsThoughEffectImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.UntapAllControllerEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.AsThoughEffectType;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.filter.FilterPermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.game.Game;

/**
 *
 * @author LevelX2
 */
public class ProphetOfKruphix extends CardImpl<ProphetOfKruphix> {

    private static final FilterPermanent filter = new FilterPermanent();
    static {
        filter.add(Predicates.or(
                new CardTypePredicate(CardType.CREATURE),
                new CardTypePredicate(CardType.LAND)));
    }

    public ProphetOfKruphix(UUID ownerId) {
        super(ownerId, 199, "Prophet of Kruphix", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{3}{G}{U}");
        this.expansionSetCode = "THS";
        this.subtype.add("Human");
        this.subtype.add("Wizard");

        this.color.setBlue(true);
        this.color.setGreen(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(3);

        // Untap all creatures and lands you control during each other player's untap step.
        Effect effect = new UntapAllControllerEffect(filter, "Untap all creatures and lands you control");
        this.addAbility(new BeginningOfUntapTriggeredAbility(Zone.BATTLEFIELD, effect, TargetController.NOT_YOU, false));
        // You may cast creature cards as though they had flash.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new ProphetOfKruphixEffect()));

    }

    public ProphetOfKruphix(final ProphetOfKruphix card) {
        super(card);
    }

    @Override
    public ProphetOfKruphix copy() {
        return new ProphetOfKruphix(this);
    }
}

class ProphetOfKruphixEffect extends AsThoughEffectImpl<ProphetOfKruphixEffect> {

    public ProphetOfKruphixEffect() {
        super(AsThoughEffectType.CAST, Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "You may cast creature cards as though they had flash";
    }

    public ProphetOfKruphixEffect(final ProphetOfKruphixEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public ProphetOfKruphixEffect copy() {
        return new ProphetOfKruphixEffect(this);
    }

    @Override
    public boolean applies(UUID sourceId, Ability source, Game game) {
        Card card = game.getCard(sourceId);
        if (card != null) {
            if (card.getCardType().contains(CardType.CREATURE)
                    && card.getOwnerId().equals(source.getControllerId())) {
                return card.getSpellAbility().isInUseableZone(game, card, false);
            }
        }
        return false;
    }
}
