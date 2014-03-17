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

package mage.abilities;

import java.util.UUID;
import mage.MageObject;
import mage.abilities.effects.Effect;
import mage.constants.AbilityType;
import mage.constants.Zone;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author BetaSteward_at_googlemail.com
 * @param <T>
 */
public abstract class TriggeredAbilityImpl<T extends TriggeredAbilityImpl<T>> extends AbilityImpl<T> implements TriggeredAbility {

    protected boolean optional;

    public TriggeredAbilityImpl(Zone zone, Effect effect) {
        this(zone, effect, false);
    }

    public TriggeredAbilityImpl(Zone zone, Effect effect, boolean optional) {
        super(AbilityType.TRIGGERED, zone);
        if (effect != null) {
            addEffect(effect);
        }
        this.optional = optional;
    }

    public TriggeredAbilityImpl(final TriggeredAbilityImpl<T> ability) {
        super(ability);
        this.optional = ability.optional;
    }

    @Override
    public void trigger(Game game, UUID controllerId) {
        //20091005 - 603.4
        if (checkInterveningIfClause(game)) {
            this.controllerId = controllerId;
            game.addTriggeredAbility(this);
        }
    }

    @Override
    public boolean checkInterveningIfClause(Game game) {
        return true;
    }

    @Override
    public boolean resolve(Game game) {
        MageObject object = game.getObject(sourceId);
        if (optional) {
            Player player = game.getPlayer(this.getControllerId());
            StringBuilder sb = new StringBuilder();
            if (object != null) {
                sb.append("Use the following ability from ").append(object.getName()).append("? ");
                sb.append(this.getRule(object.getName()));
            }
            else {
                sb.append("Use the following ability? ").append(this.getRule());
            }
            if (!player.chooseUse(getEffects().get(0).getOutcome(), sb.toString(), game)) {
                return false;
            }
        }
        //20091005 - 603.4
        if (checkInterveningIfClause(game)) {
            return super.resolve(game);
        }
        return false;
    }

    @Override
    public String getGameLogMessage(Game game) {
        MageObject object = game.getObject(sourceId);
        StringBuilder sb = new StringBuilder();
        if (object != null) {
            sb.append("Ability triggers: ").append(object.getName()).append(" - ").append(this.getRule(object.getName()));
        } else {
            sb.append("Ability triggers: ").append(this.getRule());
        }        
        String targetText = getTargetDescriptionForLog(getTargets(), game);
        if (!targetText.isEmpty()) {
            sb.append(" - ").append(targetText);
        }        
        return sb.toString();
    }

    @Override
    public String getRule() {
        String superRule = super.getRule(true);
        StringBuilder sb = new StringBuilder();
        if (!superRule.isEmpty()) {
            String ruleLow = superRule.toLowerCase();
            if (optional) {
                if (ruleLow.startsWith("you ")) {
                    if (!ruleLow.startsWith("you may")) {
                        StringBuilder newRule = new StringBuilder(superRule);
                        newRule.insert(4, "may ");
                        superRule = newRule.toString();
                    }
                } else {
                    if (this.getTargets().isEmpty()
                            || ruleLow.startsWith("exile")
                            || ruleLow.startsWith("destroy")
                            || ruleLow.startsWith("return")
                            || ruleLow.startsWith("tap")
                            || ruleLow.startsWith("untap")) {
                        sb.append("you may ");
                    } else {
                        sb.append("you may have ");
                    }
                }

            }
            sb.append(superRule);
        }

        return sb.toString();
    }
}
