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
package mage.sets.innistrad;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.common.TransformSourceEffect;
import mage.abilities.keyword.TransformAbility;
import mage.abilities.keyword.VigilanceAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.Permanent;

/**
 * @author nantuko
 */
public class ThrabenSentry extends CardImpl {

    public ThrabenSentry(UUID ownerId) {
        super(ownerId, 38, "Thraben Sentry", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{3}{W}");
        this.expansionSetCode = "ISD";
        this.subtype.add("Human");
        this.subtype.add("Soldier");

        this.canTransform = true;
        this.secondSideCard = new ThrabenMilitia(ownerId);

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        this.addAbility(VigilanceAbility.getInstance());

        // Whenever another creature you control dies, you may transform Thraben Sentry.
        this.addAbility(new TransformAbility());
        this.addAbility(new ThrabenSentryTriggeredAbility());
    }

    public ThrabenSentry(final ThrabenSentry card) {
        super(card);
    }

    @Override
    public ThrabenSentry copy() {
        return new ThrabenSentry(this);
    }
}

class ThrabenSentryTriggeredAbility extends TriggeredAbilityImpl {

    public ThrabenSentryTriggeredAbility() {
        super(Zone.BATTLEFIELD, new TransformSourceEffect(true), true);
    }

    public ThrabenSentryTriggeredAbility(ThrabenSentryTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public ThrabenSentryTriggeredAbility copy() {
        return new ThrabenSentryTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == EventType.ZONE_CHANGE;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        Permanent source = game.getPermanent(this.sourceId);
        if (source == null) {
            return false;
        }
        ZoneChangeEvent zEvent = (ZoneChangeEvent) event;
        Permanent permanent = zEvent.getTarget();
        return permanent != null && permanent.getCardType().contains(CardType.CREATURE) &&
                zEvent.getToZone() == Zone.GRAVEYARD &&
                zEvent.getFromZone() == Zone.BATTLEFIELD &&
                permanent.getControllerId().equals(this.getControllerId()) &&
                !source.isTransformed();
    }

    @Override
    public String getRule() {
        return "Whenever another creature you control dies, you may transform Thraben Sentry";
    }
}
