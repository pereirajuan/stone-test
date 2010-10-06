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
import mage.Constants.CardType;
import mage.Constants.Outcome;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.Costs;
import mage.abilities.costs.CostsImpl;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ScryEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ElixirOfImmortality extends CardImpl<ElixirOfImmortality> {

	public ElixirOfImmortality(UUID ownerId) {
		super(ownerId, "Elixir Of Immortality", Rarity.UNCOMMON, new CardType[]{CardType.ARTIFACT}, "{1}");
		this.expansionSetCode = "M11";
		Costs costs = new CostsImpl();
		costs.add(new GenericManaCost(2));
		costs.add(new TapSourceCost());
		this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new ScryEffect(2), costs));
	}

	public ElixirOfImmortality(final ElixirOfImmortality card) {
		super(card);
	}

	@Override
	public ElixirOfImmortality copy() {
		return new ElixirOfImmortality(this);
	}

	@Override
	public String getArt() {
		return "129148_typ_reg_sty_010.jpg";
	}

}

class ElixerOfImmortalityEffect extends OneShotEffect<ElixerOfImmortalityEffect> {

	public ElixerOfImmortalityEffect() {
		super(Outcome.GainLife);
	}

	public ElixerOfImmortalityEffect(final ElixerOfImmortalityEffect effect) {
		super(effect);
	}

	@Override
	public boolean apply(Game game, Ability source) {
		Player player = game.getPlayer(source.getControllerId());
		player.gainLife(5, game);
		player.removeFromBattlefield(game.getPermanent(source.getSourceId()), game);
		Card card = game.getCard(source.getSourceId());
		card.moveToZone(Zone.LIBRARY, game, true);
//		player.getLibrary().putOnBottom(game.getCard(source.getSourceId()), game);
		player.getLibrary().addAll(player.getGraveyard().getCards(game));
		player.getGraveyard().clear();
		player.getLibrary().shuffle();
		return true;
	}

	@Override
	public ElixerOfImmortalityEffect copy() {
		return new ElixerOfImmortalityEffect(this);
	}

	@Override
	public String getText(Ability source) {
		return "Shuffle Elixir of Immortality and your graveyard into your library";
	}

}