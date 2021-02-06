package org.mage.test.cards.cost.modaldoublefaces;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestCommanderDuelBase;

/**
 * @author JayDi85
 */
public class ModalDoubleFacesCardsInCommanderTest extends CardTestCommanderDuelBase {

    @Test
    public void test_Triggers_MustAddTriggersOneTimeOnly() {
        // possible bug: duplicated triggers from same card
        // https://github.com/magefree/mage/issues/7501

        removeAllCardsFromHand(playerA);
        removeAllCardsFromLibrary(playerA);
        skipInitShuffling();

        // Esika, God of the Tree
        // creature, 1/4
        // Landfall — Whenever a land enters the battlefield under your control, Kazandu Mammoth gets +2/+2 until end of turn.
        //
        // The Prismatic Bridge
        // Enchantment
        // At the beginning of your upkeep, reveal cards from the top of your library until you reveal a creature or
        // planeswalker card. Put that card onto the battlefield and the rest on the bottom of your library in a random order.
        addCard(Zone.COMMAND, playerA, "Esika, God of the Tree"); // {W}{U}{B}{R}{G}
        addCard(Zone.BATTLEFIELD, playerA, "Plains");
        addCard(Zone.BATTLEFIELD, playerA, "Island");
        addCard(Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain");
        addCard(Zone.BATTLEFIELD, playerA, "Forest");
        //
        // last goes to top library
        addCard(Zone.LIBRARY, playerA, "Forest");
        addCard(Zone.LIBRARY, playerA, "Grizzly Bears");
        addCard(Zone.LIBRARY, playerA, "Forest");
        addCard(Zone.LIBRARY, playerA, "Grizzly Bears");
        addCard(Zone.LIBRARY, playerA, "Forest");
        addCard(Zone.LIBRARY, playerA, "Grizzly Bears");
        addCard(Zone.LIBRARY, playerA, "Forest");
        addCard(Zone.LIBRARY, playerA, "Grizzly Bears");
        addCard(Zone.LIBRARY, playerA, "Forest");
        addCard(Zone.LIBRARY, playerA, "Grizzly Bears");
        addCard(Zone.LIBRARY, playerA, "Forest");
        //
        // Exile target artifact or enchantment.
        addCard(Zone.HAND, playerB, "Ironwright's Cleansing"); // {2}{W}
        addCard(Zone.BATTLEFIELD, playerB, "Plains");

        // prepare mdf
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "The Prismatic Bridge");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);
        checkPermanentCount("prepare", 1, PhaseStep.PRECOMBAT_MAIN, playerA, "The Prismatic Bridge", 1);
        checkLibraryCount("prepare", 1, PhaseStep.PRECOMBAT_MAIN, playerA, "Grizzly Bears", 5);

        // possible bug: you can catch choose dialog for duplicated upkeep triggers

        // turn 3, first upkeep and bear move
        checkLibraryCount("after upkeep 1", 3, PhaseStep.PRECOMBAT_MAIN, playerA, "Grizzly Bears", 5 - 1);
        checkPermanentCount("after upkeep 1", 3, PhaseStep.PRECOMBAT_MAIN, playerA, "Grizzly Bears", 1);

        // turn 5, second upkeep and bear move
        checkLibraryCount("after upkeep 2", 5, PhaseStep.PRECOMBAT_MAIN, playerA, "Grizzly Bears", 5 - 2);
        checkPermanentCount("after upkeep 2", 5, PhaseStep.PRECOMBAT_MAIN, playerA, "Grizzly Bears", 2);

        setStrictChooseMode(true);
        setStopAt(5, PhaseStep.END_TURN);
        execute();
        assertAllCommandsUsed();
    }
}
