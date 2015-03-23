 /*
* Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are
* permitted provided that the following conditions are met:
*
*    1. Redistributions of source code must retain the above copyright notice, this list of
*       conditions and the following disclaimer.
*
*    2. Redistributions in binary form must reproduce the above copyright notice, this list
*       of conditions and the following disclaimer in the documentation and/or other materials
*       provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
* FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation are those of the
* authors and should not be interpreted as representing official policies, either expressed
* or implied, of BetaSteward_at_googlemail.com.
*/

package mage.abilities.keyword;

import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.ZoneChangeTriggeredAbility;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.Permanent;
import mage.target.common.TargetCreaturePermanent;
import mage.target.targetpointer.FixedTarget;

/**
 * 702.53. Haunt
 *
 * 702.53a Haunt is a triggered ability. "Haunt" on a permanent means "When this permanent is put
 * into a graveyard from the battlefield, exile it haunting target creature."
 * "Haunt" on an instant or sorcery spell means "When this spell is put into a graveyard during
 * its resolution, exile it haunting target creature."
 *
 * 702.53b Cards that are in the exile zone as the result of a haunt ability "haunt" the creature
 * targeted by that ability. The phrase "creature it haunts" refers to the object targeted by the
 * haunt ability, regardless of whether or not that object is still a creature.
 *
 * 702.53c Triggered abilities of cards with haunt that refer to the haunted creature can trigger in the exile zone.
 *
 * @author LevelX2
 */

public class HauntAbility extends TriggeredAbilityImpl {

    private boolean usedFromExile = false;

    public HauntAbility(Card card, Effect effect) {
        super(Zone.ALL, effect , false);
        addSubAbility(new HauntExileAbility());
    }

    public HauntAbility(final HauntAbility ability) {
        super(ability);
        this.usedFromExile = ability.usedFromExile;
    }

    @Override
    public HauntAbility copy() {
        return new HauntAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        switch (event.getType()) {
            case ENTERS_THE_BATTLEFIELD:
                if (game.getState().getZone(getSourceId()).equals(Zone.BATTLEFIELD)) {
                    return event.getTargetId().equals(getSourceId());
                }
                break;
            case ZONE_CHANGE:
                if (!usedFromExile &&game.getState().getZone(getSourceId()).equals(Zone.EXILED)) {
                    ZoneChangeEvent zEvent = (ZoneChangeEvent) event;
                    if (zEvent.isDiesEvent()) {
                        Card card = game.getCard(getSourceId());
                        if (card != null) {
                            String key = new StringBuilder("Haunting_").append(getSourceId().toString()).append("_").append(card.getZoneChangeCounter()).toString();
                            Object object = game.getState().getValue(key);
                            if (object != null && object instanceof FixedTarget) {
                                FixedTarget target = (FixedTarget) object;
                                if (target.getTarget() != null &&  target.getTarget().equals(event.getTargetId())) {
                                    usedFromExile = true;
                                    return true;
                                }
                            }
                        }
                    }
                }
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public String getRule() {
        return "When {this} enters the battlefield or the creature it haunts dies, " + super.getRule();
    }
}

class HauntExileAbility extends ZoneChangeTriggeredAbility {

    private final static String RULE_TEXT_CREATURE = "Haunt <i>(When this creature dies, exile it haunting target creature.)</i>";
    public HauntExileAbility() {
        super(Zone.BATTLEFIELD, Zone.GRAVEYARD, new HauntEffect(), null, false);
        this.setRuleAtTheTop(true);
        this.addTarget(new TargetCreaturePermanent());

    }

    public HauntExileAbility(HauntExileAbility ability) {
        super(ability);
    }

    @Override
    public boolean isInUseableZone(Game game, MageObject source, boolean checkLKI) {
        // check it was previously on battlefield
        MageObject before = game.getLastKnownInformation(sourceId, Zone.BATTLEFIELD);
        // check now it is in graveyard
        Zone after = game.getState().getZone(sourceId);
        return before != null && after != null && Zone.GRAVEYARD.match(after);
    }

    @Override
    public String getRule() {
        return RULE_TEXT_CREATURE;
    }

    @Override
    public HauntExileAbility copy() {
        return new HauntExileAbility(this);
    }
}

class HauntEffect extends OneShotEffect {

    public HauntEffect() {
        super(Outcome.Benefit);
        this.staticText = "exile it haunting target creature";
    }

    public HauntEffect(final HauntEffect effect) {
        super(effect);
    }

    @Override
    public HauntEffect copy() {
        return new HauntEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card card = game.getCard(source.getSourceId());
        if (card != null) {
            Permanent hauntedCreature = game.getPermanent(targetPointer.getFirst(game, source));
            // haunting card will only be moved to exile, if 
            if (hauntedCreature != null) {
                if (card.moveToExile(source.getSourceId(), "Haunting", source.getSourceId(), game)) {
                    // remember the haunted creature
                    String key = new StringBuilder("Haunting_").append(source.getSourceId().toString()).append("_").append(card.getZoneChangeCounter()).toString();
                    game.getState().setValue(key, new FixedTarget(targetPointer.getFirst(game, source)));
                    card.addInfo("hauntinfo", new StringBuilder("Haunting ").append(hauntedCreature.getLogName()).toString(), game);
                    hauntedCreature.addInfo("hauntinfo", new StringBuilder("Haunted by ").append(card.getLogName()).toString(), game);
                    game.informPlayers(new StringBuilder(card.getName()).append(" haunting ").append(hauntedCreature.getLogName()).toString());
                }
                return true;
            }
        }
        return false;
    }
}
