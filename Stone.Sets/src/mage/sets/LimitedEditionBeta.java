package mage.sets;

import java.util.GregorianCalendar;

import mage.cards.ExpansionSet;
import mage.constants.SetType;

/**
 *
 * @author North
 */
public class LimitedEditionBeta extends ExpansionSet {

    private static final LimitedEditionBeta fINSTANCE = new LimitedEditionBeta();

    public static LimitedEditionBeta getInstance() {
        return fINSTANCE;
    }

    private LimitedEditionBeta() {
        super("Limited Edition Beta", "LEB", "mage.sets.limitedbeta", new GregorianCalendar(1993, 9, 1).getTime(), SetType.CORE);
        this.hasBoosters = true;
        this.numBoosterLands = 0;
        this.numBoosterCommon = 11;
        this.numBoosterUncommon = 3;
        this.numBoosterRare = 1;
        this.ratioBoosterMythic = 0;
    }
}
