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
package mage.sets.mercadianmasques;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.filter.common.FilterCreatureCard;
import mage.cards.Card;
import mage.constants.Outcome;
import mage.game.Game;
import mage.game.permanent.token.ZombieToken;
import mage.target.common.TargetCardInYourGraveyard;

/**
 *
 * @author Skyler Sell
 */
public class MidnightRitual extends CardImpl {

    private final FilterCreatureCard filter = new FilterCreatureCard("creature card from your graveyard");
    
    public MidnightRitual(UUID ownerId) {
        super(ownerId, 146, "Midnight Ritual", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{X}{2}{B}");
        this.expansionSetCode = "MMQ";

        // Exile X target creature cards from your graveyard. 
        // For each creature card exiled this way, put a 2/2 black Zombie creature token onto the battlefield.
        this.getSpellAbility().addTarget(new TargetCardInYourGraveyard(filter));
        this.getSpellAbility().addEffect(new MidnightRitualEffect());
    }
    
    @Override
    public void adjustTargets(Ability ability, Game game)
    {
        if (ability instanceof SpellAbility)
        {
            ability.getTargets().clear();
            ability.addTarget(new TargetCardInYourGraveyard(ability.getManaCostsToPay().getX(), filter));
        }
    }

    public MidnightRitual(final MidnightRitual card) {
        super(card);
    }

    @Override
    public MidnightRitual copy() {
        return new MidnightRitual(this);
    }
}

class MidnightRitualEffect extends OneShotEffect {
    
    public MidnightRitualEffect() {
        super(Outcome.Neutral);
        this.staticText = "Exile X target creature cards from your graveyard. For each creature card exiled this way, put a 2/2 black Zombie creature token onto the battlefield";
    }
    
    public MidnightRitualEffect(final MidnightRitualEffect effect)
    {
        super(effect);
    }
    
    @Override
    public MidnightRitualEffect copy() {
        return new MidnightRitualEffect(this);
    }
    
    @Override
    public boolean apply(Game game, Ability source)
    {
        for (UUID targetId : this.getTargetPointer().getTargets(game, source)) {
            Card card = game.getCard(targetId);
            if (card != null) {
                if (card.moveToExile(null, null, source.getSourceId(), game)) {
                    new ZombieToken().putOntoBattlefield(1, game, source.getSourceId(), card.getOwnerId());
                }
            }
        }
        return true;
    }
}
