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
package mage.sets.oathofthegatewatch;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.WatcherScope;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.stack.Spell;
import mage.target.common.TargetCreatureOrPlayer;
import mage.watchers.Watcher;

/**
 *
 * @author fireshoes
 */
public class PyromancersAssault extends CardImpl {

    public PyromancersAssault(UUID ownerId) {
        super(ownerId, 115, "Pyromancer's Assault", Rarity.UNCOMMON, new CardType[]{CardType.ENCHANTMENT}, "{3}{R}");
        this.expansionSetCode = "OGW";

        // Whenever you cast your second spell each turn, Pyromancer's Assault deals 2 damage to target creature or player.
       Ability ability = new PyromancersAssaultTriggeredAbility();
       ability.addTarget(new TargetCreatureOrPlayer());
       this.addAbility(ability, new PyromancersAssaultWatcher());
    }

    public PyromancersAssault(final PyromancersAssault card) {
        super(card);
    }

    @Override
    public PyromancersAssault copy() {
        return new PyromancersAssault(this);
    }
}

class PyromancersAssaultTriggeredAbility extends TriggeredAbilityImpl {

    public PyromancersAssaultTriggeredAbility() {
        super(Zone.BATTLEFIELD, new DamageTargetEffect(2));
    }

    public PyromancersAssaultTriggeredAbility(final PyromancersAssaultTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public PyromancersAssaultTriggeredAbility copy() {
        return new PyromancersAssaultTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == EventType.SPELL_CAST;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getPlayerId().equals(controllerId)) {
            Watcher watcher = game.getState().getWatchers().get("SecondSpellCast", controllerId);
            if (watcher != null && watcher.conditionMet()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever you cast your second spell each turn, {this} deals 2 damage to target creature or player.";
    }
}

class PyromancersAssaultWatcher extends Watcher {

    int spellCount = 0;

    public PyromancersAssaultWatcher() {
        super("SecondSpellCast", WatcherScope.PLAYER);
    }

    public PyromancersAssaultWatcher(final PyromancersAssaultWatcher watcher) {
        super(watcher);
        this.spellCount = watcher.spellCount;
    }

    @Override
    public PyromancersAssaultWatcher copy() {
        return new PyromancersAssaultWatcher(this);
    }

    @Override
    public void watch(GameEvent event, Game game) {
        condition = false;
        if (event.getType() == GameEvent.EventType.SPELL_CAST && event.getPlayerId().equals(controllerId)) {
            Spell spell = game.getStack().getSpell(event.getTargetId());
            if (spell != null) {
                spellCount++;
                if (spellCount == 2) {
                    condition = true;
                }
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        spellCount = 0;
    }
}