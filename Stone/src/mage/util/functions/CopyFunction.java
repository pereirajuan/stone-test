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
package mage.util.functions;

import mage.Constants;
import mage.abilities.Ability;
import mage.cards.Card;
import mage.game.permanent.token.Token;

/**
 * @author nantuko
 */
public class CopyFunction implements Function<Card, Card> {

	protected Card target;

	public CopyFunction(Card target) {
		if (target == null)
			throw new IllegalArgumentException("Target can't be null");
		this.target = target;
	}

	@Override
	public Card apply(Card source) {
		if (target == null)
			throw new IllegalArgumentException("Target can't be null");

		target.setName(source.getName());
		target.getColor().setColor(source.getColor());
		target.getManaCost().clear();
		target.getManaCost().add(source.getManaCost());
		target.getCardType().clear();
		for (Constants.CardType type : source.getCardType()) {
			target.getCardType().add(type);
		}
		target.getSubtype().clear();
		for (String type : source.getSubtype()) {
			target.getSubtype().add(type);
		}
		target.getSupertype().clear();
		for (String type : source.getSupertype()) {
			target.getSupertype().add(type);
		}
		target.setExpansionSetCode(source.getExpansionSetCode());
		target.getAbilities().clear();

		for (Ability ability0 : source.getAbilities()) {
			Ability ability = ability0.copy();
			ability.newId();
			ability.setSourceId(target.getId());
			target.addAbility(ability);
		}

		target.getPower().setValue(source.getPower().getValue());
		target.getToughness().setValue(source.getToughness().getValue());

		return target;
	}

	public Card from(Card source) {
		return apply(source);
	}
}
