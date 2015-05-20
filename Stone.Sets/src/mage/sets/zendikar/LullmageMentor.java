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
package mage.sets.zendikar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapTargetCost;
import mage.abilities.effects.common.CounterTargetEffect;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.cards.CardImpl;
import mage.constants.WatcherScope;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.filter.predicate.permanent.TappedPredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.token.Token;
import mage.game.stack.StackObject;
import mage.target.Target;
import mage.target.TargetSpell;
import mage.target.common.TargetControlledCreaturePermanent;
import mage.watchers.Watcher;

/**
 *
 * @author LevelX2
 */
public class LullmageMentor extends CardImpl {
    private static final FilterControlledCreaturePermanent filter = new FilterControlledCreaturePermanent("untapped Merfolks you control");

    static {
        filter.add(new SubtypePredicate("Merfolk"));
        filter.add(Predicates.not(new TappedPredicate()));
    }
    public LullmageMentor(UUID ownerId) {
        super(ownerId, 54, "Lullmage Mentor", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}{U}{U}");
        this.expansionSetCode = "ZEN";
        this.subtype.add("Merfolk");
        this.subtype.add("Wizard");

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Whenever a spell or ability you control counters a spell, you may put a 1/1 blue Merfolk creature token onto the battlefield.
        this.addAbility(new LullmageMentorTriggeredAbility(), new CastedSpellsWithSpellTarget());
        // Tap seven untapped Merfolk you control: Counter target spell.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new CounterTargetEffect(), new TapTargetCost(new TargetControlledCreaturePermanent(7, 7, filter, true)));
        ability.addTarget(new TargetSpell());
        this.addAbility(ability);

    }

    public LullmageMentor(final LullmageMentor card) {
        super(card);
    }

    @Override
    public LullmageMentor copy() {
        return new LullmageMentor(this);
    }
}

class LullmageMentorTriggeredAbility extends TriggeredAbilityImpl {

    public LullmageMentorTriggeredAbility() {
        super(Zone.BATTLEFIELD, new CreateTokenEffect(new MerfolkToken()), false);
    }

    public LullmageMentorTriggeredAbility(final LullmageMentorTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public LullmageMentorTriggeredAbility copy() {
        return new LullmageMentorTriggeredAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getType() == EventType.COUNTERED) {
            CastedSpellsWithSpellTarget watcher = (CastedSpellsWithSpellTarget) game.getState().getWatchers().get("CastedSpellsWithSpellTarget");
            UUID controllerIdCounter = watcher.getControllerSpell(event.getSourceId(), event.getTargetId());
            if (controllerIdCounter != null && controllerIdCounter.equals(controllerId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return new StringBuilder("Whenever a spell or ability you control counters a spell, ").append(super.getRule()).toString();
    }
}

class MerfolkToken extends Token {

    public MerfolkToken() {
        super("Merfolk", "1/1 blue Merfolk creature token");
        cardType.add(CardType.CREATURE);
        color.setBlue(true);
        subtype.add("Merfolk");
        power = new MageInt(1);
        toughness = new MageInt(1);
    }
}

class CastedSpellsWithSpellTarget extends Watcher {

    private final Map<String, UUID> casted = new HashMap<>();

    public CastedSpellsWithSpellTarget() {
        super("CastedSpellsWithSpellTarget", WatcherScope.GAME);
    }

    public CastedSpellsWithSpellTarget(final CastedSpellsWithSpellTarget watcher) {
        super(watcher);
        for (Map.Entry<String, UUID> entry: watcher.casted.entrySet()) {
            casted.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.SPELL_CAST || event.getType() == GameEvent.EventType.ACTIVATED_ABILITY) {
            StackObject stackObject = game.getStack().getStackObject(event.getTargetId());
            if (stackObject == null) {
                stackObject = (StackObject) game.getLastKnownInformation(event.getTargetId(), Zone.STACK);
            }
            if (stackObject != null && stackObject.getStackAbility() != null) {
                for (Target target: stackObject.getStackAbility().getTargets()) {
                    if (target instanceof TargetSpell && target.getFirstTarget() != null) {
                        casted.put(getKey(target.getFirstTarget(), stackObject.getSourceId()), stackObject.getControllerId());
                    }
                }
            }
        }
    }

    private String getKey(UUID targetId, UUID sourceId) {
        return new StringBuilder(targetId.toString()).append("_").append(sourceId.toString()).toString();
    }

    public UUID getControllerSpell(UUID sourceId, UUID counteredSpell) {
        if (sourceId != null && counteredSpell != null){
            return casted.get(getKey(counteredSpell, sourceId));
        }
        return null;
    }

    @Override
    public void reset() {
        super.reset();
        casted.clear();
    }

    @Override
    public CastedSpellsWithSpellTarget copy() {
        return new CastedSpellsWithSpellTarget(this);
    }

}
