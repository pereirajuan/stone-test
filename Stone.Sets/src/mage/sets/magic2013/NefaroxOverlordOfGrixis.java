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
package mage.sets.magic2013;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.common.SacrificeEffect;
import mage.abilities.keyword.ExaltedAbility;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author jeffwadsworth
 */
public class NefaroxOverlordOfGrixis extends CardImpl {

    public NefaroxOverlordOfGrixis(UUID ownerId) {
        super(ownerId, 103, "Nefarox, Overlord of Grixis", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{4}{B}{B}");
        this.expansionSetCode = "M13";
        this.supertype.add("Legendary");
        this.subtype.add("Demon");

        this.power = new MageInt(5);
        this.toughness = new MageInt(5);

        // Flying
        this.addAbility(FlyingAbility.getInstance());
        // Exalted
        this.addAbility(new ExaltedAbility());
        // Whenever Nefarox, Overlord of Grixis attacks alone, defending player sacrifices a creature.
        this.addAbility(new NefaroxOverlordOfGrixisTriggeredAbility());
    }

    public NefaroxOverlordOfGrixis(final NefaroxOverlordOfGrixis card) {
        super(card);
    }

    @Override
    public NefaroxOverlordOfGrixis copy() {
        return new NefaroxOverlordOfGrixis(this);
    }
}

class NefaroxOverlordOfGrixisTriggeredAbility extends TriggeredAbilityImpl {
    
    private static final FilterControlledPermanent filter;

    static {
        filter = new FilterControlledPermanent(" a creature");
        filter.add(new CardTypePredicate(CardType.CREATURE));
    }

    public NefaroxOverlordOfGrixisTriggeredAbility() {
        super(Zone.BATTLEFIELD, new SacrificeEffect(filter, 1, "defending player"));
    }

    public NefaroxOverlordOfGrixisTriggeredAbility(final NefaroxOverlordOfGrixisTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public NefaroxOverlordOfGrixisTriggeredAbility copy() {
        return new NefaroxOverlordOfGrixisTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == EventType.DECLARED_ATTACKERS;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (game.getActivePlayerId().equals(this.controllerId) ) {
            UUID nefarox = this.getSourceId();
            if (nefarox != null) {
                if (game.getCombat().attacksAlone() && nefarox == game.getCombat().getAttackers().get(0)) {
                    UUID defender = game.getCombat().getDefenderId(nefarox);
                    this.getEffects().get(0).setTargetPointer(new FixedTarget(defender));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever {this} attacks alone, defending player sacrifices a creature.";
    }

}
