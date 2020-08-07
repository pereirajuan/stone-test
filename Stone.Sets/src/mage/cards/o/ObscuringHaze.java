package mage.cards.o;

import mage.abilities.condition.common.ControlACommanderCondition;
import mage.abilities.costs.AlternativeCostSourceAbility;
import mage.abilities.effects.common.PreventAllDamageByAllObjectsEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.filter.FilterObject;
import mage.filter.common.FilterOpponentsCreaturePermanent;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class ObscuringHaze extends CardImpl {

    private static final FilterObject filter
            = new FilterOpponentsCreaturePermanent("creatures your opponents control");

    public ObscuringHaze(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{2}{G}");

        // If you control a commander, you may cast this spell without paying its mana cost.
        this.addAbility(new AlternativeCostSourceAbility(null, ControlACommanderCondition.instance));

        // Prevent all damage that would be dealt this turn by creatures your opponents control.
        this.getSpellAbility().addEffect(new PreventAllDamageByAllObjectsEffect(
                filter, Duration.EndOfTurn, false
        ));
    }

    private ObscuringHaze(final ObscuringHaze card) {
        super(card);
    }

    @Override
    public ObscuringHaze copy() {
        return new ObscuringHaze(this);
    }
}
