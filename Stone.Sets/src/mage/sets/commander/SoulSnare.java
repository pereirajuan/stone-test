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
package mage.sets.commander;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.SacrificeSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.common.FilterAttackingCreature;
import mage.game.Game;
import mage.game.combat.CombatGroup;
import mage.game.permanent.Permanent;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author LoneFox

 */
public class SoulSnare extends CardImpl {

    public SoulSnare(UUID ownerId) {
        super(ownerId, 32, "Soul Snare", Rarity.UNCOMMON, new CardType[]{CardType.ENCHANTMENT}, "{W}");
        this.expansionSetCode = "CMD";

        // {W}, Sacrifice Soul Snare: Exile target creature that's attacking you or a planeswalker you control.
        Effect effect = new ExileTargetEffect();
        effect.setText("Exile target creature that's attacking you or a planeswalker you control.");
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, effect, new ManaCostsImpl("{W}"));
        ability.addCost(new SacrificeSourceCost());
        ability.addTarget(new TargetCreaturePermanent(new SoulSnareFilter()));
        this.addAbility(ability);
    }

    public SoulSnare(final SoulSnare card) {
        super(card);
    }

    @Override
    public SoulSnare copy() {
        return new SoulSnare(this);
    }
}

class SoulSnareFilter extends FilterAttackingCreature {

    public SoulSnareFilter() {
        super("creature that's attacking you or a planeswalker you control");
    }


    public SoulSnareFilter(final SoulSnareFilter filter) {
        super(filter);
    }

    @Override
    public SoulSnareFilter copy() {
        return new SoulSnareFilter(this);
    }

    @Override
    public boolean match(Permanent permanent, UUID sourceId, UUID playerId, Game game) {
        if(!super.match(permanent, sourceId, playerId, game)) {
            return false;
        }

        for(CombatGroup group : game.getCombat().getGroups()) {
            for(UUID attacker : group.getAttackers()) {
                if(attacker.equals(permanent.getId())) {
                    UUID defenderId = group.getDefenderId();
                    if(defenderId.equals(playerId)) {
                        return true;
                    }
                    else {
                        Permanent planeswalker = game.getPermanent(defenderId);
                        if(planeswalker != null && planeswalker.getCardType().contains(CardType.PLANESWALKER)
                            && planeswalker.getControllerId().equals(playerId)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
