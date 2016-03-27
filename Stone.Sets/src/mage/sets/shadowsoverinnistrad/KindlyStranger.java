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
package mage.sets.shadowsoverinnistrad;

import java.util.UUID;
import mage.MageInt;
import mage.MageObject;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.abilities.keyword.TransformAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.target.Target;
import mage.target.TargetPermanent;

/**
 *
 * @author fireshoes
 */
public class KindlyStranger extends CardImpl {

    public KindlyStranger(UUID ownerId) {
        super(ownerId, 119, "Kindly Stranger", Rarity.UNCOMMON, new CardType[]{CardType.CREATURE}, "{2}{B}");
        this.expansionSetCode = "SOI";
        this.subtype.add("Human");
        this.power = new MageInt(2);
        this.toughness = new MageInt(3);

        this.canTransform = true;
        this.secondSideCard = new DemonPossessedWitch(ownerId);

        // <i>Delirium</i> &mdash; {2}{B}: Transform Kindly Stranger. Activate this ability only if there are four or more card types among cards in your graveyard.
        this.addAbility(new TransformAbility());

        // When this creature transforms into Demon-Possessed Witch, you may destroy target creature.
        this.addAbility(new DemonPossessedWitchAbility());
    }

    public KindlyStranger(final KindlyStranger card) {
        super(card);
    }

    @Override
    public KindlyStranger copy() {
        return new KindlyStranger(this);
    }
}

class DemonPossessedWitchAbility extends TriggeredAbilityImpl {

    public DemonPossessedWitchAbility() {
        super(Zone.BATTLEFIELD, new DestroyTargetEffect(), true);
        Target target = new TargetPermanent(new FilterCreaturePermanent());
        this.addTarget(target);
        // Rule only shown on the night side
        this.setRuleVisible(false);
    }

    public DemonPossessedWitchAbility(final DemonPossessedWitchAbility ability) {
        super(ability);
    }

    @Override
    public DemonPossessedWitchAbility copy() {
        return new DemonPossessedWitchAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.TRANSFORMED;
    }

    @Override
    public boolean isInUseableZone(Game game, MageObject source, GameEvent event) {
        Permanent currentSourceObject = (Permanent) getSourceObjectIfItStillExists(game);
        if (currentSourceObject != null && currentSourceObject.isNightCard()) {
            return true;
        }
        return super.isInUseableZone(game, source, event);
    }


    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getTargetId().equals(sourceId)) {
            Permanent permanent = game.getPermanent(sourceId);
            if (permanent != null && permanent.isTransformed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "When this creature transforms into Demon-Possessed Witch, you may destroy target creature.";
    }
}
