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

import mage.constants.*;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.players.Player;

/**
 *
 * @author Loki
 */
public class WindbornMuse extends CardImpl {

    public WindbornMuse(UUID ownerId) {
        super(ownerId, 60, "Windborn Muse", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{3}{W}");
        this.expansionSetCode = "10E";
        this.subtype.add("Spirit");
        this.power = new MageInt(2);
        this.toughness = new MageInt(3);
        
        // Flying
        this.addAbility(FlyingAbility.getInstance());
        // Creatures can't attack you unless their controller pays {2} for each creature he or she controls that's attacking you.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new WindbornMuseReplacementEffect()));
        
    }

    public WindbornMuse(final WindbornMuse card) {
        super(card);
    }

    @Override
    public WindbornMuse copy() {
        return new WindbornMuse(this);
    }
}

class WindbornMuseReplacementEffect extends ReplacementEffectImpl {

    private static final String effectText = "Creatures can't attack you unless their controller pays {2} for each creature he or she controls that's attacking you";

    WindbornMuseReplacementEffect ( ) {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = effectText;
    }

    WindbornMuseReplacementEffect ( WindbornMuseReplacementEffect effect ) {
        super(effect);
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.DECLARE_ATTACKER;
    }
    
    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (event.getTargetId().equals(source.getControllerId()) ) {
            Player attackedPlayer = game.getPlayer(event.getTargetId());
            if (attackedPlayer != null) {
                // only if a player is attacked. Attacking a planeswalker is free
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Player player = game.getPlayer(event.getPlayerId());
        if ( player != null && event.getTargetId().equals(source.getControllerId())) {
            ManaCostsImpl attackTax = new ManaCostsImpl("{2}");
            if ( attackTax.canPay(source, source.getSourceId(), event.getPlayerId(), game) &&
                 player.chooseUse(Outcome.Benefit, "Pay {2} to attack player?", game) )
            {
                if (attackTax.payOrRollback(source, game, this.getId(), event.getPlayerId())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public WindbornMuseReplacementEffect copy() {
        return new WindbornMuseReplacementEffect(this);
    }

}

