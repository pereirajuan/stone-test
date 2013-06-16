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

package mage.sets.shardsofalara;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.MageInt;
import mage.abilities.common.ZoneChangeTriggeredAbility;
import mage.abilities.effects.common.search.SearchLibraryPutInPlayEffect;
import mage.abilities.keyword.FirstStrikeAbility;
import mage.cards.CardImpl;
import mage.filter.FilterCard;
import mage.filter.common.FilterLandPermanent;
import mage.filter.predicate.mageobject.NamePredicate;
import mage.game.Game;
import mage.target.common.TargetCardInLibrary;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class KnightOfTheWhiteOrchid extends CardImpl<KnightOfTheWhiteOrchid> {

    public KnightOfTheWhiteOrchid(UUID ownerId) {
        super(ownerId, 16, "Knight of the White Orchid", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{W}{W}");
        this.expansionSetCode = "ALA";
        this.subtype.add("Human");
        this.subtype.add("Knight");
        this.color.setWhite(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        this.addAbility(new KnightOfTheWhiteOrchidAbility());
        this.addAbility(FirstStrikeAbility.getInstance());
    }

    public KnightOfTheWhiteOrchid(final KnightOfTheWhiteOrchid card) {
        super(card);
    }

    @Override
    public KnightOfTheWhiteOrchid copy() {
        return new KnightOfTheWhiteOrchid(this);
    }

}

class KnightOfTheWhiteOrchidAbility extends ZoneChangeTriggeredAbility<KnightOfTheWhiteOrchidAbility> {

    private static final FilterCard filter1 = new FilterCard("Plains");
    private static final FilterLandPermanent filter2 = new FilterLandPermanent();

    static {
        filter1.add(new NamePredicate("Plains"));
    }

    public KnightOfTheWhiteOrchidAbility() {
        super(Zone.BATTLEFIELD, null, "When {this} enters the battlefield, if an opponent controls more lands than you, you may ", true);
        TargetCardInLibrary target = new TargetCardInLibrary(filter1);
        addEffect(new SearchLibraryPutInPlayEffect(target, false, Outcome.PutLandInPlay));
    }

    public KnightOfTheWhiteOrchidAbility(final KnightOfTheWhiteOrchidAbility ability) {
        super(ability);
    }

    @Override
    public KnightOfTheWhiteOrchidAbility copy() {
        return new KnightOfTheWhiteOrchidAbility(this);
    }

    @Override
    public boolean checkInterveningIfClause(Game game) {
        int numLands = game.getBattlefield().countAll(filter2, this.controllerId, game);
        for (UUID opponentId: game.getOpponents(this.controllerId)) {
            if (numLands < game.getBattlefield().countAll(filter2, opponentId, game)) {
                return true;
            }
        }
        return false;
    }

}