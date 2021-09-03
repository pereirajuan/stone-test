package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.SetType;

/**
 * @author TheElk801
 */
public final class MidnightHuntCommander extends ExpansionSet {

    private static final MidnightHuntCommander instance = new MidnightHuntCommander();

    public static MidnightHuntCommander getInstance() {
        return instance;
    }

    private MidnightHuntCommander() {
        super("Midnight Hunt Commander", "MIC", ExpansionSet.buildDate(2021, 9, 24), SetType.SUPPLEMENTAL);
        this.hasBasicLands = false;
    }
}
