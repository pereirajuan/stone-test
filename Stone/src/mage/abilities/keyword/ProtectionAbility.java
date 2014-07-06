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

package mage.abilities.keyword;

import mage.MageObject;
import mage.abilities.StaticAbility;
import mage.cards.Card;
import mage.constants.Zone;
import mage.filter.Filter;
import mage.filter.FilterCard;
import mage.filter.FilterObject;
import mage.filter.FilterPermanent;
import mage.filter.FilterSpell;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.stack.Spell;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ProtectionAbility extends StaticAbility {

    protected Filter filter;
    protected boolean removeAuras;

    public ProtectionAbility(Filter filter) {
        super(Zone.BATTLEFIELD, null);
        this.filter = filter;
        this.removeAuras = true;
    }

    public ProtectionAbility(final ProtectionAbility ability) {
        super(ability);
        this.filter = ability.filter.copy();
        this.removeAuras = ability.removeAuras;
    }

    @Override
    public ProtectionAbility copy() {
        return new ProtectionAbility(this);
    }

    @Override
    public String getRule() {
        return "Protection from " + filter.getMessage() + (removeAuras ? "":". This effect doesn't remove auras.");
    }

    public boolean canTarget(MageObject source, Game game) {
        if (filter instanceof FilterPermanent) {
            if (source instanceof Permanent) {
                return !filter.match(source, game);
            }
            return true;
        }
        if (filter instanceof FilterSpell) {
            if (source instanceof Spell) {
                return !filter.match(source, game);
            }
            return true;
        }
        if (filter instanceof FilterCard) {
            if (source instanceof Card) {
                return !filter.match(source, game);
            }
            return true;
        }
        if (filter instanceof FilterObject) {
            return !filter.match(source, game);
        }
        return true;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(FilterCard filter) {
        this.filter = filter;
    }

    public void setRemovesAuras(boolean removeAuras) {
        this.removeAuras = removeAuras;
    }

    public boolean removesAuras() {
        return removeAuras;
    }
}
