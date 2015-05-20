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

package mage.sets.returntoravnica;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.CantGainLifeAllEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author LevelX2
 */
public class HavocFestival extends CardImpl {

    static final String rule = "Mana Bloom enters the battlefield with X charge counters on it";

    public HavocFestival (UUID ownerId) {
        super(ownerId, 166, "Havoc Festival", Rarity.RARE, new CardType[]{CardType.ENCHANTMENT}, "{4}{B}{R}");
        this.expansionSetCode = "RTR";


        // Players can't gain life.
       this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new CantGainLifeAllEffect()));

        // At the beginning of each player's upkeep, that player loses half his or her life, rounded up.
        Ability ability = new BeginningOfUpkeepTriggeredAbility(Zone.BATTLEFIELD, new HavocFestivalLoseLifeEffect(), TargetController.ANY, false);

        this.addAbility(ability);

    }

    public HavocFestival (final HavocFestival card) {
        super(card);
    }

    @Override
    public HavocFestival copy() {
        return new HavocFestival(this);
    }
}

class HavocFestivalLoseLifeEffect extends OneShotEffect {

    public HavocFestivalLoseLifeEffect() {
        super(Outcome.Damage);
        staticText = "that player loses half his or her life, rounded up";
    }

    public HavocFestivalLoseLifeEffect(final HavocFestivalLoseLifeEffect effect) {
        super(effect);
    }

    @Override
    public HavocFestivalLoseLifeEffect copy() {
        return new HavocFestivalLoseLifeEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(targetPointer.getFirst(game, source));
        if (player != null) {
            Integer amount = (int) Math.ceil(player.getLife() / 2f);
            if (amount > 0) {
                player.loseLife(amount, game);
                return true;
            }
        }
        return false;
    }
}
