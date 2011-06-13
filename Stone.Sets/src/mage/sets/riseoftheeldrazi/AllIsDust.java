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

package mage.sets.riseoftheeldrazi;

import java.util.UUID;

import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.players.PlayerList;

/**
 * @author Loki
 */
public class AllIsDust extends CardImpl<AllIsDust> {

    public AllIsDust(UUID ownerId) {
        super(ownerId, 1, "All Is Dust", Rarity.MYTHIC, new CardType[]{CardType.SORCERY}, "{7}");
        this.expansionSetCode = "ROE";
        this.subtype.add("Tribal");
        this.subtype.add("Eldrazi");
        this.getSpellAbility().addEffect(new AllIsDustEffect());
    }

    public AllIsDust(final AllIsDust card) {
        super(card);
    }

    @Override
    public AllIsDust copy() {
        return new AllIsDust(this);
    }

}

class AllIsDustEffect extends OneShotEffect<AllIsDustEffect> {
    AllIsDustEffect() {
        super(Constants.Outcome.DestroyPermanent);
    }

    AllIsDustEffect(final AllIsDustEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        for (Permanent p : game.getBattlefield().getAllActivePermanents()) {
            if (!p.getColor().isColorless()) {
                p.sacrifice(source.getSourceId(), game);
            }
        }

        return true;
    }

    @Override
    public AllIsDustEffect copy() {
        return new AllIsDustEffect(this);
    }

    @Override
    public String getText(Ability source) {
        return "Each player sacrifices all colored permanents he or she controls";
    }
}