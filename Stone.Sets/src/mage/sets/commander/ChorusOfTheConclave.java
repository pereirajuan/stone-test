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
package mage.sets.commander;

import java.util.UUID;
import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.Cost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.keyword.ForestwalkAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.players.Player;

/**
 *
 * @author jeffwadsworth
 *
 */
public class ChorusOfTheConclave extends CardImpl {

    public ChorusOfTheConclave(UUID ownerId) {
        super(ownerId, 189, "Chorus of the Conclave", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}");
        this.expansionSetCode = "CMD";
        this.supertype.add("Legendary");
        this.subtype.add("Dryad");

        this.color.setGreen(true);
        this.color.setWhite(true);
        this.power = new MageInt(3);
        this.toughness = new MageInt(8);

        // Forestwalk
        this.addAbility(new ForestwalkAbility());

        // As an additional cost to cast creature spells, you may pay any amount of mana. If you do, that creature enters the battlefield with that many additional +1/+1 counters on it.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new ChorusOfTheConclaveReplacementEffect()));

    }

    public ChorusOfTheConclave(final ChorusOfTheConclave card) {
        super(card);
    }

    @Override
    public ChorusOfTheConclave copy() {
        return new ChorusOfTheConclave(this);
    }
}

class ChorusOfTheConclaveReplacementEffect extends ReplacementEffectImpl {

    public ChorusOfTheConclaveReplacementEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "As an additional cost to cast creature spells, you may pay any amount of mana. If you do, that creature enters the battlefield with that many additional +1/+1 counters on it";
    }

    public ChorusOfTheConclaveReplacementEffect(final ChorusOfTheConclaveReplacementEffect effect) {
        super(effect);
    }

    @Override
    public ChorusOfTheConclaveReplacementEffect copy() {
        return new ChorusOfTheConclaveReplacementEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        int xCost = 0;
        Player you = game.getPlayer(source.getControllerId());
        MageObject object = game.getObject(event.getSourceId());
        if (you != null && object != null) {
            if (you.chooseUse(Outcome.Benefit, "Do you wish to pay the additonal cost to add +1/+1 counters to the creature you cast?", game)) {
                Card card = (Card) object;
                xCost += playerPaysXGenericMana(you, source, game);
                if (xCost > 0) {
                    Ability ability = new EntersBattlefieldAbility(new AddCountersSourceEffect(CounterType.P1P1.createInstance(xCost)));
                    ability.setRuleVisible(false);
                    card.addAbility(ability);
                    ability.setControllerId(source.getControllerId());
                    ability.setSourceId(card.getId());
                    game.getState().addAbility(ability, source.getSourceId(), card);
                }
            }
        }
        return false;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if ((event.getType() == GameEvent.EventType.CAST_SPELL)
                && event.getPlayerId() == source.getControllerId()) {
            MageObject spellObject = game.getObject(event.getSourceId());
            if (spellObject != null) {
                return spellObject.getCardType().contains(CardType.CREATURE);
            }
        }
        return false;
    }

    protected static int playerPaysXGenericMana(Player player, Ability source, Game game) {
        int xValue = 0;
        boolean payed = false;
        while (!payed) {
            xValue = player.announceXMana(0, Integer.MAX_VALUE, "How much mana will you pay?", game, source);
            if (xValue > 0) {
                Cost cost = new GenericManaCost(xValue);
                payed = cost.pay(source, game, source.getSourceId(), player.getId(), false);
            } else {
                payed = true;
            }
        }
        game.informPlayers(new StringBuilder(player.getName()).append(" pays {").append(xValue).append("}.").toString());
        return xValue;
    }

}