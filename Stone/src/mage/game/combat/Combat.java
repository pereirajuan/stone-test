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

package mage.game.combat;

import java.io.Serializable;
import java.util.*;
import mage.constants.Outcome;
import mage.abilities.Ability;
import mage.abilities.effects.RequirementEffect;
import mage.abilities.keyword.CanAttackOnlyAloneAbility;
import mage.abilities.keyword.CantAttackAloneAbility;
import mage.abilities.keyword.VigilanceAbility;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.common.FilterCreatureForCombatBlock;
import mage.filter.common.FilterPlaneswalkerPermanent;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.players.PlayerList;
import mage.target.common.TargetDefender;
import mage.util.Copyable;
import mage.util.trace.TraceUtil;


/**
 * @author BetaSteward_at_googlemail.com
 */
public class Combat implements Serializable, Copyable<Combat> {

    private static FilterPlaneswalkerPermanent filterPlaneswalker = new FilterPlaneswalkerPermanent();
    private static FilterCreatureForCombatBlock filterBlockers = new FilterCreatureForCombatBlock();

    protected List<CombatGroup> groups = new ArrayList<CombatGroup>();
    protected Map<UUID, CombatGroup> blockingGroups = new HashMap<UUID, CombatGroup>();
    protected Set<UUID> defenders = new HashSet<UUID>();
    protected UUID attackerId; //the player that is attacking

    public Combat() {
    }

    public Combat(final Combat combat) {
        this.attackerId = combat.attackerId;
        for (CombatGroup group : combat.groups) {
            groups.add(group.copy());
        }
        defenders.addAll(combat.defenders);
        for (Map.Entry<UUID, CombatGroup> group : combat.blockingGroups.entrySet()) {
            blockingGroups.put(group.getKey(), group.getValue());
        }
    }

    public List<CombatGroup> getGroups() {
        return groups;
    }

    public Collection<CombatGroup> getBlockingGroups() {
        return blockingGroups.values();
    }

    public Set<UUID> getDefenders() {
        return defenders;
    }

    public List<UUID> getAttackers() {
        List<UUID> attackers = new ArrayList<UUID>();
        for (CombatGroup group : groups) {
            attackers.addAll(group.attackers);
        }
        return attackers;
    }

    public List<UUID> getBlockers() {
        List<UUID> blockers = new ArrayList<UUID>();
        for (CombatGroup group : groups) {
            blockers.addAll(group.blockers);
        }
        return blockers;
    }

    public void clear() {
        groups.clear();
        blockingGroups.clear();
        defenders.clear();
        attackerId = null;
    }

    public String getValue() {
        StringBuilder sb = new StringBuilder();
        sb.append(attackerId).append(defenders);
        for (CombatGroup group : groups) {
            sb.append(group.defenderId).append(group.attackers).append(group.attackerOrder).append(group.blockers).append(group.blockerOrder);
        }
        return sb.toString();
    }

    public void setAttacker(UUID playerId) {
        this.attackerId = playerId;
    }

    /**
     * Add an additional attacker to the combat (e.g. token of Geist of Saint Traft)
     * This method doesn't trigger ATTACKER_DECLARED event (as intended).
     *
     * @param creatureId - creature that shall be added to the combat
     * @param game
     * @return
     */
    public boolean addAttackingCreature(UUID creatureId, Game game) {
        Player player = game.getPlayer(attackerId);
        if (defenders.size() == 1) {
            declareAttacker(creatureId, defenders.iterator().next(), game);
            return true;
        }
        else {
            TargetDefender target = new TargetDefender(defenders, creatureId);
            target.setRequired(true);
            player.chooseTarget(Outcome.Damage, target, null, game);
            if (target.getFirstTarget() != null) {
                declareAttacker(creatureId, target.getFirstTarget(), game);
                return true;
            }
        }
        return false;
    }

    public void selectAttackers(Game game) {
        if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.DECLARING_ATTACKERS, attackerId, attackerId))) {
            Player player = game.getPlayer(attackerId);
            //20101001 - 508.1d
            checkAttackRequirements(player, game);
            player.selectAttackers(game, attackerId);
            if (game.isPaused() || game.isGameOver()) {
                return;
            }
            checkAttackRestrictions(player, game);
            resumeSelectAttackers(game);
        }
    }

    public void resumeSelectAttackers(Game game) {
        Player player = game.getPlayer(attackerId);
        for (CombatGroup group: groups) {
            for (UUID attacker: group.getAttackers()) {
                game.fireEvent(GameEvent.getEvent(GameEvent.EventType.ATTACKER_DECLARED, group.defenderId, attacker, attackerId));
            }
        }
        game.fireEvent(GameEvent.getEvent(GameEvent.EventType.DECLARED_ATTACKERS, attackerId, attackerId));
        game.fireInformEvent(player.getName() + " attacks with " + groups.size() + " creatures");
    }

    protected void checkAttackRequirements(Player player, Game game) {
        //20101001 - 508.1d
        for (Permanent creature : player.getAvailableAttackers(game)) {
            for (Map.Entry entry : game.getContinuousEffects().getApplicableRequirementEffects(creature, game).entrySet()) {
                RequirementEffect effect = (RequirementEffect)entry.getKey();
                if (effect.mustAttack(game)) {
                    for (Ability ability: (HashSet<Ability>)entry.getValue()) {
                        UUID defenderId = effect.mustAttackDefender(ability, game);
                        if (defenderId == null) {
                            if (defenders.size() == 1) {
                                player.declareAttacker(creature.getId(), defenders.iterator().next(), game);
                            } else {
                                TargetDefender target = new TargetDefender(defenders, creature.getId());
                                target.setRequired(true);
                                if (player.chooseTarget(Outcome.Damage, target, null, game)) {
                                    player.declareAttacker(creature.getId(), target.getFirstTarget(), game);
                                }
                            }
                        } else {
                            player.declareAttacker(creature.getId(), defenderId, game);
                        }
                    }
                }
            }
        }
    }

    protected void checkAttackRestrictions(Player player, Game game) {
        int count = 0;
        for (CombatGroup group: groups) {
            count += group.getAttackers().size();
        }

        if (count > 1) {
            List<UUID> tobeRemoved = new ArrayList<UUID>();
            for (CombatGroup group: groups) {
                for (UUID attackingCreatureId: group.getAttackers()) {
                    Permanent attacker = game.getPermanent(attackingCreatureId);
                    if (count >1 && attacker != null && attacker.getAbilities().containsKey(CanAttackOnlyAloneAbility.getInstance().getId())) {
                        game.informPlayers(attacker.getName() + " can only attack alone. Removing it from combat.");
                        tobeRemoved.add(attackingCreatureId);
                        count--;
                    }
                }
            }
            for (UUID attackingCreatureId : tobeRemoved) {
                this.removeAttacker(attackingCreatureId, game);
            }
        }

        if (count == 1) {
            List<UUID> tobeRemoved = new ArrayList<UUID>();
            for (CombatGroup group: groups) {
                for (UUID attackingCreatureId: group.getAttackers()) {
                    Permanent attacker = game.getPermanent(attackingCreatureId);
                    if (attacker != null && attacker.getAbilities().containsKey(CantAttackAloneAbility.getInstance().getId())) {
                        game.informPlayers(attacker.getName() + " can't attack alone. Removing it from combat.");
                        tobeRemoved.add(attackingCreatureId);
                    }
                }
            }
            for (UUID attackingCreatureId : tobeRemoved) {
                this.removeAttacker(attackingCreatureId, game);
            }

        }

    }

    public void selectBlockers(Game game) {
        if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.DECLARING_BLOCKERS, attackerId, attackerId))) {
            Player player = game.getPlayer(attackerId);
            //20101001 - 509.1c
            checkBlockRequirementsBefore(player, game);
            for (UUID defenderId : getPlayerDefenders(game)) {
                boolean choose = true;
                Player defender = game.getPlayer(defenderId);
                while (choose) {
                    game.getPlayer(defenderId).selectBlockers(game, defenderId);
                    if (game.isPaused() || game.isGameOver()) {
                        return;
                    }
                    if (!checkBlockRestrictions(game.getPlayer(defenderId), game)) {
                        // only human player can decide to do the block in another way
                        if (defender.isHuman()) {
                            continue;
                        }
                    }
                    
                    choose = !checkBlockRequirementsAfter(defender, defender, game);
                }
                game.fireEvent(GameEvent.getEvent(GameEvent.EventType.DECLARED_BLOCKERS, defenderId, defenderId));
            }
            TraceUtil.traceCombatIfNeeded(game, this);
        }
    }

/**
 * Check the block restrictions
 * @param player
 * @param game
 * @return false - if block restrictions were not complied
 */
    public boolean checkBlockRestrictions(Player player, Game game) {
        int count = 0;
        boolean blockWasLegal = true;
        for (CombatGroup group: groups) {
            count += group.getBlockers().size();
        }
        for (CombatGroup group : groups) {
            blockWasLegal &= group.checkBlockRestrictions(game, count);
        }
        return blockWasLegal;
    }

    public void acceptBlockers(Game game) {
        for (CombatGroup group : groups) {
            group.acceptBlockers(game);
        }
    }

    public void resumeSelectBlockers(Game game) {
        //TODO: this isn't quite right - but will work fine for two-player games
        for (UUID defenderId : getPlayerDefenders(game)) {
            game.fireEvent(GameEvent.getEvent(GameEvent.EventType.DECLARED_BLOCKERS, defenderId, defenderId));
        }
    }

    public void checkBlockRequirementsBefore(Player player, Game game) {
        //20101001 - 509.1c
        for (Permanent creature : game.getBattlefield().getActivePermanents(filterBlockers, player.getId(), game)) {
            if (game.getOpponents(attackerId).contains(creature.getControllerId())) {
                for (Map.Entry entry : game.getContinuousEffects().getApplicableRequirementEffects(creature, game).entrySet()) {
                    RequirementEffect effect = (RequirementEffect)entry.getKey();
                    if (effect.mustBlock(game)) {
                        for (Ability ability: (HashSet<Ability>)entry.getValue()) {
                            UUID attackId = effect.mustBlockAttacker(ability, game);
                            Player defender = game.getPlayer(creature.getControllerId());
                            if (attackId != null && defender != null) {
                                defender.declareBlocker(creature.getId(), attackId, game);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean checkBlockRequirementsAfter(Player player, Player controller, Game game) {
        //20101001 - 509.1c
        for (Permanent creature : game.getBattlefield().getActivePermanents(new FilterControlledCreaturePermanent(), player.getId(), game)) {
            if (creature.getBlocking() == 0 && game.getOpponents(attackerId).contains(creature.getControllerId())) {
                for (RequirementEffect effect : game.getContinuousEffects().getApplicableRequirementEffects(creature, game).keySet()) {
                    if (effect.mustBlockAny(game)) {
                        // check that it can block an attacker
                        boolean mayBlock = false;
                        for (UUID attackingCreatureId : getAttackers()) {
                            if (creature.canBlock(attackingCreatureId, game)) {
                                mayBlock = true;
                                break;
                            }
                        }
                        if (mayBlock) {
                            if (controller.isHuman()) {
                                game.informPlayer(controller, "Creature should block this turn: " + creature.getName());
                            } else {
                                Player defender = game.getPlayer(creature.getControllerId());
                                if (defender != null) {
                                    for (UUID attackingCreatureId : getAttackers()) {
                                        if (creature.canBlock(attackingCreatureId, game)) {
                                            defender.declareBlocker(creature.getId(), attackingCreatureId, game);
                                            break;
                                        }
                                    }
                                }
                            }
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void setDefenders(Game game) {
        Set<UUID> opponents = game.getOpponents(attackerId);
        PlayerList players;
        switch (game.getAttackOption()) {
            case LEFT:
                players = game.getState().getPlayerList(attackerId);
                while (true) {
                    Player opponent = players.getNext(game);
                    if (opponents.contains(opponent.getId())) {
                        addDefender(opponent.getId(), game);
                        break;
                    }
                }
                break;
            case RIGHT:
                players = game.getState().getPlayerList(attackerId);
                while (true) {
                    Player opponent = players.getPrevious(game);
                    if (opponents.contains(opponent.getId())) {
                        addDefender(opponent.getId(), game);
                        break;
                    }
                }
                break;
            case MULTIPLE:
                for (UUID opponentId : game.getOpponents(attackerId)) {
                    addDefender(opponentId, game);
                }
                break;
        }
    }

    private void addDefender(UUID defenderId, Game game) {
        defenders.add(defenderId);
        for (Permanent permanent : game.getBattlefield().getAllActivePermanents(filterPlaneswalker, defenderId, game)) {
            defenders.add(permanent.getId());
        }
    }

    public void declareAttacker(UUID attackerId, UUID defenderId, Game game) {
        if (!defenders.contains(defenderId)) {
            return;
        }
        Permanent defender = game.getPermanent(defenderId);
        CombatGroup newGroup = new CombatGroup(defenderId, defender != null, defender != null ? defender.getControllerId(): defenderId);
        newGroup.attackers.add(attackerId);
        Permanent attacker = game.getPermanent(attackerId);
        if (!attacker.getAbilities().containsKey(VigilanceAbility.getInstance().getId())) {
            attacker.tap(game);
        }
        attacker.setAttacking(true);
        groups.add(newGroup);
    }

    // add blocking group for creatures that block more than one creature
    public void addBlockingGroup(UUID blockerId, UUID attackerId, UUID playerId, Game game) {
        Permanent blocker = game.getPermanent(blockerId);
        if (blockerId != null && blocker != null && blocker.getBlocking() > 1) {
            if (!blockingGroups.containsKey(blockerId)) {
                CombatGroup newGroup = new CombatGroup(playerId, false, playerId);
                newGroup.blockers.add(blockerId);
                // add all blocked attackers
                for (CombatGroup group : groups) {
                    if (group.getBlockers().contains(blockerId)) {
                        // take into account banding
                        for (UUID attacker : group.attackers) {
                            newGroup.attackers.add(attacker);
                        }
                    }
                }
                blockingGroups.put(blockerId, newGroup);
            } else {
                //TODO: handle banding
                blockingGroups.get(blockerId).attackers.add(attackerId);
            }
        }
    }

    public void removeFromCombat(UUID creatureId, Game game) {
        Permanent creature = game.getPermanent(creatureId);
        if (creature != null) {
            creature.setAttacking(false);
            creature.setBlocking(0);
            for (CombatGroup group : groups) {
                group.remove(creatureId);
            }
        }
    }

    public void endCombat(Game game) {
        Permanent creature;
        for (CombatGroup group : groups) {
            for (UUID attacker : group.attackers) {
                creature = game.getPermanent(attacker);
                if (creature != null) {
                    creature.setAttacking(false);
                    creature.setBlocking(0);
                }
            }
            for (UUID blocker : group.blockers) {
                creature = game.getPermanent(blocker);
                if (creature != null) {
                    creature.setAttacking(false);
                    creature.setBlocking(0);
                }
            }
        }
        clear();
    }

    public boolean hasFirstOrDoubleStrike(Game game) {
        for (CombatGroup group : groups) {
            if (group.hasFirstOrDoubleStrike(game)) {
                return true;
            }
        }
        return false;
    }

    public CombatGroup findGroup(UUID attackerId) {
        for (CombatGroup group : groups) {
            if (group.getAttackers().contains(attackerId)) {
                return group;
            }
        }
        return null;
    }

    public int totalUnblockedDamage(Game game) {
        int total = 0;
        for (CombatGroup group : groups) {
            if (group.getBlockers().isEmpty()) {
                total += group.totalAttackerDamage(game);
            }
        }
        return total;
    }

    public boolean attacksAlone() {
        return (groups.size() == 1 && groups.get(0).getAttackers().size() == 1);
    }

    public boolean noAttackers() {
        if (groups.isEmpty() || getAttackers().isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isAttacked(UUID defenderId, Game game) {
        for (CombatGroup group : groups) {
            if (group.getDefenderId().equals(defenderId)) {
                return true;
            }
            if (group.defenderIsPlaneswalker) {
                Permanent permanent = game.getPermanent(group.getDefenderId());
                if (permanent.getControllerId().equals(defenderId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public UUID getDefendingPlayer(UUID attackerId) {
        UUID defenderId = null;
        for (CombatGroup group : groups) {
            if (group.getAttackers().contains(attackerId)) {
                defenderId = group.getDefenderId();
                break;
            }
        }
        return defenderId;
    }

    public Set<UUID> getPlayerDefenders(Game game) {
        Set<UUID> playerDefenders = new HashSet<UUID>();
        for (CombatGroup group : groups) {
            if (group.defenderIsPlaneswalker) {
                Permanent permanent = game.getPermanent(group.getDefenderId());
                if (permanent != null) {
                    playerDefenders.add(permanent.getControllerId());
                }
            } else {
                playerDefenders.add(group.getDefenderId());
            }
        }
        return playerDefenders;
    }

    public void damageAssignmentOrder(Game game) {
        for (CombatGroup group : groups) {
            group.pickBlockerOrder(attackerId, game);
        }
        for (Map.Entry<UUID, CombatGroup> blockingGroup : blockingGroups.entrySet()) {
            Permanent blocker = game.getPermanent(blockingGroup.getKey());
            if (blocker != null) {
                blockingGroup.getValue().pickAttackerOrder(blocker.getControllerId(), game);
            }
        }
    }

    public void removeAttacker(UUID attackerId, Game game) {
        for (CombatGroup group : groups) {
            if (group.attackers.contains(attackerId)) {
                group.attackers.remove(attackerId);
                group.attackerOrder.remove(attackerId);
                Permanent creature = game.getPermanent(attackerId);
                if (creature != null) {
                    creature.setAttacking(false);
                    creature.setTapped(false);
                }
                if (group.attackers.isEmpty()) {
                    groups.remove(group);
                }
                return;
            }
        }
    }

    public void removeBlocker(UUID blockerId, Game game) {
        for (CombatGroup group : groups) {
            if (group.blockers.contains(blockerId)) {
                group.blockers.remove(blockerId);
                group.blockerOrder.remove(blockerId);
                if (group.blockers.isEmpty()) {
                    group.blocked = false;
                }
            }
        }
        Permanent creature = game.getPermanent(blockerId);
        if (creature != null) {
            creature.setBlocking(0);
        }
    }

    public UUID getAttackerId() {
        return attackerId;
    }

    @Override
    public Combat copy() {
        return new Combat(this);
    }

}
