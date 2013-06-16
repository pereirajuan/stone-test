package org.mage.test.cards.control;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author noxx
 */
public class ExchangeControlTest extends CardTestPlayerBase {

    /**
     * Tests switching controls for two creatures on different sides
     */
    @Test
    public void testSimpleExchange() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.HAND, playerA, "Switcheroo");

        addCard(Zone.BATTLEFIELD, playerA, "Llanowar Elves");
        addCard(Zone.BATTLEFIELD, playerB, "Elite Vanguard");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Switcheroo", "Llanowar Elves^Elite Vanguard");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        // check creatures changes their controllers
        assertPermanentCount(playerA, "Elite Vanguard", 1);
        assertPermanentCount(playerB, "Llanowar Elves", 1);
    }

    /**
     * Tests switching control for two creature on one side (both creatures are under the same player's control)
     *
     * Also tests "7/1/2012: You don't have to control either target."
     */
    @Test
    public void testOneSideExchange() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.HAND, playerA, "Switcheroo");

        addCard(Zone.BATTLEFIELD, playerB, "Llanowar Elves");
        addCard(Zone.BATTLEFIELD, playerB, "Elite Vanguard");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Switcheroo", "Llanowar Elves^Elite Vanguard");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        // check spell was cast
        assertGraveyardCount(playerA, "Switcheroo", 1);

        // check nothing happened
        assertPermanentCount(playerB, "Elite Vanguard", 1);
        assertPermanentCount(playerB, "Llanowar Elves", 1);
    }

    /**
     * Tests:
     *  7/1/2012: If one of the target creatures is an illegal target when Switcheroo resolves, the exchange won't happen.
     *
     * Targets opponent's creature
     */
    @Test
    public void testOneTargetBecomesIllegal() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.HAND, playerA, "Switcheroo");
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        addCard(Zone.BATTLEFIELD, playerA, "Llanowar Elves");
        addCard(Zone.BATTLEFIELD, playerB, "Elite Vanguard");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Switcheroo", "Llanowar Elves^Elite Vanguard");
        // cast in response
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Elite Vanguard");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertGraveyardCount(playerB, "Elite Vanguard", 1);

        // check nothing happened
        assertPermanentCount(playerA, "Llanowar Elves", 1);
    }

    /**
     * Tests:
     *  7/1/2012: If one of the target creatures is an illegal target when Switcheroo resolves, the exchange won't happen.
     *
     *  Targets its own creature.
     */
    @Test
    public void testOneTargetBecomesIllegal2() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.HAND, playerA, "Switcheroo");
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        addCard(Zone.BATTLEFIELD, playerA, "Llanowar Elves");
        addCard(Zone.BATTLEFIELD, playerB, "Elite Vanguard");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Switcheroo", "Llanowar Elves^Elite Vanguard");
        // cast in response
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Llanowar Elves");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertGraveyardCount(playerA, "Llanowar Elves", 1);

        // check nothing happened
        assertPermanentCount(playerB, "Elite Vanguard", 1);
    }

    /**
     * First gain control by Act of Treason.
     * Then exchange control with other opponent's creature.
     *
     * Finally second creature should stay under ours control permanently.
     */
    @Test
    public void testInteractionWithOtherChangeControlEffect() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.HAND, playerA, "Switcheroo");
        addCard(Zone.HAND, playerA, "Act of Treason");

        // both creatures on opponent's side
        addCard(Zone.BATTLEFIELD, playerB, "Llanowar Elves");
        addCard(Zone.BATTLEFIELD, playerB, "Elite Vanguard");

        // get control
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Act of Treason", "Elite Vanguard");

        // attack
        attack(1, playerA, "Elite Vanguard");

        // exchange control after combat
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Switcheroo", "Llanowar Elves^Elite Vanguard");

        // check the control effect still works on second turn
        setStopAt(2, PhaseStep.END_TURN);
        execute();

        // now it is our creature for ages
        assertPermanentCount(playerA, "Llanowar Elves", 1);
        // this one is still on opponent's side
        assertPermanentCount(playerB, "Elite Vanguard", 1);
    }

    /**
     * Tests switching controls will affect restriction effect
     */
    @Test
    public void testRestrictionEffect() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.HAND, playerA, "Switcheroo");

        addCard(Zone.BATTLEFIELD, playerA, "War Falcon");
        addCard(Zone.BATTLEFIELD, playerA, "Elite Vanguard");

        addCard(Zone.BATTLEFIELD, playerB, "Llanowar Elves");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Switcheroo", "War Falcon^Llanowar Elves");

        attack(2, playerB, "War Falcon");

        setStopAt(2, PhaseStep.END_TURN);
        execute();

        // check creatures changes their controllers
        assertPermanentCount(playerA, "Llanowar Elves", 1);
        assertPermanentCount(playerB, "War Falcon", 1);

        // War Falcon can't attack
        assertLife(playerA, 20);
    }
}
