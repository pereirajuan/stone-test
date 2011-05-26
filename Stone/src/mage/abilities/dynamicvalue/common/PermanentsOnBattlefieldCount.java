package mage.abilities.dynamicvalue.common;

import mage.abilities.Ability;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.filter.FilterPermanent;
import mage.game.Game;

/**
 *
 * @author North
 */
public class PermanentsOnBattlefieldCount implements DynamicValue {

    private FilterPermanent filter;

    public PermanentsOnBattlefieldCount() {
        filter = new FilterPermanent();
    }

    public PermanentsOnBattlefieldCount(FilterPermanent filter) {
        this.filter = filter;
    }

    public PermanentsOnBattlefieldCount(final PermanentsOnBattlefieldCount dynamicValue) {
        this.filter = dynamicValue.filter;
    }

    @Override
    public int calculate(Game game, Ability sourceAbility) {
        return game.getBattlefield().countAll(filter);
    }

    @Override
    public DynamicValue clone() {
        return new PermanentsOnBattlefieldCount(this);
    }

    @Override
    public String toString() {
        return "X";
    }
}
