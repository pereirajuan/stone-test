package org.mage.test.cards.split;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author JayDi85
 */
public class CastSplitCardsWithFlashbackTest extends CardTestPlayerBase {

    @Test
    public void test_Flashback_Simple() {
        // {1}{U}
        // When Snapcaster Mage enters the battlefield, target instant or sorcery card in your graveyard gains flashback until end of turn.
        addCard(Zone.HAND, playerA, "Snapcaster Mage", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        //
        addCard(Zone.GRAVEYARD, playerA, "Lightning Bolt", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 2);

        // add flashback
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Snapcaster Mage");
        addTarget(playerA, "Lightning Bolt");

        // cast as flashback
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Flashback", playerB);

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();
        assertAllCommandsUsed();

        assertLife(playerB, 20 - 3);
    }

    @Test
    public void test_Flashback_Split() {
        // {1}{U}
        // When Snapcaster Mage enters the battlefield, target instant or sorcery card in your graveyard gains flashback until end of turn.
        addCard(Zone.HAND, playerA, "Snapcaster Mage", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        //
        // Wear {1}{R} Destroy target artifact.
        // Tear {W} Destroy target enchantment.
        addCard(Zone.GRAVEYARD, playerA, "Wear // Tear", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerB, "Bident of Thassa", 1); // Legendary Enchantment Artifact
        addCard(Zone.BATTLEFIELD, playerB, "Bow of Nylea", 1); // Legendary Enchantment Artifact

        // add flashback
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Snapcaster Mage");
        addTarget(playerA, "Wear // Tear");

        // cast as flashback
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Flashback {1}{R}", "Bident of Thassa");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();
        assertAllCommandsUsed();

        assertGraveyardCount(playerB, "Bident of Thassa", 1);
        assertPermanentCount(playerB, "Bow of Nylea", 1);
    }
}
