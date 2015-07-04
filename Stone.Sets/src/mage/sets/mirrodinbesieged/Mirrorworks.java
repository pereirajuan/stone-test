/*
 *  Copyright 2011 BetaSteward_at_googlemail.com. All rights reserved.
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
package mage.sets.mirrodinbesieged;

import java.util.UUID;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.costs.Cost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.Permanent;
import mage.game.permanent.PermanentToken;
import mage.game.permanent.token.EmptyToken;
import mage.players.Player;
import mage.target.targetpointer.FixedTarget;
import mage.util.CardUtil;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class Mirrorworks extends CardImpl {

    public Mirrorworks(UUID ownerId) {
        super(ownerId, 114, "Mirrorworks", Rarity.RARE, new CardType[]{CardType.ARTIFACT}, "{5}");
        this.expansionSetCode = "MBS";

        // Whenever another nontoken artifact enters the battlefield under your control, you may pay {2}.
        // If you do, put a token that's a copy of that artifact onto the battlefield.
        this.addAbility(new MirrorworksAbility());
    }

    public Mirrorworks(final Mirrorworks card) {
        super(card);
    }

    @Override
    public Mirrorworks copy() {
        return new Mirrorworks(this);
    }

}

class MirrorworksAbility extends TriggeredAbilityImpl {

    public MirrorworksAbility() {
        super(Zone.BATTLEFIELD, new MirrorworksEffect());
     }

    public MirrorworksAbility(final MirrorworksAbility ability) {
        super(ability);
    }

    @Override
    public MirrorworksAbility copy() {
        return new MirrorworksAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == EventType.ENTERS_THE_BATTLEFIELD;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (!event.getTargetId().equals(this.getSourceId())) {
            Permanent permanent = game.getPermanent(event.getTargetId());
            if (permanent != null
                    && permanent.getControllerId().equals(getControllerId())
                    && !(permanent instanceof PermanentToken)
                    && permanent.getCardType().contains(CardType.ARTIFACT)) {
                getEffects().get(0).setTargetPointer(new FixedTarget(permanent.getId()));
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever another nontoken artifact enters the battlefield under your control, you may pay {2}. If you do, put a token that's a copy of that artifact onto the battlefield";
    }

}

class MirrorworksEffect extends OneShotEffect {

    public MirrorworksEffect() {
        super(Outcome.PutCreatureInPlay);
        this.staticText = "put a token that's a copy of that artifact onto the battlefield";
    }

    public MirrorworksEffect(final MirrorworksEffect effect) {
        super(effect);
    }

    @Override
    public MirrorworksEffect copy() {
        return new MirrorworksEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        UUID targetId = targetPointer.getFirst(game, source);
        if (targetId != null && player != null) {
            MageObject target = game.getPermanent(targetId);
            if (target == null) {
                target = game.getLastKnownInformation(targetId, Zone.BATTLEFIELD);
            }
            if (target != null) {
                Cost cost = new ManaCostsImpl("{2}");
                if (player.chooseUse(outcome, new StringBuilder("Pay ").append(cost.getText()).append(" and put a token copy of ").append(target.getName()).append(" onto the battlefield").toString(), source, game)) {
                    cost.clearPaid();
                    if (cost.pay(source, game, source.getSourceId(), source.getControllerId(), false)) {
                        if (target instanceof Permanent) {
                            EmptyToken token = new EmptyToken();
                            CardUtil.copyTo(token).from((Permanent)target);
                            token.putOntoBattlefield(1, game, source.getSourceId(), source.getControllerId());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
