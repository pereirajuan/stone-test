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
package mage.sets.mirage;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.delayed.AtTheBeginOfNextEndStepDelayedTriggeredAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.keyword.HasteAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.common.FilterPermanentCard;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCardInLibrary;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author fireshoes
 */
public class ZirilanOfTheClaw extends CardImpl {
    
    public ZirilanOfTheClaw(UUID ownerId) {
        super(ownerId, 204, "Zirilan of the Claw", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{3}{R}{R}");
        this.expansionSetCode = "MIR";
        this.supertype.add("Legendary");
        this.subtype.add("Viashino");
        this.subtype.add("Shaman");
        this.power = new MageInt(3);
        this.toughness = new MageInt(4);

        // {1}{R}{R}, {tap}: Search your library for a Dragon permanent card and put that card onto the battlefield. Then shuffle your library. 
        // That Dragon gains haste until end of turn. Exile it at the beginning of the next end step.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new ZirilanOfTheClawEffect(), new ManaCostsImpl("{1}{R}{R}"));
        ability.addCost(new TapSourceCost());
        this.addAbility(ability);
    }

    public ZirilanOfTheClaw(final ZirilanOfTheClaw card) {
        super(card);
    }

    @Override
    public ZirilanOfTheClaw copy() {
        return new ZirilanOfTheClaw(this);
    }
}

class ZirilanOfTheClawEffect extends OneShotEffect {

    public ZirilanOfTheClawEffect() {
        super(Outcome.PutCreatureInPlay);
        this.staticText = "Search your library for a Dragon permanent card and put that card onto the battlefield. Then shuffle your library."
                + "That Dragon gains haste until end of turn. Exile it at the beginning of the next end step";
    }

    public ZirilanOfTheClawEffect(final ZirilanOfTheClawEffect effect) {
        super(effect);
    }

    @Override
    public ZirilanOfTheClawEffect copy() {
        return new ZirilanOfTheClawEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            FilterPermanentCard filter = new FilterPermanentCard("a Dragon permanent card");
            filter.add(new SubtypePredicate("Dragon"));
            TargetCardInLibrary target = new TargetCardInLibrary(filter);
            if (controller.searchLibrary(target, game)) {
                Card card = controller.getLibrary().getCard(target.getFirstTarget(), game);
                if (card != null) {
                    card.putOntoBattlefield(game, Zone.LIBRARY, source.getSourceId(), source.getControllerId());
                    Permanent permanent = game.getPermanent(card.getId());
                    if (permanent != null) {
                        // gains haste
                        ContinuousEffect effect = new GainAbilityTargetEffect(HasteAbility.getInstance(), Duration.EndOfTurn);
                        effect.setTargetPointer(new FixedTarget(permanent, game));
                        game.addEffect(effect, source);
                        // Exile at begin of next end step
                        ExileTargetEffect exileEffect = new ExileTargetEffect(null, null, Zone.BATTLEFIELD);
                        exileEffect.setTargetPointer(new FixedTarget(permanent, game));
                        DelayedTriggeredAbility delayedAbility = new AtTheBeginOfNextEndStepDelayedTriggeredAbility(exileEffect);
                        delayedAbility.setSourceId(source.getSourceId());
                        delayedAbility.setControllerId(source.getControllerId());
                        delayedAbility.setSourceObject(source.getSourceObject(game), game);
                        game.addDelayedTriggeredAbility(delayedAbility);
                    }
                }
                return true;
            }
        }
        return false;
    }
}
