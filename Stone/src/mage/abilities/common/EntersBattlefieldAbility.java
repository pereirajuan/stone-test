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

package mage.abilities.common;

import mage.abilities.StaticAbility;
import mage.abilities.condition.Condition;
import mage.abilities.effects.Effect;
import mage.abilities.effects.EntersBattlefieldEffect;
import mage.constants.Zone;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class EntersBattlefieldAbility extends StaticAbility {

    protected String abilityRule;
    
    public EntersBattlefieldAbility(Effect effect) {
        this(effect, true);
    }
/**
 *
 * @param effect effect that happens when the permanent enters the battlefield
 * @param showRule show the rule for this ability
 */
    public EntersBattlefieldAbility(Effect effect, Boolean showRule) {
        this(effect, null, showRule, null, null);
    }

    public EntersBattlefieldAbility(Effect effect, String effectText) {
        this(effect, null, true, null, effectText);
    }
/**
 *
 * @param effect effect that happens when the permanent enters the battlefield
 * @param condition only if this condition is true, the effect will happen
 * @param ruleVisible show the rule for this ability
 * @param abilityRule rule for this ability (no text from effects will be added)
 * @param effectText this text will be used for the EnterBattlefieldEffect
 */
    public EntersBattlefieldAbility(Effect effect, Condition condition, Boolean ruleVisible, String abilityRule, String effectText) {
        super(Zone.BATTLEFIELD, new EntersBattlefieldEffect(effect, condition, effectText));
        this.setRuleVisible(ruleVisible);
        this.abilityRule = abilityRule;
    }

    public EntersBattlefieldAbility(final EntersBattlefieldAbility ability) {
        super(ability);
        this.abilityRule = ability.abilityRule;
    }

    @Override
    public void addEffect(Effect effect) {
        if (getEffects().size() > 0) {
            Effect entersBattlefieldEffect = this.getEffects().get(0);
            if (entersBattlefieldEffect instanceof EntersBattlefieldEffect) {
                ((EntersBattlefieldEffect) entersBattlefieldEffect).addEffect(effect);
                return;
            }
        }
        super.addEffect(effect); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EntersBattlefieldAbility copy() {
        return new EntersBattlefieldAbility(this);
    }

    @Override
    public String getRule() {
        if (!ruleVisible) {
                return "";
        }
        if (abilityRule != null && !abilityRule.isEmpty()) {
            return abilityRule;
        }
        return "{this} enters the battlefield " + super.getRule();
    }
}
