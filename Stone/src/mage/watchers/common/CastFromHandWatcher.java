package mage.watchers.common;

import mage.constants.WatcherScope;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.stack.Spell;
import mage.watchers.Watcher;

public class CastFromHandWatcher extends Watcher<CastFromHandWatcher> {
    public CastFromHandWatcher() {
        super("CastFromHand", WatcherScope.CARD);
    }

    public CastFromHandWatcher(final CastFromHandWatcher watcher) {
        super(watcher);
    }

    @Override
    public void watch(GameEvent event, Game game) {
         if (event.getType() == GameEvent.EventType.SPELL_CAST && event.getZone() == Zone.HAND) {
            Spell spell = (Spell) game.getObject(event.getTargetId());
            if (this.getSourceId().equals(spell.getSourceId())) {
               condition = true;
            }
        }
    }

    @Override
    public CastFromHandWatcher copy() {
        return new CastFromHandWatcher(this);
    }
}
