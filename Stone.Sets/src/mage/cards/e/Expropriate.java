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

package mage.cards.e;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mage.abilities.Ability;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ExileSpellEffect;
import mage.abilities.effects.common.continuous.GainControlTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.FilterPermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.other.OwnerIdPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.turn.TurnMod;
import mage.players.Player;
import mage.players.Players;
import mage.target.Target;
import mage.target.TargetPermanent;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author JRHerlehy
 */
public class Expropriate extends CardImpl {

    public Expropriate(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{7}{U}{U}");

        // <i>Council's dilemma</i> &mdash; Starting with you, each player votes for time or money. For each time vote, take an extra turn after this one. For each money vote, choose a permanent owned by the voter and gain control of it. Exile Expropriate
        this.getSpellAbility().addEffect(new ExpropriateDilemmaEffect());
        this.getSpellAbility().addEffect(ExileSpellEffect.getInstance());
    }

    public Expropriate(final Expropriate card) {
        super(card);
    }

    @Override
    public Expropriate copy() {
        return new Expropriate(this);
    }
}

class ExpropriateDilemmaEffect extends OneShotEffect {

    public ExpropriateDilemmaEffect() {
        super(Outcome.Benefit);
        this.staticText = "<i>Council's dilemma</i> — Starting with you, each player votes for time or money. For each time vote, take an extra turn after this one. For each money vote, choose a permanent owned by the voter and gain control of it.";
    }

    public ExpropriateDilemmaEffect(final ExpropriateDilemmaEffect effect) {
        super(effect);
    }

    public ExpropriateDilemmaEffect(Outcome outcome) {
        super(outcome);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());

        //If not controller, exit out here and do not vote.
        if (controller == null) return false;

        int timeCount = 0, moneyCount = 0;
        Players moneyVoters = new Players();

        for (UUID playerId : game.getState().getPlayersInRange(controller.getId(), game)) {
            Player player = game.getPlayer(playerId);
            if (player != null) {
                if (player.chooseUse(Outcome.Benefit, "Choose time?", source, game)) {
                    timeCount++;
                    game.informPlayers(player.getName() + " has voted for time");
                } else {
                    moneyCount++;
                    moneyVoters.addPlayer(player);
                    game.informPlayers(player.getName() + " has voted for money");
                }
            }
        }

        if (timeCount > 0) {
            if (timeCount == 1) {
                game.informPlayers(controller.getName() + " will take an extra turn");
            } else {
                game.informPlayers(controller.getName() + " will take " + timeCount + " extra turns");
            }

            do {
                game.getState().getTurnMods().add(new TurnMod(source.getControllerId(), false));
                timeCount--;
            } while (timeCount > 0);
        }

        if (moneyCount > 0) {
            List<Permanent> chosenCards = new ArrayList<>();

            for (UUID playerId : moneyVoters.keySet()) {
                FilterPermanent filter = new FilterPermanent("permanent owned by " + game.getPlayer(playerId).getName());
                filter.add(new OwnerIdPredicate(playerId));

                Target target = new TargetPermanent(filter);
                target.setNotTarget(true);

                if (controller.choose(Outcome.GainControl, target, source.getSourceId(), game)) {
                    Permanent targetPermanent = game.getPermanent(target.getFirstTarget());

                    if (targetPermanent != null) chosenCards.add(targetPermanent);
                }
            }

            for (Permanent permanent : chosenCards) {
                ContinuousEffect effect = new ExpropriateControlEffect(controller.getId());
                effect.setTargetPointer(new FixedTarget(permanent.getId()));
                game.addEffect(effect, source);
            }
        } //End moneyCount if statement

        return true;
    }

    @Override
    public ExpropriateDilemmaEffect copy() {
        return new ExpropriateDilemmaEffect(this);
    }

}

class ExpropriateControlEffect extends ContinuousEffectImpl {

    private UUID controllerId;

    public ExpropriateControlEffect(UUID controllerId) {
        super(Duration.EndOfGame, Layer.ControlChangingEffects_2, SubLayer.NA, Outcome.GainControl);
        this.controllerId = controllerId;
    }

    public ExpropriateControlEffect(final ExpropriateControlEffect effect) {
        super(effect);
        this.controllerId = effect.controllerId;
    }

    @Override
    public ExpropriateControlEffect copy() {
        return new ExpropriateControlEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(targetPointer.getFirst(game, source));
        if (permanent != null && controllerId != null) {
            return permanent.changeControllerId(controllerId, game);
        }
        return false;
    }
}
