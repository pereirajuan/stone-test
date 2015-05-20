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
package mage.sets.betrayersofkamigawa;

import java.util.UUID;

import mage.constants.CardType;
import mage.constants.Rarity;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.cards.CardImpl;
import mage.constants.Outcome;
import mage.filter.FilterPermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetPermanent;

/**
 *
 * @author LevelX2
 */
public class TerashisGrasp extends CardImpl {

    private static final FilterPermanent filter = new FilterPermanent("artifact or enchantment");

    static {
        filter.add(Predicates.or(
                new CardTypePredicate(CardType.ARTIFACT),
                new CardTypePredicate(CardType.ENCHANTMENT)));
    }

    public TerashisGrasp(UUID ownerId) {
        super(ownerId, 26, "Terashi's Grasp", Rarity.COMMON, new CardType[]{CardType.SORCERY}, "{2}{W}");
        this.expansionSetCode = "BOK";
        this.subtype.add("Arcane");


        // Destroy target artifact or enchantment. 
        this.getSpellAbility().addTarget(new TargetPermanent(filter));
        this.getSpellAbility().addEffect(new DestroyTargetEffect());
        // You gain life equal to its converted mana cost.
        this.getSpellAbility().addEffect(new TerashisGraspEffect());
    }

    public TerashisGrasp(final TerashisGrasp card) {
        super(card);
    }

    @Override
    public TerashisGrasp copy() {
        return new TerashisGrasp(this);
    }

    private class TerashisGraspEffect extends OneShotEffect {

        public TerashisGraspEffect() {
            super(Outcome.DestroyPermanent);
            staticText = "You gain life equal to its converted mana cost";
        }

        public TerashisGraspEffect(TerashisGraspEffect effect) {
            super(effect);
        }

        @Override
        public boolean apply(Game game, Ability source) {
            Permanent targetPermanent = game.getPermanentOrLKIBattlefield(getTargetPointer().getFirst(game, source));
            if (targetPermanent != null) {
                int cost = targetPermanent.getManaCost().convertedManaCost();
                Player player = game.getPlayer(source.getControllerId());
                if (player != null) {
                    player.gainLife(cost, game);
                }
            }
            return true;
        }

        @Override
        public TerashisGraspEffect copy() {
            return new TerashisGraspEffect(this);
        }
    }
}
