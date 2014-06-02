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

package mage.sets.zendikar;

import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Rarity;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.abilities.effects.common.continious.GainAbilityControlledEffect;
import mage.abilities.keyword.ProtectionAbility;
import mage.cards.CardImpl;
import mage.choices.ChoiceColor;
import mage.filter.FilterCard;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.mageobject.ColorPredicate;
import mage.game.Game;

import java.util.UUID;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class BraveTheElements extends CardImpl {

    public BraveTheElements(UUID ownerId) {
        super(ownerId, 4, "Brave the Elements", Rarity.UNCOMMON, new CardType[]{CardType.INSTANT}, "{W}");
        this.expansionSetCode = "ZEN";
        this.color.setWhite(true);
        this.getSpellAbility().addChoice(new ChoiceColor());
        this.getSpellAbility().addEffect(new BraveTheElementsEffect());
    }

    public BraveTheElements(final BraveTheElements card) {
        super(card);
    }

    @Override
    public BraveTheElements copy() {
        return new BraveTheElements(this);
    }

}

class BraveTheElementsEffect extends GainAbilityControlledEffect {

    private static final FilterCreaturePermanent filter1 = new FilterCreaturePermanent();

    static {
        filter1.add(new ColorPredicate(ObjectColor.WHITE));
    }

    private FilterCard filter2;

    public BraveTheElementsEffect() {
        super(new ProtectionAbility(new FilterCard()), Duration.EndOfTurn, filter1);
        filter2 = (FilterCard)((ProtectionAbility)getFirstAbility()).getFilter();
        staticText = "Choose a color. White creatures you control gain protection from the chosen color until end of turn";
    }

    public BraveTheElementsEffect(final BraveTheElementsEffect effect) {
        super(effect);
        this.filter2 = effect.filter2.copy();
    }

    @Override
    public BraveTheElementsEffect copy() {
        return new BraveTheElementsEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        ChoiceColor choice = (ChoiceColor) source.getChoices().get(0);
        filter2.add(new ColorPredicate(choice.getColor()));
        filter2.setMessage(choice.getChoice());
        setAbility(new ProtectionAbility(new FilterCard(filter2)));
        return super.apply(game, source);
    }

}