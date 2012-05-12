package org.mage.test.combat;

import junit.framework.Assert;
import mage.Constants;
import mage.game.permanent.Permanent;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * Test restrictions for choosing attackers and blockers.
 *
 * @author noxx
 */
public class AttackBlockRestrictionsTest extends CardTestPlayerBase {

    @Test
    public void testFlyingVsNonFlying() {
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Captain of the Mists");
        addCard(Constants.Zone.BATTLEFIELD, playerB, "Mist Raven");

        attack(2, playerB, "Mist Raven");
        block(2, playerA, "Captain of the Mists", "Mist Raven");

        setStopAt(2, Constants.PhaseStep.END_TURN);
        execute();

        assertLife(playerA, 18);
        assertLife(playerB, 20);

        Permanent mistRaven = getPermanent("Mist Raven", playerB.getId());
        Assert.assertTrue("Should be tapped because of attacking", mistRaven.isTapped());
    }

    /**
     * Tests attacking creature doesn't untap after being blocked by Wall of Frost
     */
    @Test
    public void testWallofWrost() {
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Wall of Frost");
        addCard(Constants.Zone.BATTLEFIELD, playerB, "Craw Wurm");

        attack(2, playerB, "Craw Wurm");
        block(2, playerA, "Wall of Frost", "Craw Wurm");

        setStopAt(4, Constants.PhaseStep.END_TURN);
        execute();

        assertLife(playerA, 20);
        assertLife(playerB, 20);

        Permanent crawWurm = getPermanent("Craw Wurm", playerB.getId());
        Assert.assertTrue("Should be tapped because of being blocked by Wall of Frost", crawWurm.isTapped());
    }

    /**
     * Tests card that says that it can't block specific cards
     * Hunted Ghoul:
     *   Hunted Ghoul can't block Humans.
     */
    @Test
    public void testFilteredBlocking() {
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Hunted Ghoul");
        addCard(Constants.Zone.BATTLEFIELD, playerB, "Elite Vanguard");

        attack(2, playerB, "Elite Vanguard");
        block(2, playerA, "Hunted Ghoul", "Elite Vanguard");

        setStopAt(4, Constants.PhaseStep.END_TURN);
        execute();

        assertLife(playerA, 18);
        assertLife(playerB, 20);
    }

    /**
     * Tests card that says that it can't block specific cards still can block others
     * Hunted Ghoul:
     *   Hunted Ghoul can't block Humans.
     */
    @Test
    public void testFilteredBlocking2() {
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Hunted Ghoul");
        addCard(Constants.Zone.BATTLEFIELD, playerB, "Craw Wurm");

        attack(2, playerB, "Craw Wurm");
        block(2, playerA, "Hunted Ghoul", "Craw Wurm");

        setStopAt(4, Constants.PhaseStep.END_TURN);
        execute();

        assertLife(playerA, 20);
        assertLife(playerB, 20);

        assertPermanentCount(playerA, "Hunted Ghoul", 0);
    }
}
