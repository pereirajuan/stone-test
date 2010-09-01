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

package mage.abilities.effects.common;

import mage.Constants.Outcome;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.choices.ChoiceColor;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class AddManaOfAnyColorEffect extends OneShotEffect<AddManaOfAnyColorEffect> {

	int amount;

	public AddManaOfAnyColorEffect(int amount) {
		super(Outcome.PutManaInPool);
		this.amount = amount;
	}

	public AddManaOfAnyColorEffect(final AddManaOfAnyColorEffect effect) {
		super(effect);
		this.amount = effect.amount;
	}

	@Override
	public AddManaOfAnyColorEffect copy() {
		return new AddManaOfAnyColorEffect(this);
	}

	@Override
	public boolean apply(Game game, Ability source) {
		ChoiceColor choice = (ChoiceColor) source.getChoices().get(0);
		Player player = game.getPlayer(source.getControllerId());
		if (choice.getColor().isBlack()) {
			player.getManaPool().changeMana(Mana.BlackMana);
			return true;
		}
		else if (choice.getColor().isBlue()) {
			player.getManaPool().changeMana(Mana.BlueMana);
			return true;
		}
		else if (choice.getColor().isRed()) {
			player.getManaPool().changeMana(Mana.RedMana);
			return true;
		}
		else if (choice.getColor().isGreen()) {
			player.getManaPool().changeMana(Mana.GreenMana);
			return true;
		}
		else if (choice.getColor().isWhite()) {
			player.getManaPool().changeMana(Mana.WhiteMana);
			return true;
		}
		return false;
	}

	@Override
	public String getText(Ability source) {
		if (amount > 1)
			return "add " + Integer.toString(amount) + " mana of any color to your mana pool";
		else
			return "add one mana of any color to your mana pool";
	}


}
