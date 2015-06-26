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
package mage.sets.apocalypse;

import java.util.Set;
import java.util.UUID;

import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.BecomesSubtypeTargetEffect;
import mage.cards.CardImpl;
import mage.cards.repository.CardRepository;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCreaturePermanent;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author EvilGeek
 */
public class UnnaturalSelection extends CardImpl {

    public UnnaturalSelection(UUID ownerId) {
        super(ownerId, 32, "Unnatural Selection", Rarity.RARE, new CardType[]{CardType.ENCHANTMENT}, "{1}{U}");
        this.expansionSetCode = "APC";

        // {1}: Choose a creature type other than Wall. Target creature becomes that type until end of turn.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new UnnaturalSelectionEffect(), new GenericManaCost(1));
        ability.addTarget(new TargetCreaturePermanent());
        this.addAbility(ability);
    }

    public UnnaturalSelection(final UnnaturalSelection card) {
        super(card);
    }

    @Override
    public UnnaturalSelection copy() {
        return new UnnaturalSelection(this);
    }
}

class UnnaturalSelectionEffect extends OneShotEffect {

	public UnnaturalSelectionEffect() {
		super(Outcome.BoostCreature);
		staticText = "choose a creature type other than wall, target creature's type becomes that type until end of turn";
	}
	
	public UnnaturalSelectionEffect(final UnnaturalSelectionEffect effect) {
        super(effect);
    }

	@Override
	public boolean apply(Game game, Ability source) {
		Player player = game.getPlayer(source.getControllerId());
        Permanent permanent = game.getPermanent(source.getSourceId());
        String chosenType = "";
        if (player != null && permanent != null) {
            Choice typeChoice = new ChoiceImpl(true);
            typeChoice.setMessage("Choose creature type other than Wall");
            Set<String> types = CardRepository.instance.getCreatureTypes();
            types.remove("Wall");
            typeChoice.setChoices(types);
            while (!player.choose(Outcome.BoostCreature, typeChoice, game)) {
                if (!player.isInGame()) {
                    return false;
                }
            }
            game.informPlayers(permanent.getName() + ": " + player.getLogName() + " has chosen " + typeChoice.getChoice());
            chosenType = typeChoice.getChoice();
            if (chosenType != null && !chosenType.isEmpty()) {
                // ADD TYPE TO TARGET
                ContinuousEffect effect = new BecomesSubtypeTargetEffect(Duration.EndOfTurn, chosenType);
                effect.setTargetPointer(new FixedTarget(getTargetPointer().getFirst(game, source)));
                game.addEffect(effect, source);
                return true;
            }
            
        }
        return false;
	}

	@Override
	public Effect copy() {
		return new UnnaturalSelectionEffect(this);
	}

	
	
}