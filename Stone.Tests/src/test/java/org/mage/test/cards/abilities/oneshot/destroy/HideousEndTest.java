package org.mage.test.cards.abilities.oneshot.destroy;

import mage.Constants;
import org.junit.Ignore;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

public class HideousEndTest extends CardTestPlayerBase {

    @Test
    public void testWithValidTarget() {
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Constants.Zone.HAND, playerA, "Hideous End");
        addCard(Constants.Zone.BATTLEFIELD, playerB, "Copper Myr");

        castSpell(1, Constants.PhaseStep.PRECOMBAT_MAIN, playerA, "Hideous End", "Copper Myr");

        setStopAt(1, Constants.PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerB, "Copper Myr", 0);
        assertLife(playerB, 18);
    }

    @Test
    public void testWithInvalidTarget() {
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Constants.Zone.HAND, playerA, "Hideous End");
        addCard(Constants.Zone.BATTLEFIELD, playerB, "Zombie Goliath");

        castSpell(1, Constants.PhaseStep.PRECOMBAT_MAIN, playerA, "Hideous End", "Zombie Goliath");

        setStopAt(1, Constants.PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerB, "Zombie Goliath", 1);
        assertLife(playerB, 20);
    }

    @Test
    @Ignore
    public void testWithPossibleProtection() {
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Constants.Zone.HAND, playerA, "Hideous End");
        addCard(Constants.Zone.BATTLEFIELD, playerB, "Plains");
        addCard(Constants.Zone.BATTLEFIELD, playerB, "Plains");
        addCard(Constants.Zone.BATTLEFIELD, playerB, "Copper Myr");
        addCard(Constants.Zone.HAND, playerB, "Apostle's Blessing");

        castSpell(1, Constants.PhaseStep.PRECOMBAT_MAIN, playerA, "Hideous End", "Copper Myr");

        setStopAt(1, Constants.PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerB, "Copper Myr", 1);
        assertLife(playerB, 20);
    }
}
