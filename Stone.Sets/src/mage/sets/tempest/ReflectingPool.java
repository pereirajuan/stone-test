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
package mage.sets.tempest;

import java.util.List;
import java.util.UUID;

import mage.constants.CardType;
import mage.constants.Rarity;
import mage.Mana;
import mage.abilities.Abilities;
import mage.abilities.Ability;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.common.ManaEffect;
import mage.abilities.mana.ManaAbility;
import mage.abilities.mana.SimpleManaAbility;
import mage.cards.CardImpl;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.constants.Zone;
import mage.filter.common.FilterControlledLandPermanent;
import mage.filter.common.FilterControlledPermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

/**
 *
 * @author jeffwadsworth
 */
public class ReflectingPool extends CardImpl {

    public ReflectingPool(UUID ownerId) {
        super(ownerId, 328, "Reflecting Pool", Rarity.RARE, new CardType[]{CardType.LAND}, "");
        this.expansionSetCode = "TMP";

        // {T}: Add to your mana pool one mana of any type that a land you control could produce.
        this.addAbility(new SimpleManaAbility(Zone.BATTLEFIELD, new ReflectingPoolEffect(), new TapSourceCost()));
    }

    public ReflectingPool(final ReflectingPool card) {
        super(card);
    }

    @Override
    public ReflectingPool copy() {
        return new ReflectingPool(this);
    }
}

class ReflectingPoolEffect extends ManaEffect<ReflectingPoolEffect> {

    private static final FilterControlledPermanent filter = new FilterControlledLandPermanent();

    public ReflectingPoolEffect() {
        super();
        staticText = "Add to your mana pool one mana of any type that a land you control could produce";
    }

    public ReflectingPoolEffect(final ReflectingPoolEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        List<Permanent> lands = game.getBattlefield().getActivePermanents(filter, source.getControllerId(), game);
        Mana types = new Mana();
        for (Permanent land : lands) {
            Abilities<ManaAbility> mana = land.getAbilities().getManaAbilities(Zone.BATTLEFIELD);
            for (ManaAbility ability : mana) {
                types.add(ability.getNetMana(game));
            }
        }
        Choice choice = new ChoiceImpl(true);
        choice.setMessage("Pick a mana color");
        if (types.getBlack() > 0) {
            choice.getChoices().add("Black");
        }
        if (types.getRed() > 0) {
            choice.getChoices().add("Red");
        }
        if (types.getBlue() > 0) {
            choice.getChoices().add("Blue");
        }
        if (types.getGreen() > 0) {
            choice.getChoices().add("Green");
        }
        if (types.getWhite() > 0) {
            choice.getChoices().add("White");
        }
        if (types.getColorless() > 0) {
            choice.getChoices().add("Colorless");
        }
        if (types.getAny() > 0) {
            choice.getChoices().add("Black");
            choice.getChoices().add("Red");
            choice.getChoices().add("Blue");
            choice.getChoices().add("Green");
            choice.getChoices().add("White");
            choice.getChoices().add("Colorless");
        }
        if (choice.getChoices().size() > 0) {
            Player player = game.getPlayer(source.getControllerId());
            if (choice.getChoices().size() == 1) {
                choice.setChoice(choice.getChoices().iterator().next());
            } else {
                player.choose(outcome, choice, game);
            }
            if (choice.getChoice() != null) {
                switch (choice.getChoice()) {
                    case "Black":
                        player.getManaPool().addMana(Mana.BlackMana, game, source);
                        break;
                    case "Blue":
                        player.getManaPool().addMana(Mana.BlueMana, game, source);
                        break;
                    case "Red":
                        player.getManaPool().addMana(Mana.RedMana, game, source);
                        break;
                    case "Green":
                        player.getManaPool().addMana(Mana.GreenMana, game, source);
                        break;
                    case "White":
                        player.getManaPool().addMana(Mana.WhiteMana, game, source);
                        break;
                    case "Colorless":
                        player.getManaPool().addMana(Mana.ColorlessMana, game, source);
                        break;
                }
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public ReflectingPoolEffect copy() {
        return new ReflectingPoolEffect(this);
    }
}