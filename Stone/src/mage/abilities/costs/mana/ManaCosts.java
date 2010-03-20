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

package mage.abilities.costs.mana;

import java.util.ArrayList;
import java.util.List;
import mage.Constants.ColoredManaSymbol;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.costs.Costs;
import mage.abilities.costs.CostsImpl;
import mage.abilities.mana.ManaOptions;
import mage.game.Game;
import mage.players.ManaPool;
import mage.players.Player;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ManaCosts extends CostsImpl<ManaCost> implements Costs<ManaCost>, ManaCost {

	public ManaCosts(String mana) {
		load(mana);
	}

	public ManaCosts(Ability ability) {
		super(ability);
	}

	public ManaCosts(Ability ability, String mana) {
		super(ability);
		load(mana);
	}

	@Override
	public int convertedManaCost() {
		int total = 0;
		for (ManaCost cost: this) {
			total += cost.convertedManaCost();
		}
		return total;
	}

	@Override
	public Mana getPayment() {
		Mana manaTotal = new Mana();
		for (ManaCost cost: this) {
			manaTotal.add(cost.getPayment());
		}
		return manaTotal;
	}

	@Override
	public boolean pay(Game game, boolean noMana) {
		if (noMana) {
			setPaid();
			return true;
		}
		Player player = game.getPlayer(this.getAbility().getControllerId());
		assignPayment(player.getManaPool());
		while (!isPaid()) {
			if (player.playMana(this.getUnpaid(), game))
				assignPayment(player.getManaPool());
			else
				return false;
		}
		for (ManaCost cost: this.getUnpaidVariableCosts()) {
			VariableManaCost vCost = (VariableManaCost) cost;
			while (!vCost.isPaid()) {
				if (player.playXMana(vCost, game))
					vCost.assignPayment(player.getManaPool());
				else
					return false;
			}
		}
		return true;
	}

	@Override
	public ManaCosts getUnpaid() {
		ManaCosts unpaid = new ManaCosts(ability);
		for (ManaCost cost: this) {
			if (!(cost instanceof VariableManaCost) && !cost.isPaid())
				unpaid.add(cost.getUnpaid());
		}
		return unpaid;
	}

	public ManaCosts getUnpaidVariableCosts() {
		ManaCosts unpaid = new ManaCosts(ability);
		for (ManaCost cost: this) {
			if (cost instanceof VariableManaCost && !cost.isPaid())
				unpaid.add(cost.getUnpaid());
		}
		return unpaid;
	}

	@Override
	public List<VariableManaCost> getVariableCosts() {
		List<VariableManaCost> variableCosts = new ArrayList<VariableManaCost>();
		for (ManaCost cost: this) {
			if (cost instanceof VariableManaCost)
				variableCosts.add((VariableManaCost) cost);
		}
		return variableCosts;
	}

	@Override
	public void assignPayment(ManaPool pool) {
		//attempt to pay colored costs first
		for (ManaCost cost: this) {
			if (!cost.isPaid() && cost instanceof ColoredManaCost) {
				cost.assignPayment(pool);
			}
		}

		for (ManaCost cost: this) {
			if (!cost.isPaid() && cost instanceof HybridManaCost) {
				cost.assignPayment(pool);
			}
		}

		for (ManaCost cost: this) {
			if (!cost.isPaid() && cost instanceof MonoHybridManaCost) {
				cost.assignPayment(pool);
			}
		}

		for (ManaCost cost: this) {
			if (!cost.isPaid() && cost instanceof GenericManaCost) {
				cost.assignPayment(pool);
			}
		}

		for (ManaCost cost: this) {
			if (!cost.isPaid() && cost instanceof VariableManaCost) {
				cost.assignPayment(pool);
			}
		}
	}

	public void load(String mana) {
		this.clear();
		if (mana == null || mana.length() == 0)
			return;
		String[] symbols = mana.split("^\\{|\\}\\{|\\}$");
		for (String symbol: symbols) {
			if (symbol.length() > 0) {
				if (symbol.length() == 1) {
					if (Character.isDigit(symbol.charAt(0))) {
						this.add(new GenericManaCost(Integer.valueOf(symbol)));
					}
					else {
						if (!symbol.equals("X"))
							this.add(new ColoredManaCost(ColoredManaSymbol.lookup(symbol.charAt(0))));
						else
							this.add(new VariableManaCost());
						//TODO: handle multiple {X} and/or {Y} symbols
					}
				}
				else {
					if (Character.isDigit(symbol.charAt(0))) {
						this.add(new MonoHybridManaCost(ColoredManaSymbol.lookup(symbol.charAt(2))));
					}
					else {
						this.add(new HybridManaCost(ColoredManaSymbol.lookup(symbol.charAt(0)), ColoredManaSymbol.lookup(symbol.charAt(2))));
					}
				}
			}
		}
	}

	public List<String> getSymbols() {
		List<String> symbols = new ArrayList<String>();
		for (ManaCost cost: this) {
			symbols.add(cost.getText());
		}
		return symbols;
	}
	
	@Override
	public String getText() {
		if (this.size() == 0)
			return "";

		StringBuilder sbText = new StringBuilder();
		for (ManaCost cost: this) {
			sbText.append(cost.getText());
		}
		return sbText.toString();
	}

	public ManaOptions getOptions() {
		ManaOptions options = new ManaOptions();
		for (ManaCost cost: this) {
			options.addMana(cost.getOptions());
		}
		return options;
	}

	public boolean testPay(Mana testMana) {
		for (ManaCost cost: this) {
			if (cost.testPay(testMana))
				return true;
		}
		return false;
	}

}
