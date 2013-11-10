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

package mage.abilities;

import java.util.UUID;

import mage.constants.AbilityType;
import mage.constants.SpellAbilityType;
import mage.constants.TimingRule;
import mage.constants.Zone;
import mage.MageObject;
import mage.abilities.costs.AlternativeSourceCosts;
import mage.abilities.costs.Cost;
import mage.abilities.costs.Costs;
import mage.abilities.costs.OptionalAdditionalSourceCosts;
import mage.abilities.costs.mana.ManaCosts;
import mage.abilities.costs.mana.PhyrexianManaCost;
import mage.abilities.effects.Effect;
import mage.abilities.effects.Effects;
import mage.cards.Card;
import mage.choices.Choice;
import mage.constants.TargetController;
import mage.game.Game;
import mage.game.stack.Spell;
import mage.game.stack.StackAbility;
import mage.target.Target;


/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public abstract class ActivatedAbilityImpl<T extends ActivatedAbilityImpl<T>> extends AbilityImpl<T> implements ActivatedAbility {

    protected TimingRule timing = TimingRule.INSTANT;
    protected TargetController mayActivate = TargetController.YOU;
    protected UUID activatorId;

    protected ActivatedAbilityImpl(AbilityType abilityType, Zone zone) {
        super(abilityType, zone);
    }

    public ActivatedAbilityImpl(ActivatedAbilityImpl ability) {
        super(ability);
        timing = ability.timing;
        mayActivate = ability.mayActivate;
        activatorId = ability.activatorId;
    }

    public ActivatedAbilityImpl(Zone zone) {
        this(zone, null);
    }

    public ActivatedAbilityImpl(Zone zone, Effect effect) {
        super(AbilityType.ACTIVATED, zone);
        if (effect != null) {
            this.addEffect(effect);
        }
    }

    public ActivatedAbilityImpl(Zone zone, Effect effect, ManaCosts cost) {
        super(AbilityType.ACTIVATED, zone);
        if (effect != null) {
            this.addEffect(effect);
        }
        if (cost != null) {
            this.addManaCost(cost);
        }
    }

    public ActivatedAbilityImpl(Zone zone, Effects effects, ManaCosts cost) {
        super(AbilityType.ACTIVATED, zone);
        if (effects != null) {
            for (Effect effect: effects) {
                this.addEffect(effect);
            }
        }
        if (cost != null) {
            this.addManaCost(cost);
        }
    }

    public ActivatedAbilityImpl(Zone zone, Effect effect, Cost cost) {
        super(AbilityType.ACTIVATED, zone);
        if (effect != null) {
            this.addEffect(effect);
        }
        if (cost != null) {
            if (cost instanceof PhyrexianManaCost) {
                this.addManaCost((PhyrexianManaCost)cost);
            } else {
                this.addCost(cost);
            }
        }
    }

    public ActivatedAbilityImpl(Zone zone, Effect effect, Costs<Cost> costs) {
        super(AbilityType.ACTIVATED, zone);
        if (effect != null) {
            this.addEffect(effect);
        }
        if (costs != null) {
            for (Cost cost: costs) {
                this.addCost(cost);
            }
        }
    }

    public ActivatedAbilityImpl(Zone zone, Effects effects, Cost cost) {
        super(AbilityType.ACTIVATED, zone);
        if (effects != null) {
            for (Effect effect: effects) {
                this.addEffect(effect);
            }
        }
        if (cost != null) {
            this.addCost(cost);
        }
    }

    public ActivatedAbilityImpl(Zone zone, Effects effects, Costs<Cost> costs) {
        super(AbilityType.ACTIVATED, zone);
        for (Effect effect: effects) {
            if (effect != null) {
                this.addEffect(effect);
            }
        }
        if (costs != null) {
            for (Cost cost: costs) {
                this.addCost(cost);
            }
        }
    }

    @Override
    public boolean canActivate(UUID playerId, Game game) {
        //20091005 - 602.2
        switch(mayActivate){
            case ANY:
                break;
                
            case NOT_YOU:
                if(controlsAbility(playerId, game)){
                    return false;
                }
                break;
                
            case OPPONENT:
                if(!game.getOpponents(controllerId).contains(playerId)){
                    return false;
                }
                break;
                
            case YOU:
                if(!controlsAbility(playerId, game)){
                    return false;
                }
                break;
        }
        //20091005 - 602.5d/602.5e
        if (timing == TimingRule.INSTANT || game.canPlaySorcery(playerId)) {
            if (costs.canPay(sourceId, controllerId, game) && canChooseTarget(game)) {
                this.activatorId = playerId;
                return true;
            }
        }
        return false;
    }

    protected boolean controlsAbility(UUID playerId, Game game) {
        if (this.controllerId != null && this.controllerId.equals(playerId)) {
            return true;
        }
        else {
            Card card = (Card)game.getObject(this.sourceId);
            if (card != null && game.getState().getZone(this.sourceId) != Zone.BATTLEFIELD) {
                return card.getOwnerId().equals(playerId);
            }
        }
        return false;
    }

    @Override
    public String getActivatedMessage(Game game) {
        if (game.isSimulation()) {
            return "";
        }
        MageObject object = game.getObject(this.sourceId);
        return new StringBuilder(" activates ")
                .append(object != null ? this.getRule(object.getName()) :this.getRule())
                .append(" from ")
                .append(getMessageText(game)).toString();
    }

    protected String getMessageText(Game game) {
        StringBuilder sb = new StringBuilder();
        MageObject object = game.getObject(this.sourceId);
        if (object == null) {
            object = game.getLastKnownInformation(this.sourceId, Zone.BATTLEFIELD);
        }
        if (object != null) {
            if (object instanceof StackAbility) {
                Card card = game.getCard(((StackAbility) object).getSourceId());
                if (card != null) {
                    sb.append(card.getName());
                } else {
                    sb.append(object.getName());
                }
            } else {
                if (object instanceof Spell) {
                    Spell<?> spell = (Spell<?>) object;
                    String castText = spell.getSpellAbility().toString();
                    sb.append((castText.startsWith("Cast ") ? castText.substring(5):castText));
                    if (spell.getFromZone() == Zone.GRAVEYARD) {
                        sb.append(" from graveyard");
                    }
                    sb.append(getOptionalTextSuffix(game, spell));
                } else {
                    sb.append(object.getName());
                }
            }
        } else {
            sb.append("unknown");
        }
        if (object instanceof Spell && ((Spell) object).getSpellAbilities().size() > 1) {
            if (((Spell) object).getSpellAbility().getSpellAbilityType().equals(SpellAbilityType.SPLIT_FUSED)) {
                Spell<?> spell = (Spell<?>) object;
                int i = 0;
                for (SpellAbility spellAbility : spell.getSpellAbilities()) {
                    i++;
                    String half;
                    if (i == 1) {
                        half = " left";
                    } else {
                        half = " right";
                    }
                    if (spellAbility.getTargets().size() > 0) {
                        sb.append(half).append(" half targeting ");
                        for (Target target: spellAbility.getTargets()) {
                            sb.append(target.getTargetedName(game));
                        }
                    }
                }
            } else {
                Spell<?> spell = (Spell<?>) object;
                int i = 0;
                for (SpellAbility spellAbility : spell.getSpellAbilities()) {
                    i++;
                    if ( i > 1) {
                        sb.append(" splicing ");
                        if (spellAbility.name.length() > 5 && spellAbility.name.startsWith("Cast ")) {
                            sb.append(spellAbility.name.substring(5));
                        } else {
                            sb.append(spellAbility.name);
                        }
                    }
                    if (spellAbility.getTargets().size() > 0) {
                        for (Target target: spellAbility.getTargets()) {
                            sb.append(" targeting ");
                            sb.append(target.getTargetedName(game));
                        }
                    }
                }
            }
        } else if (object instanceof Spell && ((Spell) object).getSpellAbility().getModes().size() > 1) {
            Modes spellModes = ((Spell) object).getSpellAbility().getModes();
            int item = 0;
            for (Mode mode : spellModes.values()) {
                item++;
                if (spellModes.getSelectedModes().contains(mode.getId())) {
                    spellModes.setMode(mode);
                    sb.append(" (mode ").append(item).append(")");
                    if (getTargets().size() > 0) {
                        sb.append(" targeting ");
                        for (Target target: getTargets()) {
                            sb.append(target.getTargetedName(game));
                        }
                    }
                }
            }
        } else {
            if (getTargets().size() > 0) {
                sb.append(" targeting ");
                for (Target target: getTargets()) {
                    sb.append(target.getTargetedName(game));
                }
            }
        }
        for (Choice choice :this.getChoices()) {
            sb.append(" - ").append(choice.getMessage()).append(": ").append(choice.getChoice());
        }
        return sb.toString();
    }

    String getOptionalTextSuffix(Game game, Spell spell) {
        StringBuilder sb = new StringBuilder();
        for (Ability ability : (Abilities<Ability>) spell.getAbilities()) {
            if (ability instanceof OptionalAdditionalSourceCosts) {
                sb.append(((OptionalAdditionalSourceCosts) ability).getCastMessageSuffix());
            }
            if (ability instanceof AlternativeSourceCosts) {
                sb.append(((AlternativeSourceCosts) ability).getCastMessageSuffix());
            }
        }
        return sb.toString();
    }

    public void setMayActivate(TargetController mayActivate) {
        this.mayActivate = mayActivate;
    }

    public UUID getActivatorId() {
        return this.activatorId;
    }
}
