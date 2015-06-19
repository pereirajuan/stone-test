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

package mage.sets.dragonsmaze;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.common.counter.AddCountersAllEffect;
import mage.abilities.keyword.EvolveAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.predicate.permanent.AnotherPredicate;
import mage.filter.predicate.permanent.CounterPredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.stack.StackObject;



public class RenegadeKrasis extends CardImpl {

    public RenegadeKrasis (UUID ownerId) {
        super(ownerId, 47, "Renegade Krasis", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}{G}{G}");
        this.expansionSetCode = "DGM";
        this.subtype.add("Beast");
        this.subtype.add("Mutant");

        this.power = new MageInt(3);
        this.toughness = new MageInt(2);

        // Evolve
        this.addAbility(new EvolveAbility());

        // Whenever Renegade Krasis evolves, put a +1/+1 counter on each other creature you control with a +1/+1 counter on it.
        this.addAbility(new RenegadeKrasisTriggeredAbility());
    }

    public RenegadeKrasis (final RenegadeKrasis card) {
        super(card);
    }

    @Override
    public RenegadeKrasis copy() {
        return new RenegadeKrasis(this);
    }

}

class RenegadeKrasisTriggeredAbility extends TriggeredAbilityImpl {

    private static final FilterControlledCreaturePermanent filter = new FilterControlledCreaturePermanent();
    static {
        filter.add(new AnotherPredicate());
        filter.add(new CounterPredicate(CounterType.P1P1));
    }

    public RenegadeKrasisTriggeredAbility() {
        super(Zone.BATTLEFIELD, new AddCountersAllEffect(CounterType.P1P1.createInstance(1),filter), false);
    }

    public RenegadeKrasisTriggeredAbility(final RenegadeKrasisTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public RenegadeKrasisTriggeredAbility copy() {
        return new RenegadeKrasisTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == EventType.COUNTER_ADDED;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getTargetId().equals(getSourceId())) {
            Object object = game.getState().getValue("EvolveAddCounterActive");
            if (object != null && (Boolean) object) {
                StackObject stackObject = game.getStack().getLast();
                if (stackObject.getStackAbility() instanceof EvolveAbility) {
                    object = game.getState().getValue(this.getId() + "_lastUsedEvolveStackObject");
                    if (object != null && ((UUID) object).equals(stackObject.getId())) {
                        // this evolve was already handled before (prevents to trigger multiple times if counter from evolve is e.g. doubled)
                        return false;
                    }
                    game.getState().setValue(this.getId() + "_lastUsedEvolveStackObject", stackObject.getId());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever Renegade Krasis evolves, put a +1/+1 counter on each other creature you control with a +1/+1 counter on it.";
    }
}
