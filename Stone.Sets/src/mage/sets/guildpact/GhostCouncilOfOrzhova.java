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

package mage.sets.guildpact;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.delayed.AtTheBeginOfNextEndStepDelayedTriggeredAbility;
import mage.abilities.costs.common.SacrificeTargetCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ReturnFromExileEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetControlledCreaturePermanent;
import mage.target.common.TargetOpponent;

/**
 *
 * @author Loki
 */
public class GhostCouncilOfOrzhova extends CardImpl {

    public GhostCouncilOfOrzhova (UUID ownerId) {
        super(ownerId, 114, "Ghost Council of Orzhova", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{W}{W}{B}{B}");
        this.expansionSetCode = "GPT";
        this.supertype.add("Legendary");
        this.subtype.add("Spirit");
        this.color.setWhite(true);
        this.color.setBlack(true);
        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // When Ghost Council of Orzhova enters the battlefield, target opponent loses 1 life and you gain 1 life.
        Ability ability = new EntersBattlefieldTriggeredAbility(new GhostCouncilOfOrzhovaEffect());
        ability.addTarget(new TargetOpponent());
        this.addAbility(ability);
        
        // {1}, Sacrifice a creature: Exile Ghost Council of Orzhova. Return it to the battlefield under its owner's control at the beginning of the next end step.
        ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new GhostCouncilOfOrzhovaRemovingEffect(), new GenericManaCost(1));
        ability.addCost(new SacrificeTargetCost(new TargetControlledCreaturePermanent()));
        this.addAbility(ability);
    }

    public GhostCouncilOfOrzhova (final GhostCouncilOfOrzhova card) {
        super(card);
    }

    @Override
    public GhostCouncilOfOrzhova copy() {
        return new GhostCouncilOfOrzhova(this);
    }

}

class GhostCouncilOfOrzhovaEffect extends OneShotEffect {
    GhostCouncilOfOrzhovaEffect() {
        super(Outcome.Damage);
        staticText = "target opponent loses 1 life and you gain 1 life";
    }

    GhostCouncilOfOrzhovaEffect(final GhostCouncilOfOrzhovaEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player targetPlayer = game.getPlayer(source.getFirstTarget());
        Player controllerPlayer = game.getPlayer(source.getControllerId());
        if (targetPlayer != null && controllerPlayer != null) {
            targetPlayer.damage(1, source.getSourceId(), game, false, true);
            controllerPlayer.gainLife(1, game);
        }
        return false;
    }

    @Override
    public GhostCouncilOfOrzhovaEffect copy() {
        return new GhostCouncilOfOrzhovaEffect(this);
    }

}

class GhostCouncilOfOrzhovaRemovingEffect extends OneShotEffect {

    private static final String effectText = "Exile {this}. Return it to the battlefield under its owner's control at the beginning of the next end step";

    GhostCouncilOfOrzhovaRemovingEffect () {
        super(Outcome.Benefit);
        staticText = effectText;
    }

    GhostCouncilOfOrzhovaRemovingEffect(GhostCouncilOfOrzhovaRemovingEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Permanent permanent = game.getPermanent(source.getSourceId());
            if (permanent != null) {
                if (controller.moveCardToExileWithInfo(permanent, source.getSourceId(), permanent.getName(), source.getSourceId(), game, Zone.BATTLEFIELD, true)) {
                    //create delayed triggered ability
                    AtTheBeginOfNextEndStepDelayedTriggeredAbility delayedAbility = new AtTheBeginOfNextEndStepDelayedTriggeredAbility(
                            new ReturnFromExileEffect(source.getSourceId(), Zone.BATTLEFIELD));
                    delayedAbility.setSourceId(source.getSourceId());
                    delayedAbility.setControllerId(source.getControllerId());
                    delayedAbility.setSourceObject(source.getSourceObject(game), game);
                    game.addDelayedTriggeredAbility(delayedAbility);
                    return true;
                }
            }            
        }
        return false;
    }

    @Override
    public GhostCouncilOfOrzhovaRemovingEffect copy() {
        return new GhostCouncilOfOrzhovaRemovingEffect(this);
    }

}
