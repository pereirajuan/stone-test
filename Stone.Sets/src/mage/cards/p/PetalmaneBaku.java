package mage.cards.p;

import java.util.UUID;
import mage.MageInt;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.common.SpellCastControllerTriggeredAbility;
import mage.abilities.costs.common.RemoveVariableCountersSourceCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.dynamicvalue.common.CountersSourceCount;
import mage.abilities.dynamicvalue.common.GetXValue;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.mana.DynamicManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.counters.CounterType;
import mage.filter.StaticFilters;

/**
 * @author LevelX2
 */
public final class PetalmaneBaku extends CardImpl {

    public PetalmaneBaku(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{1}{G}");
        this.subtype.add(SubType.SPIRIT);

        this.power = new MageInt(1);
        this.toughness = new MageInt(2);

        // Whenever you cast a Spirit or Arcane spell, you may put a ki counter on Petalmane Baku.
        this.addAbility(new SpellCastControllerTriggeredAbility(new AddCountersSourceEffect(CounterType.KI.createInstance()), StaticFilters.FILTER_SPELL_SPIRIT_OR_ARCANE, true));

        // {1}, Remove X ki counters from Petalmane Baku: Add X mana of any one color.
        Ability ability = new DynamicManaAbility(
                new Mana(0, 0, 0, 0, 0, 0, 1, 0),
                GetXValue.instance,
                new GenericManaCost(1),
                "Add X mana of any one color",
                true, new CountersSourceCount(CounterType.KI));
        ability.addCost(new RemoveVariableCountersSourceCost(CounterType.KI));
        this.addAbility(ability);
    }

    private PetalmaneBaku(final PetalmaneBaku card) {
        super(card);
    }

    @Override
    public PetalmaneBaku copy() {
        return new PetalmaneBaku(this);
    }
}
