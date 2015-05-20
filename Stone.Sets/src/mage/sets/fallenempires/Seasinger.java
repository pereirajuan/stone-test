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
package mage.sets.fallenempires;

import java.util.UUID;

import mage.constants.*;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.StateTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SkipUntapOptionalAbility;
import mage.abilities.condition.common.PermanentsOnTheBattlefieldCondition;
import mage.abilities.condition.common.SourceTappedCondition;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.decorator.ConditionalContinuousEffect;
import mage.abilities.effects.common.SacrificeSourceEffect;
import mage.abilities.effects.common.continuous.GainControlTargetEffect;
import mage.cards.CardImpl;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.mageobject.CardIdPredicate;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.filter.predicate.permanent.ControllerPredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.target.common.TargetCreaturePermanent;
import mage.filter.predicate.permanent.ControllerControlsIslandPredicate;

/**
 *
 * @author jeffwadsworth
 */
public class Seasinger extends CardImpl {

    private static final String rule = "Gain control of target creature whose controller controls an Island for as long as you control Seasinger and Seasinger remains tapped";
    private static final FilterPermanent islandYouControl = new FilterPermanent("Island");
    private static final FilterCreaturePermanent creatureWhoseControllerControlsIsland = new FilterCreaturePermanent("creature whose controller controls an island");

    static {
        islandYouControl.add(new SubtypePredicate("Island"));
        islandYouControl.add(new ControllerPredicate(TargetController.YOU));
    }

    public Seasinger(UUID ownerId) {
        super(ownerId, 52, "Seasinger", Rarity.UNCOMMON, new CardType[]{CardType.CREATURE}, "{1}{U}{U}");
        this.expansionSetCode = "FEM";
        this.subtype.add("Merfolk");

        this.power = new MageInt(0);
        this.toughness = new MageInt(1);

        FilterPermanent seasinger = new FilterPermanent();
        seasinger.add(new ControllerPredicate(TargetController.YOU));
        seasinger.add(new CardIdPredicate(this.getId()));

        // When you control no Islands, sacrifice Seasinger.
        this.addAbility(new SeasingerTriggeredAbility());

        // You may choose not to untap Seasinger during your untap step.
        this.addAbility(new SkipUntapOptionalAbility());

        // {tap}: Gain control of target creature whose controller controls an Island for as long as you control Seasinger and Seasinger remains tapped.
        ConditionalContinuousEffect effect = new ConditionalContinuousEffect(
                new GainControlTargetEffect(Duration.Custom),
                new PermanentsOnTheBattlefieldCondition(seasinger, PermanentsOnTheBattlefieldCondition.CountType.EQUAL_TO, 1, SourceTappedCondition.getInstance()), rule);
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, effect, new TapSourceCost());
        creatureWhoseControllerControlsIsland.add(new ControllerControlsIslandPredicate());
        ability.addTarget(new TargetCreaturePermanent(creatureWhoseControllerControlsIsland));
        this.addAbility(ability);
    }

    public Seasinger(final Seasinger card) {
        super(card);
    }

    @Override
    public Seasinger copy() {
        return new Seasinger(this);
    }
}

class SeasingerTriggeredAbility extends StateTriggeredAbility {

    public SeasingerTriggeredAbility() {
        super(Zone.BATTLEFIELD, new SacrificeSourceEffect());
    }

    public SeasingerTriggeredAbility(final SeasingerTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public SeasingerTriggeredAbility copy() {
        return new SeasingerTriggeredAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        return (game.getBattlefield().countAll(ControllerControlsIslandPredicate.filter, controllerId, game) == 0);
    }

    @Override
    public String getRule() {
        return "When you control no islands, sacrifice {this}.";
    }
}
