
package mage.cards.c;

import mage.abilities.condition.common.KickedCondition;
import mage.abilities.decorator.ConditionalOneShotEffect;
import mage.abilities.effects.common.DamageEverythingEffect;
import mage.abilities.keyword.KickerAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.filter.StaticFilters;

import java.util.UUID;

/**
 * @author LoneFox
 */
public final class CanopySurge extends CardImpl {

    public CanopySurge(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{1}{G}");

        // Kicker {2}
        this.addAbility(new KickerAbility("{2}"));
        // Canopy Surge deals 1 damage to each creature with flying and each player. If Canopy Surge was kicked, it deals 4 damage to each creature with flying and each player instead.
        this.getSpellAbility().addEffect(new ConditionalOneShotEffect(new DamageEverythingEffect(4, StaticFilters.FILTER_CREATURE_FLYING),
                new DamageEverythingEffect(1, StaticFilters.FILTER_CREATURE_FLYING), KickedCondition.ONCE,
                "{this} deals 1 damage to each creature with flying and each player. If this spell was kicked, it deals 4 damage to each creature with flying and each player instead."));
    }

    private CanopySurge(final CanopySurge card) {
        super(card);
    }

    @Override
    public CanopySurge copy() {
        return new CanopySurge(this);
    }
}
