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

package mage.filter;

import mage.Constants.TargetController;
import mage.game.Game;
import mage.game.stack.StackObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class FilterStackObject<T extends FilterStackObject<T>> extends FilterObject<StackObject, FilterStackObject<T>> {

	protected List<UUID> controllerId = new ArrayList<UUID>();
	protected boolean notController = false;
	protected TargetController controller = TargetController.ANY;

	public FilterStackObject() {
		super("spell or ability");
	}

	public FilterStackObject(String name) {
		super(name);
	}

	public FilterStackObject(final FilterStackObject<T> filter) {
		super(filter);
        this.controllerId.addAll(filter.controllerId);
		this.notController = filter.notController;
		this.controller = filter.controller;
	}

	@Override
	public boolean match(StackObject spell, Game game) {

		if (!super.match(spell, game))
			return notFilter;

		if (controllerId.size() > 0 && controllerId.contains(spell.getControllerId()) == notController)
			return notFilter;

		return !notFilter;
	}

	public boolean match(StackObject spell, UUID playerId, Game game) {
		if (!this.match(spell, game))
			return notFilter;

		if (controller != TargetController.ANY && playerId != null) {
			switch(controller) {
				case YOU:
					if (!spell.getControllerId().equals(playerId))
						return notFilter;
					break;
				case OPPONENT:
					if (!game.getOpponents(playerId).contains(spell.getControllerId()))
						return notFilter;
					break;
				case NOT_YOU:
					if (spell.getControllerId().equals(playerId))
						return notFilter;
					break;
			}
		}

		return !notFilter;
	}

	public List<UUID> getControllerId() {
		return controllerId;
	}

	public void setNotController(boolean notController) {
		this.notController = notController;
	}

	public void setTargetController(TargetController controller) {
		this.controller = controller;
	}

	@Override
	public FilterStackObject copy() {
		return new FilterStackObject(this);
	}

}
