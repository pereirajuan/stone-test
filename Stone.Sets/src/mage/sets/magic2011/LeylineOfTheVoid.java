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
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.keyword.LeylineAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.Permanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class LeylineOfTheVoid extends CardImpl<LeylineOfTheVoid> {

    public LeylineOfTheVoid(UUID ownerId) {
        super(ownerId, 101, "Leyline of the Void", Rarity.RARE, new CardType[]{CardType.ENCHANTMENT}, "{2}{B}{B}");
        this.expansionSetCode = "M11";
        this.color.setBlack(true);
        this.addAbility(LeylineAbility.getInstance());
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new LeylineOfTheVoidEffect()));
    }

    public LeylineOfTheVoid(final LeylineOfTheVoid card) {
        super(card);
    }

    @Override
    public LeylineOfTheVoid copy() {
        return new LeylineOfTheVoid(this);
    }

}

class LeylineOfTheVoidEffect extends ReplacementEffectImpl<LeylineOfTheVoidEffect> {

    public LeylineOfTheVoidEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "If a card would be put into an opponent's graveyard from anywhere, exile it instead";
    }

    public LeylineOfTheVoidEffect(final LeylineOfTheVoidEffect effect) {
        super(effect);
    }

    @Override
    public LeylineOfTheVoidEffect copy() {
        return new LeylineOfTheVoidEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        if (((ZoneChangeEvent)event).getFromZone() == Zone.BATTLEFIELD) {
            Permanent permanent = ((ZoneChangeEvent)event).getTarget();
            if (permanent != null) {
                return permanent.moveToExile(null, "", source.getId(), game);
            }
        }
        else {
            Card card = game.getCard(event.getTargetId());
            if (card != null) {
                return card.moveToExile(null, "", source.getId(), game);
            }
        }
        return false;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (event.getType() == EventType.ZONE_CHANGE && ((ZoneChangeEvent)event).getToZone() == Zone.GRAVEYARD) {
            if (game.getOpponents(source.getControllerId()).contains(event.getPlayerId())) {
                return true;
            }
        }
        return false;
    }

}
