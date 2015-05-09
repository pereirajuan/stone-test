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
package mage.sets.magic2012;

import mage.constants.*;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.AsEntersBattlefieldAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.cards.repository.CardRepository;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

import java.util.UUID;

/**
 * @author nantuko
 */
public class AdaptiveAutomaton extends CardImpl {

    public AdaptiveAutomaton(UUID ownerId) {
        super(ownerId, 201, "Adaptive Automaton", Rarity.RARE, new CardType[]{CardType.ARTIFACT, CardType.CREATURE}, "{3}");
        this.expansionSetCode = "M12";
        this.subtype.add("Construct");

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // As Adaptive Automaton enters the battlefield, choose a creature type.
        this.addAbility(new AsEntersBattlefieldAbility(new AdaptiveAutomatonEffect()));
        // Adaptive Automaton is the chosen type in addition to its other types.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new AdaptiveAutomatonAddSubtypeEffect()));
        // Other creatures you control of the chosen type get +1/+1.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new AdaptiveAutomatonBoostControlledEffect()));
    }

    public AdaptiveAutomaton(final AdaptiveAutomaton card) {
        super(card);
    }

    @Override
    public AdaptiveAutomaton copy() {
        return new AdaptiveAutomaton(this);
    }
}

class AdaptiveAutomatonEffect extends OneShotEffect {

    public AdaptiveAutomatonEffect() {
        super(Outcome.BoostCreature);
        staticText = "choose a creature type";
    }

    public AdaptiveAutomatonEffect(final AdaptiveAutomatonEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        Permanent permanent = game.getPermanent(source.getSourceId());
        if (player != null && permanent != null) {
            Choice typeChoice = new ChoiceImpl(true);
            typeChoice.setMessage("Choose creature type");
            typeChoice.setChoices(CardRepository.instance.getCreatureTypes());
            while (!player.choose(Outcome.BoostCreature, typeChoice, game)) {
                if (!player.isInGame()) {
                    return false;
                }
            }
            game.informPlayers(permanent.getName() + ": " + player.getLogName() + " has chosen " + typeChoice.getChoice());
            game.getState().setValue(permanent.getId() + "_type", typeChoice.getChoice());
            permanent.addInfo("chosen type", "<i>Chosen type: " + typeChoice.getChoice().toString() + "</i>", game);
        }
        return false;
    }

    @Override
    public AdaptiveAutomatonEffect copy() {
        return new AdaptiveAutomatonEffect(this);
    }

}

class AdaptiveAutomatonAddSubtypeEffect extends ContinuousEffectImpl {
    public AdaptiveAutomatonAddSubtypeEffect() {
        super(Duration.WhileOnBattlefield, Layer.TypeChangingEffects_4, SubLayer.NA, Outcome.Benefit);
        staticText = "{this} is the chosen type in addition to its other types";
    }

    public AdaptiveAutomatonAddSubtypeEffect(final AdaptiveAutomatonAddSubtypeEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(source.getSourceId());
        if (permanent != null) {
            String subtype = (String) game.getState().getValue(permanent.getId() + "_type");
            if (subtype != null && !permanent.getSubtype().contains(subtype)) {
                permanent.getSubtype().add(subtype);
            }
        }
        return true;
    }

    @Override
    public AdaptiveAutomatonAddSubtypeEffect copy() {
        return new AdaptiveAutomatonAddSubtypeEffect(this);
    }
}

class AdaptiveAutomatonBoostControlledEffect extends ContinuousEffectImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent();

    public AdaptiveAutomatonBoostControlledEffect() {
        super(Duration.WhileOnBattlefield, Layer.PTChangingEffects_7, SubLayer.ModifyPT_7c, Outcome.BoostCreature);
        staticText = "Other creatures you control of the chosen type get +1/+1";
    }

    public AdaptiveAutomatonBoostControlledEffect(final AdaptiveAutomatonBoostControlledEffect effect) {
        super(effect);
    }

    @Override
    public AdaptiveAutomatonBoostControlledEffect copy() {
        return new AdaptiveAutomatonBoostControlledEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(source.getSourceId());
        if (permanent != null) {
            String subtype = (String) game.getState().getValue(permanent.getId() + "_type");
            if (subtype != null) {
                for (Permanent perm: game.getBattlefield().getAllActivePermanents(filter, source.getControllerId(), game)) {
                    if (!perm.getId().equals(source.getSourceId()) && perm.hasSubtype(subtype)) {
                        perm.addPower(1);
                        perm.addToughness(1);
                    }
                }
            }
        }
        return true;
    }

}
