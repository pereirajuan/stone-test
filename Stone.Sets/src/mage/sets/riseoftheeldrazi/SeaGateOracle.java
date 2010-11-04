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
import mage.Constants.CardType;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.filter.FilterCard;
import mage.game.Game;
import mage.players.Player;
import mage.target.TargetCard;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class SeaGateOracle extends CardImpl<SeaGateOracle> {

	public SeaGateOracle(UUID ownerId) {
		super(ownerId, 85, "Sea Gate Oracle", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{2}{U}");
		this.expansionSetCode = "ROE";
		this.color.setBlue(true);
		this.subtype.add("Human");
		this.subtype.add("Wizard");
		this.power = new MageInt(1);
		this.toughness = new MageInt(1);
		this.addAbility(new EntersBattlefieldTriggeredAbility(new SeaGateOracleEffect(), false));
	}


	public SeaGateOracle(final SeaGateOracle card) {
		super(card);
	}

	@Override
	public SeaGateOracle copy() {
		return new SeaGateOracle(this);
	}

	@Override
	public String getArt() {
		return "127435_typ_reg_sty_010.jpg";
	}
}

class SeaGateOracleEffect extends OneShotEffect<SeaGateOracleEffect> {

	private static FilterCard filter = new FilterCard("card to put in hand");

	public SeaGateOracleEffect() {
		super(Outcome.DrawCard);
	}

	public SeaGateOracleEffect(SeaGateOracleEffect effect) {
		super(effect);
	}

	@Override
	public boolean apply(Game game, Ability source) {
		Player player = game.getPlayer(source.getControllerId());
		if (player.getLibrary().size() > 0) {
			if (player.getLibrary().size() == 1) {
				Card card = player.getLibrary().removeFromTop(game);
				card.moveToZone(Zone.HAND, game, false);
//				player.putInHand(player.getLibrary().removeFromTop(game), game);
			}
			else {
				Cards cards = new CardsImpl(Zone.PICK);
				cards.add(player.getLibrary().removeFromTop(game));
				cards.add(player.getLibrary().removeFromTop(game));
				TargetCard target = new TargetCard(Zone.PICK, filter);
				target.setRequired(true);
				player.lookAtCards(cards, game);
				player.chooseTarget(cards, target, source, game);
				Card card = cards.get(target.getFirstTarget(), game);
				card.moveToZone(Zone.HAND, game, false);
//				player.putInHand(cards.get(target.getFirstTarget(), game), game);
				for (Card card1: cards.getCards(game)) {
					card1.moveToZone(Zone.LIBRARY, game, false);
//					player.getLibrary().putOnBottom(cards.get(cardId, game), game);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public SeaGateOracleEffect copy() {
		return new SeaGateOracleEffect(this);
	}

	@Override
	public String getText(Ability source) {
		return "look at the top two cards of your library. Put one of them into your hand and the other on the bottom of your library";
	}

}