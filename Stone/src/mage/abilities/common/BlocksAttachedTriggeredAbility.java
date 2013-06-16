/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mage.abilities.common;

import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.Effect;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author garnold
 */
public class BlocksAttachedTriggeredAbility extends TriggeredAbilityImpl<BlocksAttachedTriggeredAbility>{
    private boolean setFixedTargetPointer;
    private String attachedDescription;

    public BlocksAttachedTriggeredAbility(Effect effect, String attachedDescription, boolean optional) {
        this(effect, attachedDescription, optional, false);
    }

    public BlocksAttachedTriggeredAbility(Effect effect, String attachedDescription, boolean optional, boolean setFixedTargetPointer) {
        super(Zone.BATTLEFIELD, effect, optional);
        this.setFixedTargetPointer = setFixedTargetPointer;
        this.attachedDescription = attachedDescription;
    }

    public BlocksAttachedTriggeredAbility(final BlocksAttachedTriggeredAbility ability) {
        super(ability);
        this.setFixedTargetPointer = ability.setFixedTargetPointer;
        this.attachedDescription = ability.attachedDescription;
    }

    @Override
    public BlocksAttachedTriggeredAbility copy() {
        return new BlocksAttachedTriggeredAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.BLOCKER_DECLARED) {
            Permanent p = game.getPermanent(event.getSourceId());
            if (p != null && p.getAttachments().contains(this.getSourceId())) {
                if (setFixedTargetPointer) {
                    for (Effect effect : this.getEffects()) {
                        effect.setTargetPointer(new FixedTarget(event.getPlayerId()));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever " + attachedDescription + " creature blocks, " + super.getRule();
    }
}
