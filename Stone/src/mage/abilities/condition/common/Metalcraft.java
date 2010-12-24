package mage.abilities.condition.common;

import mage.Constants;
import mage.abilities.Ability;
import mage.abilities.condition.Condition;
import mage.filter.FilterPermanent;
import mage.game.Game;

/**
 * Describes condition when Metacraft mechanic is turned on.
 *
 * @author nantuko
 */
public class Metalcraft implements Condition {

    private static FilterPermanent filter = new FilterPermanent("artifact");

    static {
        filter.getCardType().add(Constants.CardType.ARTIFACT);
    }

    private static Metalcraft fInstance = new Metalcraft();

    public static Condition getIntance() {
        return fInstance;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return game.getBattlefield().countAll(filter, source.getControllerId()) >= 3;
    }
}
