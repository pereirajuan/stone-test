package mage.abilities.keyword;

import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ContinuousRuleModifiyingEffectImpl;
import mage.abilities.mana.ManaAbility;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;

/**
 * Split Second
 *
 * As long as this spell is on the stack, players can't cast other spells or activate abilities that aren't mana abilities.
 */

public class SplitSecondAbility extends SimpleStaticAbility  {

    public SplitSecondAbility() {
        super(Zone.STACK, new SplitSecondEffect());
        this.setRuleAtTheTop(true);
    }

    @Override
    public String getRule() {
        return "Split second <i>(As long as this spell is on the stack, players can't cast spells or activate abilities that aren't mana abilities.)</i>";
    }

    public SplitSecondAbility(SplitSecondAbility ability) {
        super(ability);
    }

    @Override
    public SimpleStaticAbility copy() {
          return new SplitSecondAbility(this);
    }
}

// Molten Disaster has a copy of this effect in it's class, so in case this effect has to be changed check also there

class SplitSecondEffect extends ContinuousRuleModifiyingEffectImpl {

    SplitSecondEffect() {
        super(Duration.WhileOnStack, Outcome.Detriment);
    }

    SplitSecondEffect(final SplitSecondEffect effect) {
        super(effect);
    }

    @Override
    public String getInfoMessage(Ability source, GameEvent event, Game game) {
        return "You can't cast spells or activate abilities that aren't mana abilities (Split second).";
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (event.getType() == GameEvent.EventType.CAST_SPELL) {
            return true;
        }
        if (event.getType() == GameEvent.EventType.ACTIVATE_ABILITY) {
            Ability ability = game.getAbility(event.getTargetId(), event.getSourceId());
            if (ability != null && !(ability instanceof ManaAbility)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return false;
    }

    @Override
    public SplitSecondEffect copy() {
        return new SplitSecondEffect(this);
    }
}
