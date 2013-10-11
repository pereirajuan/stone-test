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

package mage.sets.tenth;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Rarity;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.EvasionAbility;
import mage.abilities.common.AttacksEachTurnStaticAbility;
import mage.abilities.effects.common.combat.CantBlockSourceEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class Juggernaut extends CardImpl<Juggernaut> {

    public Juggernaut(UUID ownerId) {
        super(ownerId, 328, "Juggernaut", Rarity.UNCOMMON, new CardType[]{CardType.ARTIFACT, CardType.CREATURE}, "{4}");
        this.expansionSetCode = "10E";
        this.subtype.add("Juggernaut");
        this.power = new MageInt(5);
        this.toughness = new MageInt(3);

        this.addAbility(new AttacksEachTurnStaticAbility());
        this.addAbility(new JuggernautAbility());
    }

    public Juggernaut(final Juggernaut card) {
        super(card);
    }

    @Override
    public Juggernaut copy() {
        return new Juggernaut(this);
    }

}

class JuggernautAbility extends EvasionAbility<JuggernautAbility> {

    public JuggernautAbility() {
        this.addEffect(new JuggernautEffect());
    }

    public JuggernautAbility(final JuggernautAbility ability) {
        super(ability);
    }

    @Override
    public String getRule() {
        return "{this} can't be blocked by Walls.";
    }

    @Override
    public JuggernautAbility copy() {
        return new JuggernautAbility(this);
    }

}

class JuggernautEffect extends CantBlockSourceEffect {

    public JuggernautEffect() {
        super(Duration.WhileOnBattlefield);
    }

    public JuggernautEffect(final JuggernautEffect effect) {
        super(effect);
    }

    @Override
    public boolean canBeBlocked(Permanent attacker, Permanent blocker, Ability source, Game game) {
        return !blocker.hasSubtype("Wall");
    }

    @Override
    public JuggernautEffect copy() {
        return new JuggernautEffect(this);
    }

}
