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

import mage.constants.Outcome;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.OneShotEffect;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.predicate.permanent.ControllerPredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.target.common.TargetControlledPermanent;

import java.util.UUID;

/**
 * 702.84. Annihilator
 *   702.84a Annihilator is a triggered ability. "Annihilator N" means "Whenever this
 *   creature attacks, defending player sacrifices N permanents."
 *
 *   702.84b If a creature has multiple instances of annihilator, each triggers separately.
 * 
 * @author maurer.it_at_gmail.com
 */
public class AnnihilatorAbility extends TriggeredAbilityImpl<AnnihilatorAbility> {

    int count;

    public AnnihilatorAbility ( int count ) {
        super(Zone.BATTLEFIELD, new AnnihilatorEffect(count), false);
        this.count = count;
    }

    public AnnihilatorAbility ( final AnnihilatorAbility ability ) {
        super(ability);
        this.count = ability.count;
    }

    @Override
    public boolean checkTrigger ( GameEvent event, Game game ) {
        if ( event.getType() == EventType.ATTACKER_DECLARED && event.getSourceId().equals(this.getSourceId()) ) {
            return true;
        }
        return false;
    }

    @Override
    public String getRule ( ) {
        return "Annihilator " + count;
    }

    @Override
    public AnnihilatorAbility copy ( ) {
        return new AnnihilatorAbility(this);
    }

}

class AnnihilatorEffect extends OneShotEffect<AnnihilatorEffect> {

    private final int count;
    private static final FilterControlledPermanent filter = new FilterControlledPermanent();;
    static {
        filter.add(new ControllerPredicate(TargetController.YOU));
    }

    AnnihilatorEffect ( int count ) {
        super(Outcome.Sacrifice);
        this.count = count;
    }

    AnnihilatorEffect(AnnihilatorEffect effect) {
        super(effect);
        this.count = effect.count;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        UUID defenderId = game.getCombat().getDefenderId(source.getSourceId());
        Player player = game.getPlayer(defenderId);

        //Defender may be a planeswalker.
        if ( player == null ) {
            Permanent permanent = game.getPermanent(defenderId);
            player = game.getPlayer(permanent.getControllerId());
        }

   
        int amount = Math.min(count, game.getBattlefield().countAll(filter, player.getId(), game));
        Target target = new TargetControlledPermanent(amount, amount, filter, false);

        //A spell or ability could have removed the only legal target this player
        //had, if thats the case this ability should fizzle.
        if (target.canChoose(player.getId(), game)) {
            boolean abilityApplied = false;
            while (!target.isChosen() && target.canChoose(player.getId(), game) && player.isInGame()) {
                player.choose(Outcome.Sacrifice, target, source.getSourceId(), game);
            }

            for ( int idx = 0; idx < target.getTargets().size(); idx++) {
                Permanent permanent = game.getPermanent((UUID)target.getTargets().get(idx));

                if ( permanent != null ) {
                    abilityApplied |= permanent.sacrifice(source.getId(), game);
                }
            }

            return abilityApplied;
        }
        return false;
    }

    @Override
    public AnnihilatorEffect copy() {
        return new AnnihilatorEffect(this);
    }

}
