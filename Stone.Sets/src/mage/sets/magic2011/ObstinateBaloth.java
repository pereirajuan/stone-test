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
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.effects.common.GainLifeEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.events.ZoneChangeEvent;
import mage.game.stack.StackObject;
import mage.players.Player;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ObstinateBaloth extends CardImpl {

    public ObstinateBaloth(UUID ownerId) {
        super(ownerId, 188, "Obstinate Baloth", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{2}{G}{G}");
        this.expansionSetCode = "M11";
        this.subtype.add("Beast");

        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // When Obstinate Baloth enters the battlefield, you gain 4 life.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new GainLifeEffect(4), false));
        
        // If a spell or ability an opponent controls causes you to discard Obstinate Baloth, put it onto the battlefield instead of putting it into your graveyard.
        this.addAbility(new SimpleStaticAbility(Zone.HAND, new ObstinateBalothEffect()));
    }

    public ObstinateBaloth(final ObstinateBaloth card) {
        super(card);
    }

    @Override
    public ObstinateBaloth copy() {
        return new ObstinateBaloth(this);
    }

}

class ObstinateBalothEffect extends ReplacementEffectImpl {

    public ObstinateBalothEffect() {
        super(Duration.EndOfGame, Outcome.PutCardInPlay);
        staticText = "If a spell or ability an opponent controls causes you to discard {this}, put it onto the battlefield instead of putting it into your graveyard";
    }

    public ObstinateBalothEffect(final ObstinateBalothEffect effect) {
        super(effect);
    }

    @Override
    public ObstinateBalothEffect copy() {
        return new ObstinateBalothEffect(this);
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (event.getType() == EventType.ZONE_CHANGE && event.getTargetId().equals(source.getSourceId())) {
            ZoneChangeEvent zcEvent = (ZoneChangeEvent) event;
            if (zcEvent.getFromZone() == Zone.HAND && zcEvent.getToZone() == Zone.GRAVEYARD) {
                StackObject spell = game.getStack().getStackObject(event.getSourceId());
                if (spell != null && game.getOpponents(source.getControllerId()).contains(spell.getControllerId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card card = game.getCard(source.getSourceId());
        if (card != null) {
            Player owner = game.getPlayer(card.getOwnerId());
            if (owner != null) {                
                if (owner.putOntoBattlefieldWithInfo(card, game, Zone.HAND, source.getSourceId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        return apply(game, source);
    }

}