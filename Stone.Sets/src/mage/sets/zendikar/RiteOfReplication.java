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
package mage.sets.zendikar;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.condition.common.KickedCondition;
import mage.abilities.decorator.ConditionalOneShotEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.KickerAbility;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.EmptyToken;
import mage.target.common.TargetCreaturePermanent;
import mage.util.CardUtil;

/**
 *
 * @author North
 */
public class RiteOfReplication extends CardImpl {

    public RiteOfReplication(UUID ownerId) {
        super(ownerId, 61, "Rite of Replication", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{2}{U}{U}");
        this.expansionSetCode = "ZEN";


        // Kicker {5}
        this.addAbility(new KickerAbility("{5}"));

        // Put a token that's a copy of target creature onto the battlefield. If Rite of Replication was kicked, put five of those tokens onto the battlefield instead.
        this.getSpellAbility().addTarget(new TargetCreaturePermanent());
        this.getSpellAbility().addEffect(new ConditionalOneShotEffect(new RiteOfReplicationEffect(5),
                new RiteOfReplicationEffect(1), KickedCondition.getInstance(),
                "Put a token that's a copy of target creature onto the battlefield. If {this} was kicked, put five of those tokens onto the battlefield instead"));
    }

    public RiteOfReplication(final RiteOfReplication card) {
        super(card);
    }

    @Override
    public RiteOfReplication copy() {
        return new RiteOfReplication(this);
    }
}

class RiteOfReplicationEffect extends OneShotEffect {

    private final int amount;

    public RiteOfReplicationEffect(int amount) {
        super(Outcome.PutCreatureInPlay);
        this.amount = amount;
    }

    public RiteOfReplicationEffect(final RiteOfReplicationEffect effect) {
        super(effect);
        this.amount = effect.amount;
    }

    @Override
    public RiteOfReplicationEffect copy() {
        return new RiteOfReplicationEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(source.getFirstTarget());
        if (permanent == null) {
            permanent = (Permanent) game.getLastKnownInformation(source.getFirstTarget(), Zone.BATTLEFIELD);
        }
        if (permanent != null) {
            EmptyToken token = new EmptyToken();
            CardUtil.copyTo(token).from(permanent);
            token.putOntoBattlefield(amount, game, source.getSourceId(), source.getControllerId());
            return true;
        }
        return false;
    }
}
