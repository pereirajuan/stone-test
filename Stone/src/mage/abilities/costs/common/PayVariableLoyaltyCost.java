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

package mage.abilities.costs.common;

import java.util.UUID;

import mage.abilities.Ability;
import mage.abilities.costs.CostImpl;
import mage.abilities.costs.VariableCost;
import mage.counters.CounterType;
import mage.filter.FilterMana;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class PayVariableLoyaltyCost extends CostImpl<PayVariableLoyaltyCost> implements VariableCost {

    protected int amountPaid = 0;

    public PayVariableLoyaltyCost() {
        this.text = "-X";
    }

    public PayVariableLoyaltyCost(final PayVariableLoyaltyCost cost) {
        super(cost);
        this.amountPaid = cost.amountPaid;
    }

    @Override
    public boolean canPay(UUID sourceId, UUID controllerId, Game game) {
        Permanent planeswalker = game.getPermanent(sourceId);
        return !planeswalker.isLoyaltyUsed();
    }

    @Override
    public boolean pay(Ability ability, Game game, UUID sourceId, UUID controllerId, boolean noMana) {
        Permanent planeswalker = game.getPermanent(sourceId);
        Player player = game.getPlayer(planeswalker.getControllerId());
        this.amountPaid = player.getAmount(0, planeswalker.getCounters().getCount(CounterType.LOYALTY), "Choose X", game);
        if (this.amountPaid> 0) {
            planeswalker.getCounters().removeCounter(CounterType.LOYALTY, this.amountPaid); 
        } else if (this.amountPaid < 0) {
            planeswalker.getCounters().addCounter(CounterType.LOYALTY.createInstance(Math.abs(this.amountPaid)));
        }
        planeswalker.setLoyaltyUsed(true);
        this.paid = true;
        return paid;
    }

    @Override
    public void clearPaid() {
        paid = false;
        amountPaid = 0;
    }

    @Override
    public int getAmount() {
        return amountPaid;
    }

    @Override
    public void setAmount(int amount) {
        amountPaid = amount;
    }

    /**
     * Not Supported
     * @param filter
     */
    @Override
    public void setFilter(FilterMana filter) {
    }

    /**
     * Not supported
     * @return
     */
    @Override
    public FilterMana getFilter() {
        return new FilterMana();
    }

    @Override
    public PayVariableLoyaltyCost copy() {
        return new PayVariableLoyaltyCost(this);
    }

}
