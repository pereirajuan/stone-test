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

package mage.player.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Zone;
import mage.abilities.keyword.DoubleStrikeAbility;
import mage.abilities.keyword.FirstStrikeAbility;
import mage.abilities.keyword.TrampleAbility;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class PermanentEvaluator {

	private Map<UUID, Integer> values = new HashMap<UUID, Integer>();

	public int evaluate(Permanent permanent, Game game) {
		if (!values.containsKey(permanent.getId())) {
			int value = 0;
			if (permanent.getCardType().contains(CardType.CREATURE)) {
				if (permanent.canAttack(game))
					value += 2;
				value += permanent.getPower().getValue();
				value += permanent.getToughness().getValue();
				value += permanent.getAbilities().getEvasionAbilities().size();
				value += permanent.getAbilities().getProtectionAbilities().size();
				value += permanent.getAbilities().containsKey(FirstStrikeAbility.getInstance().getId())?1:0;
				value += permanent.getAbilities().containsKey(DoubleStrikeAbility.getInstance().getId())?2:0;
				value += permanent.getAbilities().containsKey(TrampleAbility.getInstance().getId())?1:0;
			}
			value += permanent.getAbilities().getManaAbilities(Zone.BATTLEFIELD).size();
			value += permanent.getAbilities().getActivatedAbilities(Zone.BATTLEFIELD).size();
			values.put(permanent.getId(), value);
		}
		return values.get(permanent.getId());
	}

}
