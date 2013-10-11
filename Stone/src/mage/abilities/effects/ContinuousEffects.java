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

package mage.abilities.effects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.MageSingleton;
import mage.abilities.SpellAbility;
import mage.abilities.StaticAbility;
import mage.abilities.keyword.SpliceOntoArcaneAbility;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.AsThoughEffectType;
import mage.constants.CostModificationType;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SpellAbilityType;
import mage.constants.SubLayer;
import mage.filter.FilterCard;
import mage.filter.predicate.Predicate;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.CardIdPredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCardInHand;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ContinuousEffects implements Serializable {

    private Date lastSetTimestamp;

    //transient Continuous effects
    private ContinuousEffectsList<ContinuousEffect> layeredEffects = new ContinuousEffectsList<ContinuousEffect>();
    private ContinuousEffectsList<ReplacementEffect> replacementEffects = new ContinuousEffectsList<ReplacementEffect>();
    private ContinuousEffectsList<PreventionEffect> preventionEffects = new ContinuousEffectsList<PreventionEffect>();
    private ContinuousEffectsList<RequirementEffect> requirementEffects = new ContinuousEffectsList<RequirementEffect>();
    private ContinuousEffectsList<RestrictionEffect> restrictionEffects = new ContinuousEffectsList<RestrictionEffect>();
    private ContinuousEffectsList<RestrictionUntapNotMoreThanEffect> restrictionUntapNotMoreThanEffects = new ContinuousEffectsList<RestrictionUntapNotMoreThanEffect>();
    private Map<AsThoughEffectType, ContinuousEffectsList<AsThoughEffect>> asThoughEffectsMap = new EnumMap<AsThoughEffectType, ContinuousEffectsList<AsThoughEffect>>(AsThoughEffectType.class);
    private ContinuousEffectsList<CostModificationEffect> costModificationEffects = new ContinuousEffectsList<CostModificationEffect>();
    private ContinuousEffectsList<SpliceCardEffect> spliceCardEffects = new ContinuousEffectsList<SpliceCardEffect>();

    private List<ContinuousEffectsList<?>> allEffectsLists = new ArrayList<ContinuousEffectsList<?>>();
    
    private final ApplyCountersEffect applyCounters;
    private final PlaneswalkerRedirectionEffect planeswalkerRedirectionEffect;
    private final AuraReplacementEffect auraReplacementEffect;

    private List<ContinuousEffect> previous = new ArrayList<ContinuousEffect>();

    // effect.id -> sourceId - which effect was added by which sourceId
    private Map<UUID, UUID> sources = new HashMap<UUID, UUID>();

    public ContinuousEffects() {
        applyCounters = new ApplyCountersEffect();
        planeswalkerRedirectionEffect = new PlaneswalkerRedirectionEffect();
        auraReplacementEffect = new AuraReplacementEffect();
        collectAllEffects();
    }

    public ContinuousEffects(final ContinuousEffects effect) {
        this.applyCounters = effect.applyCounters.copy();
        this.planeswalkerRedirectionEffect = effect.planeswalkerRedirectionEffect.copy();
        this.auraReplacementEffect = effect.auraReplacementEffect.copy();
        layeredEffects = effect.layeredEffects.copy();
        replacementEffects = effect.replacementEffects.copy();
        preventionEffects = effect.preventionEffects.copy();
        requirementEffects = effect.requirementEffects.copy();
        restrictionEffects = effect.restrictionEffects.copy();
        restrictionUntapNotMoreThanEffects = effect.restrictionUntapNotMoreThanEffects.copy();
        for (Map.Entry<AsThoughEffectType, ContinuousEffectsList<AsThoughEffect>> entry : effect.asThoughEffectsMap.entrySet()) {
            asThoughEffectsMap.put(entry.getKey(), entry.getValue());
        }

        costModificationEffects = effect.costModificationEffects.copy();
        spliceCardEffects = effect.spliceCardEffects.copy();
        for (Map.Entry<UUID, UUID> entry : effect.sources.entrySet()) {
            sources.put(entry.getKey(), entry.getValue());
        }
        collectAllEffects();
        lastSetTimestamp = effect.lastSetTimestamp;
    }

    private void collectAllEffects() {
        allEffectsLists.add(layeredEffects);
        allEffectsLists.add(replacementEffects);
        allEffectsLists.add(preventionEffects);
        allEffectsLists.add(requirementEffects);
        allEffectsLists.add(restrictionEffects);
        allEffectsLists.add(restrictionUntapNotMoreThanEffects);
        for(ContinuousEffectsList asThoughtlist :asThoughEffectsMap.values()) {
            allEffectsLists.add(asThoughtlist);
        }
        allEffectsLists.add(costModificationEffects);
        allEffectsLists.add(spliceCardEffects);
    }

    public ContinuousEffects copy() {
        return new ContinuousEffects(this);
    }

    public List<RequirementEffect> getRequirementEffects() {
        return requirementEffects;
    }

    public List<RestrictionEffect> getRestrictionEffects() {
        return restrictionEffects;
    }

    public void removeEndOfCombatEffects() {
        layeredEffects.removeEndOfCombatEffects();
        replacementEffects.removeEndOfCombatEffects();
        preventionEffects.removeEndOfCombatEffects();
        requirementEffects.removeEndOfCombatEffects();
        restrictionEffects.removeEndOfCombatEffects();
        for(ContinuousEffectsList asThoughtlist :asThoughEffectsMap.values()) {
            asThoughtlist.removeEndOfCombatEffects();
        }
        costModificationEffects.removeEndOfCombatEffects();
        spliceCardEffects.removeEndOfCombatEffects();
    }

    public void removeEndOfTurnEffects() {
        layeredEffects.removeEndOfTurnEffects();
        replacementEffects.removeEndOfTurnEffects();
        preventionEffects.removeEndOfTurnEffects();
        requirementEffects.removeEndOfTurnEffects();
        restrictionEffects.removeEndOfTurnEffects();
        for(ContinuousEffectsList asThoughtlist :asThoughEffectsMap.values()) {
            asThoughtlist.removeEndOfTurnEffects();
        }
        costModificationEffects.removeEndOfTurnEffects();
        spliceCardEffects.removeEndOfTurnEffects();
    }

    public void removeInactiveEffects(Game game) {
        layeredEffects.removeInactiveEffects(game);
        replacementEffects.removeInactiveEffects(game);
        preventionEffects.removeInactiveEffects(game);
        requirementEffects.removeInactiveEffects(game);
        restrictionEffects.removeInactiveEffects(game);
        restrictionUntapNotMoreThanEffects.removeInactiveEffects(game);
        for(ContinuousEffectsList asThoughtlist :asThoughEffectsMap.values()) {
            asThoughtlist.removeInactiveEffects(game);
        }
        costModificationEffects.removeInactiveEffects(game);
        spliceCardEffects.removeInactiveEffects(game);
    }

    public List<ContinuousEffect> getLayeredEffects(Game game) {
        List<ContinuousEffect> layerEffects = new ArrayList<ContinuousEffect>();
        for (ContinuousEffect effect: layeredEffects) {
            switch (effect.getDuration()) {
                case WhileOnBattlefield:
                case WhileOnStack:
                case WhileInGraveyard:
                    HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
                    for (Ability ability: abilities) {
                        // If e.g. triggerd abilities (non static) created the effect, the ability must not be in usable zone (e.g. Unearth giving Haste effect)
                        if (!(ability instanceof StaticAbility) || ability.isInUseableZone(game, null, false)) {
                            layerEffects.add(effect);
                            break;
                        }
                    }
                    break;
                default:
                    layerEffects.add(effect);
            }
        }

        updateTimestamps(layerEffects);

        Collections.sort(layerEffects, new TimestampSorter());
        return layerEffects;
    }

    /**
     * Initially effect timestamp is set when game starts in game.loadCard method.
     * After that timestamp should be updated whenever effect becomes "actual" meaning it becomes turned on
     * that is defined by Ability.#isInUseableZone(Game, boolean) method in #getLayeredEffects(Game).
     * @param layerEffects
     */
    private void updateTimestamps(List<ContinuousEffect> layerEffects) {
        for (ContinuousEffect continuousEffect : layerEffects) {
            // check if it's new, then set timestamp
            if (!previous.contains(continuousEffect)) {
                setUniqueTimesstamp(continuousEffect);
            }
        }
        previous.clear();
        previous.addAll(layerEffects);
    }

    public void setUniqueTimesstamp(ContinuousEffect effect) {
        do {
            effect.setTimestamp();
        }
        while (effect.getTimestamp().equals(lastSetTimestamp)); // prevent to set the same timestamp so logical order is saved
        lastSetTimestamp = effect.getTimestamp();
    }

    private List<ContinuousEffect> filterLayeredEffects(List<ContinuousEffect> effects, Layer layer) {
        List<ContinuousEffect> layerEffects = new ArrayList<ContinuousEffect>();
        for (ContinuousEffect effect: effects) {
            if (effect.hasLayer(layer)) {
                layerEffects.add(effect);
            }
        }
        return layerEffects;
    }

    public HashMap<RequirementEffect, HashSet<Ability>> getApplicableRequirementEffects(Permanent permanent, Game game) {
        HashMap<RequirementEffect, HashSet<Ability>> effects = new HashMap<RequirementEffect, HashSet<Ability>>();
        for (RequirementEffect effect: requirementEffects) {
            HashSet<Ability> abilities = requirementEffects.getAbility(effect.getId());
            HashSet<Ability> applicableAbilities = new HashSet<Ability>();
            for (Ability ability : abilities) {
                if (!(ability instanceof StaticAbility) || ability.isInUseableZone(game, null, false)) {
                    if (effect.applies(permanent, ability, game)) {
                        applicableAbilities.add(ability);
                    }
                }
            }
            if (!applicableAbilities.isEmpty()) {
                effects.put(effect, abilities);
            }
        }
        return effects;
    }

    public HashMap<RestrictionEffect, HashSet<Ability>> getApplicableRestrictionEffects(Permanent permanent, Game game) {
        HashMap<RestrictionEffect, HashSet<Ability>> effects = new HashMap<RestrictionEffect, HashSet<Ability>>();        
        for (RestrictionEffect effect: restrictionEffects) {
            HashSet<Ability> abilities = restrictionEffects.getAbility(effect.getId());
            HashSet<Ability> applicableAbilities = new HashSet<Ability>();
            for (Ability ability : abilities) {
                if (!(ability instanceof StaticAbility) || ability.isInUseableZone(game, ability instanceof MageSingleton ? permanent : null, false)) {
                    if (effect.applies(permanent, ability, game)) {
                        applicableAbilities.add(ability);
                    }
                }
            }
            if (!applicableAbilities.isEmpty()) {
                effects.put(effect, abilities);
            }
        }
        return effects;
    }

    public HashMap<RestrictionUntapNotMoreThanEffect, HashSet<Ability>> getApplicableRestrictionUntapNotMoreThanEffects(Player player, Game game) {
        HashMap<RestrictionUntapNotMoreThanEffect, HashSet<Ability>> effects = new HashMap<RestrictionUntapNotMoreThanEffect, HashSet<Ability>>();
        for (RestrictionUntapNotMoreThanEffect effect: restrictionUntapNotMoreThanEffects) {
            HashSet<Ability> abilities = restrictionUntapNotMoreThanEffects.getAbility(effect.getId());
            HashSet<Ability> applicableAbilities = new HashSet<Ability>();
            for (Ability ability : abilities) {
                if (!(ability instanceof StaticAbility) || ability.isInUseableZone(game, null, false)) {
                    if (effect.applies(player, ability, game)) {
                        applicableAbilities.add(ability);
                    }
                }
            }
            if (!applicableAbilities.isEmpty()) {
                effects.put(effect, abilities);
            }
        }
        return effects;
    }
    
    /**
     *
     * @param event
     * @param game
     * @return a list of all {@link ReplacementEffect} that apply to the current event
     */
    private HashMap<ReplacementEffect, HashSet<Ability>> getApplicableReplacementEffects(GameEvent event, Game game) {
        // List<ReplacementEffect> replaceEffects = new ArrayList<ReplacementEffect>();
        HashMap<ReplacementEffect, HashSet<Ability>> replaceEffects = new HashMap<ReplacementEffect, HashSet<Ability>>();
        if (planeswalkerRedirectionEffect.applies(event, null, game)) {
            replaceEffects.put(planeswalkerRedirectionEffect, null);
        }
        if(auraReplacementEffect.applies(event, null, game)){
            replaceEffects.put(auraReplacementEffect, null);
        }
        //get all applicable transient Replacement effects
        for (ReplacementEffect effect: replacementEffects) {
            if (event.getAppliedEffects() != null && event.getAppliedEffects().contains(effect.getId())) {
                // Effect already applied to this event, ignore it
                // TODO: Handle also gained effect that are connected to different abilities.
                continue;
            }
            HashSet<Ability> abilities = replacementEffects.getAbility(effect.getId());
            HashSet<Ability> applicableAbilities = new HashSet<Ability>();
            for (Ability ability : abilities) {
                if (!(ability instanceof StaticAbility) || ability.isInUseableZone(game, null, false)) {
                    if (effect.getDuration() != Duration.OneUse || !effect.isUsed()) {
                        if (!game.getScopeRelevant() || effect.hasSelfScope() || !event.getTargetId().equals(ability.getSourceId())) {
                            if (effect.applies(event, ability, game)) {
                                applicableAbilities.add(ability);
                            }
                        }
                    }
                }
            }
            if (!applicableAbilities.isEmpty()) {
                replaceEffects.put((ReplacementEffect) effect, applicableAbilities);
            }
        }
        for (PreventionEffect effect: preventionEffects) {
            HashSet<Ability> abilities = preventionEffects.getAbility(effect.getId());
            HashSet<Ability> applicableAbilities = new HashSet<Ability>();
            for (Ability ability : abilities) {
                if (!(ability instanceof StaticAbility) || ability.isInUseableZone(game, null, false)) {
                    if (effect.getDuration() != Duration.OneUse || !effect.isUsed()) {
                        if (effect.applies(event, ability, game)) {
                            applicableAbilities.add(ability);
                        }
                    }
                }
            }
            if (!applicableAbilities.isEmpty()) {
                replaceEffects.put((ReplacementEffect) effect, applicableAbilities);
            }
        }
        return replaceEffects;
    }

    /**
     * Filters out cost modification effects that are not active.
     *
     * @param game
     * @return
     */
    private List<CostModificationEffect> getApplicableCostModificationEffects(Game game) {
        List<CostModificationEffect> costEffects = new ArrayList<CostModificationEffect>();

        for (CostModificationEffect effect: costModificationEffects) {
            HashSet<Ability> abilities = costModificationEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                if (!(ability instanceof StaticAbility) || ability.isInUseableZone(game, null, false)) {
                    if (effect.getDuration() != Duration.OneUse || !effect.isUsed()) {
                        costEffects.add(effect);
                        break;
                    }
                }
            }
        }

        return costEffects;
    }
    /**
     * Filters out splice effects that are not active.
     *
     * @param game
     * @return
     */
    private List<SpliceCardEffect> getApplicableSpliceCardEffects(Game game, UUID playerId) {
        List<SpliceCardEffect> spliceEffects = new ArrayList<SpliceCardEffect>();

        for (SpliceCardEffect effect: spliceCardEffects) {
            HashSet<Ability> abilities = spliceCardEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                if (ability.getControllerId().equals(playerId) && (!(ability instanceof StaticAbility) || ability.isInUseableZone(game, null, false))) {
                    if (effect.getDuration() != Duration.OneUse || !effect.isUsed()) {
                        spliceEffects.add(effect);
                        break;
                    }
                }
            }
        }

        return spliceEffects;
    }

    public boolean asThough(UUID objectId, AsThoughEffectType type, Game game) {
        List<AsThoughEffect> asThoughEffectsList = getApplicableAsThoughEffects(type, game);
        for (AsThoughEffect effect: asThoughEffectsList) {
            HashSet<Ability> abilities = asThoughEffectsMap.get(type).getAbility(effect.getId());
            for (Ability ability : abilities) {
                if (effect.applies(objectId, ability, game)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Filters out asThough effects that are not active.
     *
     * @param AsThoughEffectType type
     * @param game
     * @return
     */
    private List<AsThoughEffect> getApplicableAsThoughEffects(AsThoughEffectType type, Game game) {
        List<AsThoughEffect> asThoughEffectsList = new ArrayList<AsThoughEffect>();
        if (asThoughEffectsMap.containsKey(type)) {
            for (AsThoughEffect effect: asThoughEffectsMap.get(type)) {
                HashSet<Ability> abilities = asThoughEffectsMap.get(type).getAbility(effect.getId());
                for (Ability ability : abilities) {
                    if (!(ability instanceof StaticAbility) || ability.isInUseableZone(game, null, false)) {
                        if (effect.getDuration() != Duration.OneUse || !effect.isUsed()) {
                            asThoughEffectsList.add(effect);
                            break;
                        }
                    }
                }
            }
        }
        return asThoughEffectsList;
    }

    /**
     * 601.2e The player determines the total cost of the spell. Usually this is
     * just the mana cost. Some spells have additional or alternative costs. Some
     * effects may increase or reduce the cost to pay, or may provide other alternative costs.
     * Costs may include paying mana, tapping permanents, sacrificing permanents,
     * discarding cards, and so on. The total cost is the mana cost or alternative
     * cost (as determined in rule 601.2b), plus all additional costs and cost increases,
     * and minus all cost reductions. If the mana component of the total cost is reduced
     * to nothing by cost reduction effects, it is considered to be {0}. 
     * It can’t be reduced to less than {0}. Once the total cost is determined,
     * any effects that directly affect the total cost are applied.
     * Then the resulting total cost becomes “locked in.”
     * If effects would change the total cost after this time, they have no effect.
     */
    /**
     * Inspects all {@link Permanent permanent's} {@link Ability abilities} on the battlefield
     * for {@link CostModificationEffect cost modification effects} and applies them if necessary.
     *
     * @param abilityToModify
     * @param game
     * @return
     */
    public void costModification ( Ability abilityToModify, Game game ) {
        List<CostModificationEffect> costEffects = getApplicableCostModificationEffects(game);

        for ( CostModificationEffect effect : costEffects) {
            if(effect.getModificationType() == CostModificationType.INCREASE_COST){
                HashSet<Ability> abilities = costModificationEffects.getAbility(effect.getId());
                for (Ability ability : abilities) {
                    if ( effect.applies(abilityToModify, ability, game) ) {
                        effect.apply(game, ability, abilityToModify);
                    }
                }
            }
        }
        
        for ( CostModificationEffect effect : costEffects) {
            if(effect.getModificationType() == CostModificationType.REDUCE_COST){
                HashSet<Ability> abilities = costModificationEffects.getAbility(effect.getId());
                for (Ability ability : abilities) {
                    if ( effect.applies(abilityToModify, ability, game) ) {
                        effect.apply(game, ability, abilityToModify);
                    }
                }
            }
        }
                
        for ( CostModificationEffect effect : costEffects) {
            if(effect.getModificationType() == CostModificationType.SET_COST){
                HashSet<Ability> abilities = costModificationEffects.getAbility(effect.getId());
                for (Ability ability : abilities) {
                    if ( effect.applies(abilityToModify, ability, game) ) {
                        effect.apply(game, ability, abilityToModify);
                    }
                }
            }
        }
    }

    /**
     * Checks all available splice effects to be applied.
     *
     * @param abilityToModify
     * @param game
     * @return
     */
    public void applySpliceEffects ( Ability abilityToModify, Game game ) {
        if ( ((SpellAbility) abilityToModify).getSpellAbilityType().equals(SpellAbilityType.SPLICE)) {
            // on a spliced ability of a spell can't be spliced again
            return;
        }
        List<SpliceCardEffect> spliceEffects = getApplicableSpliceCardEffects(game, abilityToModify.getControllerId());
        // get the applyable splice abilities
        List<SpliceOntoArcaneAbility> spliceAbilities = new ArrayList<SpliceOntoArcaneAbility>();
        for (SpliceCardEffect effect : spliceEffects) {
            HashSet<Ability> abilities = spliceCardEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                if (effect.applies(abilityToModify, ability, game) ) {
                    spliceAbilities.add((SpliceOntoArcaneAbility) ability);
                }
            }
        }
        // check if player wants to use splice

        if (spliceAbilities.size() > 0) {
            Player controller = game.getPlayer(abilityToModify.getControllerId());
            if (controller.chooseUse(Outcome.Benefit, "Splice a card?", game)) {
                Cards cardsToReveal = new CardsImpl();
                do {
                    FilterCard filter = new FilterCard("a card to splice");
                    ArrayList<Predicate<MageObject>> idPredicates = new ArrayList<Predicate<MageObject>>();
                    for (SpliceOntoArcaneAbility ability : spliceAbilities) {
                        idPredicates.add(new CardIdPredicate((ability.getSourceId())));
                    }
                    filter.add(Predicates.or(idPredicates));
                    TargetCardInHand target = new TargetCardInHand(filter);
                    target.setRequired(true);
                    controller.chooseTarget(Outcome.Benefit, target, abilityToModify, game);
                    UUID cardId = target.getFirstTarget();
                    if (cardId != null) {
                        SpliceOntoArcaneAbility selectedAbility = null;
                        for(SpliceOntoArcaneAbility ability :spliceAbilities) {
                            if (ability.getSourceId().equals(cardId)) {
                                selectedAbility = ability;
                                break;
                            }
                        }
                        if (selectedAbility != null) {
                            SpliceCardEffect spliceEffect = (SpliceCardEffect) selectedAbility.getEffects().get(0);
                            spliceEffect.apply(game, selectedAbility, abilityToModify);
                            cardsToReveal.add(game.getCard(cardId));
                            spliceAbilities.remove(selectedAbility);
                        }
                    }
                } while (!spliceAbilities.isEmpty() && controller.chooseUse(Outcome.Benefit, "Splice another card?", game));
                controller.revealCards("Spliced cards", cardsToReveal, game);
            }
        }
    }


    public boolean replaceEvent(GameEvent event, Game game) {
        boolean caught = false;
        HashMap<UUID, HashSet<UUID>> consumed = new HashMap<UUID, HashSet<UUID>>();
        do {
            HashMap<ReplacementEffect, HashSet<Ability>> rEffects = getApplicableReplacementEffects(event, game);
            // Remove all consumed effects (ability dependant)
            for (Iterator<ReplacementEffect> it1 = rEffects.keySet().iterator(); it1.hasNext();){
                ReplacementEffect entry = it1.next();
                if (consumed.containsKey(entry.getId())) {
                    HashSet<UUID> consumedAbilitiesIds = consumed.get(entry.getId());
                    if (rEffects.get(entry) == null || consumedAbilitiesIds.size() == ((HashSet<Ability>) rEffects.get(entry)).size()) {
                        it1.remove();
                    } else {
                        Iterator it = ((HashSet<Ability>) rEffects.get(entry)).iterator();
                        while (it.hasNext()) {
                            Ability ability = (Ability) it.next();
                            if (consumedAbilitiesIds.contains(ability.getId())) {
                                it.remove();
                            }
                        }
                    }
                }
            }
            // no effects left, quit
            if (rEffects.isEmpty()) {
                break;
            }
            int index;
            boolean onlyOne = false;
            if (rEffects.size() == 1) {
                ReplacementEffect effect = (ReplacementEffect) rEffects.keySet().iterator().next();
                HashSet<Ability> abilities = replacementEffects.getAbility(effect.getId());
                if (abilities == null || abilities.size() == 1) {
                    onlyOne = true;
                }
            }
            if (onlyOne) {
                index = 0;
            } else {
                //20100716 - 616.1c
                Player player = game.getPlayer(event.getPlayerId());
                index = player.chooseEffect(getReplacementEffectsTexts(rEffects, game), game);
            }
            // get the selected effect
            int checked = 0;
            ReplacementEffect rEffect = null;
            Ability rAbility = null;
            for (Map.Entry entry : rEffects.entrySet()) {
                if (entry.getValue() == null) {
                    if (checked == index) {
                        rEffect = (ReplacementEffect) entry.getKey();
                        break;
                    } else {
                        checked++;
                    }
                } else {
                    HashSet<Ability> abilities = (HashSet<Ability>) entry.getValue();
                    int size = abilities.size();
                    if (index > (checked + size - 1)) {
                        checked += size;
                    } else {
                        rEffect = (ReplacementEffect) entry.getKey();
                        Iterator it = abilities.iterator();
                        while (it.hasNext() && rAbility == null) {
                            if (checked == index) {
                                rAbility = (Ability) it.next();
                            } else {
                                it.next();
                                checked++;
                            }
                        }
                        break;
                    }
                }                
            }

            if (rEffect != null) {
                event.getAppliedEffects().add(rEffect.getId());
                caught = rEffect.replaceEvent(event, rAbility, game);
            }
            if (caught) { // Event was completely replaced -> stop applying effects to it
                break;
            }

            // add used effect to consumed effects
            if (rEffect != null) {
                if (consumed.containsKey(rEffect.getId())) {
                    HashSet<UUID> set = consumed.get(rEffect.getId());
                    if (rAbility != null) {
                        if (!set.contains(rAbility.getId())) {
                            set.add(rAbility.getId());
                        }
                    }
                } else {
                    HashSet<UUID> set = new HashSet<UUID>();
                    if (rAbility != null) { // in case of AuraReplacementEffect or PlaneswalkerReplacementEffect there is no Ability
                        set.add(rAbility.getId());
                    }
                    consumed.put(rEffect.getId(), set);
                }
            }

            game.applyEffects();
        } while (true);
        return caught;
    }

    //20091005 - 613
    public void apply(Game game) {
        removeInactiveEffects(game);
        List<ContinuousEffect> layerEffects = getLayeredEffects(game);
        List<ContinuousEffect> layer = filterLayeredEffects(layerEffects, Layer.CopyEffects_1);
        for (ContinuousEffect effect: layer) {
            HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                effect.apply(Layer.CopyEffects_1, SubLayer.NA, ability, game);
            }
        }
        //Reload layerEffect if copy effects were applied
        if (layer.size()>0) {
            layerEffects = getLayeredEffects(game);
        }

        layer = filterLayeredEffects(layerEffects, Layer.ControlChangingEffects_2);
        for (ContinuousEffect effect: layer) {
            HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                effect.apply(Layer.ControlChangingEffects_2, SubLayer.NA, ability, game);
            }
        }
        layer = filterLayeredEffects(layerEffects, Layer.TextChangingEffects_3);
        for (ContinuousEffect effect: layer) {
            HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                effect.apply(Layer.TextChangingEffects_3, SubLayer.NA, ability, game);
            }
        }
        layer = filterLayeredEffects(layerEffects, Layer.TypeChangingEffects_4);
        for (ContinuousEffect effect: layer) {
            HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                effect.apply(Layer.TypeChangingEffects_4, SubLayer.NA, ability, game);
            }
        }
        layer = filterLayeredEffects(layerEffects, Layer.ColorChangingEffects_5);
        for (ContinuousEffect effect: layer) {
            HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                effect.apply(Layer.ColorChangingEffects_5, SubLayer.NA, ability, game);
            }
        }
        layer = filterLayeredEffects(layerEffects, Layer.AbilityAddingRemovingEffects_6);
        for (ContinuousEffect effect: layer) {
            if (layerEffects.contains(effect)) {
                HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
                for (Ability ability : abilities) {
                    effect.apply(Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, ability, game);
                }
            }
            layerEffects = getLayeredEffects(game);
        }
        layer = filterLayeredEffects(layerEffects, Layer.PTChangingEffects_7);
        for (ContinuousEffect effect: layer) {
            HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                effect.apply(Layer.PTChangingEffects_7, SubLayer.SetPT_7b, ability, game);
            }
        }
        for (ContinuousEffect effect: layer) {
            HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                effect.apply(Layer.PTChangingEffects_7, SubLayer.ModifyPT_7c, ability, game);
            }
        }
        applyCounters.apply(Layer.PTChangingEffects_7, SubLayer.Counters_7d, null, game);
        for (ContinuousEffect effect: layer) {
            HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                effect.apply(Layer.PTChangingEffects_7, SubLayer.SwitchPT_e, ability, game);
            }
        }
        layer = filterLayeredEffects(layerEffects, Layer.PlayerEffects);
        for (ContinuousEffect effect: layer) {
            HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                effect.apply(Layer.PlayerEffects, SubLayer.NA, ability, game);
            }
        }
        layer = filterLayeredEffects(layerEffects, Layer.RulesEffects);
        for (ContinuousEffect effect: layer) {
            HashSet<Ability> abilities = layeredEffects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                effect.apply(Layer.RulesEffects, SubLayer.NA, ability, game);
            }
        }
    }

    public void addEffect(ContinuousEffect effect, UUID sourceId, Ability source) {
        addEffect(effect, source);
        sources.put(effect.getId(), sourceId);
    }

    public void addEffect(ContinuousEffect effect, Ability source) {
        switch (effect.getEffectType()) {
            case REPLACEMENT:
            case REDIRECTION:
                ReplacementEffect newReplacementEffect = (ReplacementEffect)effect;
                replacementEffects.addEffect(newReplacementEffect, source);
                break;
            case PREVENTION:
                PreventionEffect newPreventionEffect = (PreventionEffect)effect;
                preventionEffects.addEffect(newPreventionEffect, source);
                break;
            case RESTRICTION:
                RestrictionEffect newRestrictionEffect = (RestrictionEffect)effect;
                restrictionEffects.addEffect(newRestrictionEffect, source);
                break;
            case RESTRICTION_UNTAP_NOT_MORE_THAN:
                RestrictionUntapNotMoreThanEffect newRestrictionUntapNotMoreThanEffect = (RestrictionUntapNotMoreThanEffect)effect;
                restrictionUntapNotMoreThanEffects.addEffect(newRestrictionUntapNotMoreThanEffect, source);
                break;
            case REQUIREMENT:
                RequirementEffect newRequirementEffect = (RequirementEffect)effect;
                requirementEffects.addEffect(newRequirementEffect, source);
                break;
            case ASTHOUGH:
                AsThoughEffect newAsThoughEffect = (AsThoughEffect)effect;
                if (!asThoughEffectsMap.containsKey(newAsThoughEffect.getAsThoughEffectType())) {
                    ContinuousEffectsList list = new ContinuousEffectsList();
                    asThoughEffectsMap.put(newAsThoughEffect.getAsThoughEffectType(), list);
                }
                asThoughEffectsMap.get(newAsThoughEffect.getAsThoughEffectType()).addEffect(newAsThoughEffect, source);
                break;
            case COSTMODIFICATION:
                CostModificationEffect newCostModificationEffect = (CostModificationEffect)effect;
                costModificationEffects.addEffect(newCostModificationEffect, source);
                break;
            case SPLICE:
                SpliceCardEffect newSpliceCardEffect = (SpliceCardEffect)effect;
                spliceCardEffects.addEffect(newSpliceCardEffect, source);
                break;
            default:
                layeredEffects.addEffect(effect, source);
                break;
        }
    }

    public void setController(UUID cardId, UUID controllerId) {
        for (ContinuousEffectsList effectsList : allEffectsLists) {
            setControllerForEffect(effectsList, cardId, controllerId);
        }
    }

    private void setControllerForEffect(ContinuousEffectsList<?> effects, UUID cardId, UUID controllerId) {
        for (Effect effect : effects) {
            HashSet<Ability> abilities = effects.getAbility(effect.getId());
            for (Ability ability : abilities) {
                if (ability.getSourceId().equals(cardId)) {
                    ability.setControllerId(controllerId);
                }
            }
        }
    }

    public void clear() {
        for (ContinuousEffectsList effectsList : allEffectsLists) {
            effectsList.clear();
        }
        sources.clear();
    }


    /**
     * Removes effects granted by sourceId
     *
     * @param sourceId
     */
    public List<Effect> removeGainedEffectsForSource(UUID sourceId) {
        List<Effect> effects = new ArrayList<Effect>();
        Set<UUID> effectsToRemove = new HashSet<UUID>();
        for (Map.Entry<UUID, UUID> source : sources.entrySet()) {
            if (sourceId.equals(source.getValue())) {
                for (ContinuousEffectsList effectsList : allEffectsLists) {
                    Iterator it = effectsList.iterator();
                    while (it.hasNext()) {
                        ContinuousEffect effect = (ContinuousEffect) it.next();
                        if (source.getKey().equals(effect.getId())) {
                            effectsToRemove.add(effect.getId());
                            effectsList.removeEffectAbilityMap(effect.getId());
                            it.remove();
                        }
                    }
                }
            }
        }
        for (UUID effectId: effectsToRemove) {
            sources.remove(effectId);
        }
        return effects;
    }

    public List<String> getReplacementEffectsTexts(HashMap<ReplacementEffect, HashSet<Ability>> rEffects, Game game) {
        List<String> texts = new ArrayList<String>();
        for (Map.Entry entry : rEffects.entrySet()) {
            for (Ability ability :(HashSet<Ability>) entry.getValue()) {
                MageObject object = game.getObject(ability.getSourceId());
                if (object != null) {
                    texts.add(ability.getRule(object.getName()));
                } else {
                    texts.add(((ReplacementEffect)entry.getKey()).getText(null));
                }
            }

        }
        return texts;
    }

}
class TimestampSorter implements Comparator<ContinuousEffect> {
    @Override
    public int compare(ContinuousEffect one, ContinuousEffect two) {
        return one.getTimestamp().compareTo(two.getTimestamp());
    }
}
