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

package mage.sets.magic2011;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.ColoredManaSymbol;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.ActivatedAbilityImpl;
import mage.abilities.costs.CostImpl;
import mage.abilities.costs.mana.ColoredManaCost;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.permanent.ControllerIdPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class AncientHellkite extends CardImpl {

    public AncientHellkite(UUID ownerId) {
        super(ownerId, 122, "Ancient Hellkite", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{4}{R}{R}{R}");
        this.expansionSetCode = "M11";
        this.subtype.add("Dragon");

        this.power = new MageInt(6);
        this.toughness = new MageInt(6);

        this.addAbility(FlyingAbility.getInstance());
        this.addAbility(new AncientHellkiteAbility());
    }

    public AncientHellkite(final AncientHellkite card) {
        super(card);
    }

    @Override
    public AncientHellkite copy() {
        return new AncientHellkite(this);
    }

}

class AncientHellkiteAbility extends ActivatedAbilityImpl {

    private static final FilterCreaturePermanent filterTemplate = new FilterCreaturePermanent("creature defending player controls");

    public AncientHellkiteAbility() {
        super(Zone.BATTLEFIELD, new DamageTargetEffect(1));
        addCost(new AncientHellkiteCost());
        addManaCost(new ColoredManaCost(ColoredManaSymbol.R));
        addTarget(new TargetCreaturePermanent(filterTemplate));
    }

    public AncientHellkiteAbility(final AncientHellkiteAbility ability) {
        super(ability);
    }

    @Override
    public AncientHellkiteAbility copy() {
        return new AncientHellkiteAbility(this);
    }

    @Override
    public boolean activate(Game game, boolean noMana) {
        UUID defenderId = game.getCombat().getDefenderId(sourceId);
        if (defenderId != null) {
            FilterCreaturePermanent filter = filterTemplate.copy();
            filter.add(new ControllerIdPredicate(defenderId));

            this.getTargets().clear();
            TargetCreaturePermanent target = new TargetCreaturePermanent(filter);
            this.addTarget(target);
            return super.activate(game, noMana);
        }
        return false;
    }
}

class AncientHellkiteCost extends CostImpl {

    public AncientHellkiteCost() {
        this.text = "Activate this ability only if Ancient Hellkite is attacking";
    }

    public AncientHellkiteCost(final AncientHellkiteCost cost) {
        super(cost);
    }

    @Override
    public AncientHellkiteCost copy() {
        return new AncientHellkiteCost(this);
    }

    @Override
    public boolean canPay(Ability ability, UUID sourceId, UUID controllerId, Game game) {
        Permanent permanent = game.getPermanent(sourceId);
        if (permanent != null && permanent.isAttacking()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean pay(Ability ability, Game game, UUID sourceId, UUID controllerId, boolean noMana) {
        this.paid = true;
        return paid;
    }

}