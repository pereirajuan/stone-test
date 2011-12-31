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
package mage.sets.championsofkamigawa;

/**
 *
 * @author LevelX
 */


import java.util.UUID;


import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.ActivateAsSorceryActivatedAbility;
import mage.abilities.costs.common.SacrificeTargetCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.abilities.keyword.SoulshiftAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.filter.Filter;
import mage.filter.FilterCard;
import mage.filter.common.FilterControlledPermanent;
import mage.game.Game;
import mage.players.Player;
import mage.target.TargetCard;
import mage.target.common.TargetControlledPermanent;
import mage.target.common.TargetOpponent;

/**
 * @author LevelX
 */
public class HeWhoHungers extends CardImpl<HeWhoHungers> {

    private final static FilterControlledPermanent filter = new FilterControlledPermanent("a Spirit");

    static {
        filter.getSubtype().add("Spirit");
        filter.setScopeSubtype(Filter.ComparisonScope.Any);
    }
    
    public HeWhoHungers(UUID ownerId) {
        super(ownerId, 114, "He Who Hungers", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{4}{B}");
        this.expansionSetCode = "CHK";
        this.supertype.add("Legendary");
        this.subtype.add("Spirit");
        this.color.setBlack(true);
        this.power = new MageInt(3);
        this.toughness = new MageInt(2);
        
        //Flying
        this.addAbility(FlyingAbility.getInstance());
        
        /* {1}, Sacrifice a Spirit: Target opponent reveals his or her hand. You choose a card from it. 
         * That player discards that card. Activate this ability only any time you could cast a sorcery. */
        Ability ability = new ActivateAsSorceryActivatedAbility(Zone.BATTLEFIELD, new HeWhoHungersEffect(), new ManaCostsImpl("{1}"));
        ability.addTarget(new TargetOpponent());
        ability.addCost(new SacrificeTargetCost(new TargetControlledPermanent(filter)));
        this.addAbility(ability);
        
        //Soulshift 4 (When this creature dies, you may return target Spirit card with converted mana cost 4 or less from your graveyard to your hand.)
        this.addAbility(new SoulshiftAbility(4));
    }

    public HeWhoHungers(final HeWhoHungers card) {
        super(card);
    }

    @Override
    public HeWhoHungers copy() {
        return new HeWhoHungers(this);
    }

}

class HeWhoHungersEffect extends OneShotEffect<HeWhoHungersEffect> {

	public HeWhoHungersEffect() {
		super(Constants.Outcome.Discard);
		staticText = "Target opponent reveals his or her hand. You choose a card from it. That player discards that card";
	}

	public HeWhoHungersEffect(final HeWhoHungersEffect effect) {
		super(effect);
	}

	@Override
	public boolean apply(Game game, Ability source) {
		Player player = game.getPlayer(source.getFirstTarget());
		if (player != null) {
			player.revealCards("He Who Hungers", player.getHand(), game);
			Player you = game.getPlayer(source.getControllerId());
			if (you != null) {
				TargetCard target = new TargetCard(Constants.Zone.PICK, new FilterCard());
                                target.setRequired(true);
				if (you.choose(Constants.Outcome.Benefit, player.getHand(), target, game)) {
					Card card = player.getHand().get(target.getFirstTarget(), game);
					if (card != null) {
						return player.discard(card, source, game);
					}
				}
			}
		}
		return false;
	}

	@Override
	public HeWhoHungersEffect copy() {
		return new HeWhoHungersEffect(this);
	}

}