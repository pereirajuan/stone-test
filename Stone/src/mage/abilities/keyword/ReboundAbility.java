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

package mage.abilities.keyword;

import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.SpellAbility;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.condition.common.MyTurnCondition;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.cards.Card;
import mage.game.ExileZone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.events.ZoneChangeEvent;
import mage.game.stack.Spell;
import mage.game.stack.StackObject;
import mage.players.Player;

import java.util.UUID;

/**
 * This ability has no effect by default and will always return false on the call
 * to apply.  This is because of how the {@link ReboundEffect} works.  It will
 * install the effect if and only if the spell was cast from the {@link Zone#HAND Hand}.
 * <p/>
 * 702.85. Rebound
 * <p/>
 * 702.85a Rebound appears on some instants and sorceries. It represents a static
 * ability that functions while the spell is on the stack and may create a delayed
 * triggered ability. "Rebound" means "If this spell was cast from your hand,
 * instead of putting it into your graveyard as it resolves, exile it and, at
 * the beginning of your next upkeep, you may cast this card from exile without
 * paying its mana cost."
 * <p/>
 * 702.85b Casting a card without paying its mana cost as the result of a rebound
 * ability follows the rules for paying alternative costs in rules 601.2b and 601.2e-g.
 * <p/>
 * 702.85c Multiple instances of rebound on the same spell are redundant.
 *
 * @author maurer.it_at_gmail.com, noxx
 */
public class ReboundAbility extends TriggeredAbilityImpl {
    //20101001 - 702.85
    private boolean installReboundEffect;
    private static String reboundText = "Rebound <i>(If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)</i>";

    public ReboundAbility() {
        super(Zone.STACK, null);
        this.installReboundEffect = false;
    }

    public ReboundAbility(final ReboundAbility ability) {
        super(ability);
        this.installReboundEffect = ability.installReboundEffect;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        //Something hit the stack from the hand, see if its a spell with this ability.
        if (event.getType() == EventType.ZONE_CHANGE &&
                ((ZoneChangeEvent) event).getFromZone() == Zone.HAND &&
                ((ZoneChangeEvent) event).getToZone() == Zone.STACK) {
            Card card = (Card) game.getObject(event.getTargetId());

            if (card.getAbilities(game).contains(this)) {
                this.installReboundEffect = true;
            }
        }

        //Only 'install' the effect on a successfully cast spell otherwise the user
        //may cancel before paying its costs and potentially having two copies rebound
        if (event.getType() == EventType.SPELL_CAST && this.installReboundEffect) {
            Spell spell = game.getStack().getSpell(event.getTargetId());
            if (spell != null && spell.getSourceId().equals(this.getSourceId())) {
                addReboundEffectToSpellIfMissing(spell);
                this.installReboundEffect = false;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return reboundText;
    }

    @Override
    public ReboundAbility copy() {
        return new ReboundAbility(this);
    }
    
    static public void addReboundEffectToSpellIfMissing(Spell spell) {
        Effect reboundEffect = new ReboundEffect();
        boolean found = false;
        for (Effect effect : spell.getSpellAbility().getEffects()) {
            if (effect instanceof ReboundEffect) {
                found = true;
                break;
            }
        }
        if (!found) {
            spell.getSpellAbility().addEffect(reboundEffect);
        }        
    }
}

/**
 * Upon successful resolution of a spell with the {@link ReboundAbility} this effect
 * will setup a {@link ReboundCastFromHandReplacementEffect replacement effect} which
 * will only work once.  It will then setup a {@link ReboundEffectCastFromExileDelayedTrigger delayed trigger}
 * which will fire upon the controllers next upkeep.
 *
 * @author maurer.it_at_gmail.com
 */
class ReboundEffect extends OneShotEffect {

    public ReboundEffect() {
        super(Outcome.Benefit);
    }

    public ReboundEffect(ReboundEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Spell sourceSpell = (Spell) game.getObject(source.getId());
        if (sourceSpell == null || !sourceSpell.isCopiedSpell()) {
            MageObject mageObject = game.getObject(source.getSourceId());
            if (mageObject instanceof StackObject) {
                StackObject sourceCard = (StackObject) mageObject;
                ReboundEffectCastFromExileDelayedTrigger trigger = new ReboundEffectCastFromExileDelayedTrigger(sourceCard.getSourceId(), sourceCard.getSourceId());
                trigger.setControllerId(source.getControllerId());
                game.addDelayedTriggeredAbility(trigger);

                game.getContinuousEffects().addEffect(new ReboundCastFromHandReplacementEffect(source.getSourceId()), source);
                return true;
            }
        }
        return false;
    }

    @Override
    public ReboundEffect copy() {
        return new ReboundEffect(this);
    }
}

/**
 * This replacement effect needs to be created only when the spell with rebound
 * successfully resolves.  This will help to ensure that interactions with Leyline of the Void
 * or any other such effect will not get in the way.  This should take precendence
 * in such interactions.
 *
 * @author maurer.it_at_gmail.com
 */
class ReboundCastFromHandReplacementEffect extends ReplacementEffectImpl {

    private static String replacementText = "Rebound - If you cast {this} from your hand, exile it as it resolves";
    private UUID cardId;

    ReboundCastFromHandReplacementEffect(UUID cardId) {
        super(Duration.OneUse, Outcome.Exile);
        this.cardId = cardId;
        this.staticText = replacementText;
    }

    ReboundCastFromHandReplacementEffect(ReboundCastFromHandReplacementEffect effect) {
        super(effect);
        this.cardId = effect.cardId;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public ReboundCastFromHandReplacementEffect copy() {
        return new ReboundCastFromHandReplacementEffect(this);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Spell sourceSpell = (Spell) game.getObject(source.getId());
        if (sourceSpell != null && sourceSpell.isCopiedSpell()) {
            return false;
        } else {
            Card sourceCard = (Card) game.getObject(source.getSourceId());
            Player player = game.getPlayer(sourceCard.getOwnerId());
            if (player != null) {
                player.moveCardToExileWithInfo(sourceCard, this.cardId, new StringBuilder(player.getName()).append(" Rebound").toString(), source.getSourceId(), game, Zone.HAND, true);
                this.used = true;
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ZONE_CHANGE;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (((ZoneChangeEvent) event).getFromZone() == Zone.STACK &&
                ((ZoneChangeEvent) event).getToZone() == Zone.GRAVEYARD &&
                source.getSourceId() == this.cardId) {
            return true;
        }
        return false;
    }

}

/**
 * This delayed trigger will tell the framework when its ok to kick off the
 * {@link ReboundCastSpellFromExileEffect} by checking the step and the controller
 * of this ability.  When it is the controllers upkeep step the framework will then
 * be told it can kick off the effect.
 *
 * @author maurer.it_at_gmail.com
 */
class ReboundEffectCastFromExileDelayedTrigger extends DelayedTriggeredAbility {

    ReboundEffectCastFromExileDelayedTrigger(UUID cardId, UUID sourceId) {
        super(new ReboundCastSpellFromExileEffect(cardId));
        setSourceId(sourceId);
        this.optional = true;
    }

    ReboundEffectCastFromExileDelayedTrigger(ReboundEffectCastFromExileDelayedTrigger ability) {
        super(ability);
    }

    @Override
    public ReboundEffectCastFromExileDelayedTrigger copy() {
        return new ReboundEffectCastFromExileDelayedTrigger(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.UPKEEP_STEP_PRE;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        return MyTurnCondition.getInstance().apply(game, this);
    }
    @Override
    public String getRule() {
        return "Rebound - You may cast {this} from exile without paying its mana cost.";
    }
}

/**
 * Will be triggered by {@link ReboundEffectCastFromExileDelayedTrigger} and will
 * simply cast the spell then remove it from its former home in exile.
 *
 * @author maurer.it_at_gmail.com
 */
class ReboundCastSpellFromExileEffect extends OneShotEffect {

    private static String castFromExileText = "Rebound - You may cast {this} from exile without paying its mana cost";
    private final UUID cardId;

    ReboundCastSpellFromExileEffect(UUID cardId) {
        super(Outcome.Benefit);
        this.cardId = cardId;
        staticText = castFromExileText;
    }

    ReboundCastSpellFromExileEffect(ReboundCastSpellFromExileEffect effect) {
        super(effect);
        this.cardId = effect.cardId;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        ExileZone zone = game.getExile().getExileZone(this.cardId);
        if (zone == null || zone.isEmpty()) {
            return false;
        }
        Card reboundCard = zone.get(this.cardId, game);
        Player player = game.getPlayer(source.getControllerId());
        if (player != null && reboundCard != null) {
            SpellAbility ability = reboundCard.getSpellAbility();
            player.cast(ability, game, true);
            zone.remove(reboundCard.getId());
            return true;
        }
        return false;
    }

    @Override
    public ReboundCastSpellFromExileEffect copy() {
        return new ReboundCastSpellFromExileEffect(this);
    }

}
