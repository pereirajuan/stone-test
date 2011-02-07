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

package mage.abilities.effects.common.counter;

import mage.Constants.Outcome;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.counters.Counter;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class AddCountersTargetEffect extends OneShotEffect<AddCountersTargetEffect> {

	private int amount;
	private String name;
	private Counter counter;

	public AddCountersTargetEffect(String name, int amount) {
		super(Outcome.Benefit);
		this.name = name;
		this.amount = amount;
	}

	public AddCountersTargetEffect(Counter counter) {
		super(Outcome.Benefit);
		this.name = counter.getName();
		this.counter = counter;
	}

	public AddCountersTargetEffect(final AddCountersTargetEffect effect) {
		super(effect);
		this.amount = effect.amount;
		this.name = effect.name;
		this.counter = effect.counter;
	}

	@Override
	public boolean apply(Game game, Ability source) {
		Permanent permanent = game.getPermanent(source.getFirstTarget());
		if (permanent != null) {
			if (counter != null) {
				permanent.addCounters(counter);
			} else {
				permanent.addCounters(name, amount);
			}
		}
		return true;
	}

	@Override
	public String getText(Ability source) {
		StringBuilder sb = new StringBuilder();
		sb.append("put ");
		if (amount > 1) {
			sb.append(Integer.toString(amount)).append(" ").append(name).append(" counters on ");
		}
		else {
			sb.append("a ").append(name).append(" counter on ");
		}
		sb.append(source.getTargets().get(0).getTargetName());
		return sb.toString();
	}

	@Override
	public AddCountersTargetEffect copy() {
		return new AddCountersTargetEffect(this);
	}


}
