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

package mage.abilities.effects.common;

import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.PhaseStep;
import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author BetaSteward_at_googlemail.com
 */
public class SkipNextUntapTargetEffect extends ReplacementEffectImpl<SkipNextUntapTargetEffect> {

    protected Set<UUID> usedFor = new HashSet<UUID>();
    protected int count;

    public SkipNextUntapTargetEffect() {
        super(Duration.OneUse, Outcome.Detriment);
    }

    public SkipNextUntapTargetEffect(String text) {
        this();
        this.staticText = text;
    }

    public SkipNextUntapTargetEffect(final SkipNextUntapTargetEffect effect) {
        super(effect);
        for (UUID uuid : effect.usedFor) {
            this.usedFor.add(uuid);
        }
        this.count = effect.count;
    }

    @Override
    public SkipNextUntapTargetEffect copy() {
        return new SkipNextUntapTargetEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return false;
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        if (targetPointer.getTargets(game, source).size() < 2) {
            used = true;
        } else {
            count++;
        }
        // not clear how to turn off the effect for more than one target
        // especially as some targets may leave the battlefield since the effect creation
        // so handling this in applies method is the only option for now for such cases
        if (count == targetPointer.getTargets(game, source).size()) {
            // this won't work for targets disappeared before applies() return true
            used = true;
        }
        return true;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (game.getTurn().getStepType() == PhaseStep.UNTAP && event.getType() == EventType.UNTAP) {
            for (UUID target : targetPointer.getTargets(game, source)) {
                if (event.getTargetId().equals(target)) {
                    if (!usedFor.contains(target)) {
                        usedFor.add(target);
                        return true;
                    }
                    break;
                }
            }

            return false;
        }
        return false;
    }

    @Override
    public String getText(Mode mode) {
            if (staticText.length() > 0) 
                return staticText + " doesn't untap during its controller's next untap step";
            else 
        return "Target " + mode.getTargets().get(0).getTargetName() + " doesn't untap during its controller's next untap step";
    }

}
