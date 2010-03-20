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

import mage.Constants.Duration;
import mage.Constants.Layer;
import mage.Constants.Outcome;
import mage.Constants.SubLayer;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.Token;
import mage.util.Copier;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class BecomesCreatureSourceEOTEffect extends ContinuousEffectImpl {

	protected Token token;

	public BecomesCreatureSourceEOTEffect(Token token) {
		super(Duration.EndOfTurn, Outcome.BecomeCreature);
		this.token = token;
	}

	@Override
	public boolean apply(Layer layer, SubLayer sublayer, Game game) {
		Permanent permanent = game.getPermanent(source.getSourceId());
		switch (layer) {
			case TypeChangingEffects_4:
				if (sublayer == SubLayer.NA) {
					if (token.getCardType().size() > 0)
						permanent.getCardType().addAll(token.getCardType());
					if (token.getSubtype().size() > 0)
						permanent.getSubtype().addAll(token.getSubtype());
				}
				break;
			case ColorChangingEffects_5:
				if (sublayer == SubLayer.NA) {
					if (token.getColor().hasColor())
						permanent.getColor().setColor(token.getColor());
				}
				break;
			case AbilityAddingRemovingEffects_6:
				if (sublayer == SubLayer.NA) {
					if (token.getAbilities().size() > 0) {
						for (Ability ability: token.getAbilities()) {
							permanent.addAbility(ability);
						}
					}
				}
				break;
			case PTChangingEffects_7:
				if (sublayer == SubLayer.SetPT_7b) {
					if (token.getPower() != MageInt.EmptyMageInt)
						permanent.getPower().setValue(token.getPower().getValue());
					if (token.getToughness() != MageInt.EmptyMageInt)
						permanent.getToughness().setValue(token.getToughness().getValue());
				}
		}
		return true;
	}

	@Override
	public boolean apply(Game game) {
		return false;
	}

	@Override
	public String getText() {
		return "Until end of turn {this} becomes a " + token.getDescription() + ". It's still a land";
	}

}
