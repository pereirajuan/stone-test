/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mage.test.cards.abilities.keywords;

import junit.framework.Assert;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.filter.Filter;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 *     702.78. Persist
 *       702.78a Persist is a triggered ability. "Persist" means "When this permanent is put into a graveyard
 *       from the battlefield, if it had no -1/-1 counters on it, return it to the battlefield under its
 *       owner's control with a -1/-1 counter on it."
 *
 * @author LevelX2
 */

public class PersistTest extends CardTestPlayerBase {

    /**
     * Tests Safehold Elite don't returns from Persist if already a -1/-1 counter
     * was put on it from another source
     *
     */
    @Test
    public void testUndyingdoesntTriggerWithMinusCounter() {
        
        // Safehold Elite 2/2   {1}{G/W}
        // Creature - Elf Scout
        // 
        // Persist
        addCard(Zone.BATTLEFIELD, playerA, "Safehold Elite");

        // Put a -1/-1 counter on target creature. When that creature dies this turn, its controller gets a poison counter.
        addCard(Zone.HAND, playerB, "Virulent Wound",1);
        addCard(Zone.HAND, playerB, "Lightning Bolt",1);
        addCard(Zone.BATTLEFIELD, playerB, "Swamp", 1);
        addCard(Zone.BATTLEFIELD, playerB, "Mountain", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, "Virulent Wound", "Safehold Elite");
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerB, "Lightning Bolt", "Safehold Elite");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerA, "Safehold Elite", 0);
        assertGraveyardCount(playerA, "Safehold Elite", 1);

        // one poison counter from Virulent Wound
        Assert.assertEquals(1, playerA.getCounters().getCount(CounterType.POISON));
    }


    /**
     * If a card with persist is removed from a graveyard before the persist ability resolves, persist will do nothing.
     */
    @Test
    public void testWontTriggerIfPersistCardIsRemovedFromGraveyard() {

        // Safehold Elite 2/2   {1}{G/W}
        // Creature - Elf Scout
        //
        // Persist
        addCard(Zone.BATTLEFIELD, playerA, "Safehold Elite");

        // Exile target card from a graveyard. You gain 3 life.
        addCard(Zone.HAND, playerB, "Lightning Bolt",1);
        addCard(Zone.HAND, playerB, "Shadowfeed",1);

        addCard(Zone.BATTLEFIELD, playerB, "Swamp", 1);
        addCard(Zone.BATTLEFIELD, playerB, "Mountain", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, "Lightning Bolt", "Safehold Elite");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, "Shadowfeed", "Safehold Elite","Persist <i>(When this creature dies, if it had no -1/-1 counters on it, return it to the battlefield under its owner's control with a -1/-1 counter on it.)</i>");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertLife(playerB, 23);

        assertPermanentCount(playerA, "Safehold Elite", 0);
        assertGraveyardCount(playerA, "Safehold Elite", 0);

    }

    @Test
    public void testInteractionWithLifelink() {

        // Kitchen Finks 3/2   {1}{G/W}{G/W}
        // Creature - Ouphe
        // When Kitchen Finks enters the battlefield, you gain 2 life.
        // Persist (When this creature dies, if it had no -1/-1 counters on it, return it to the battlefield under its owner's control with a -1/-1 counter on it.)
        //
        // Persist
        addCard(Zone.BATTLEFIELD, playerA, "Kitchen Finks", 1);

        /**
         * Deathtouch, lifelink
         * When Wurmcoil Engine dies, put a 3/3 colorless Wurm artifact creature token with
         * deathtouch and a 3/3 colorless Wurm artifact creature token with lifelink onto the battlefield.
         */
        addCard(Zone.BATTLEFIELD, playerB, "Wurmcoil Engine",1);

        attack(2, playerB, "Wurmcoil Engine");
        block(2, playerA, "Kitchen Finks", "Wurmcoil Engine");

        setStopAt(2, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerB, "Wurmcoil Engine", 1);
        assertPermanentCount(playerA, "Kitchen Finks", 1);
        assertPowerToughness(playerA, "Kitchen Finks", 2,1);

        assertLife(playerA, 22); // Kitchen Finks +2 life
        assertLife(playerB, 26); // Wurmcoil Engine +6 ife

    }


    @Test
    public void testInteractionWithToporOrb() {

        // Kitchen Finks 3/2   {1}{G/W}{G/W}
        // Creature - Ouphe
        // When Kitchen Finks enters the battlefield, you gain 2 life.
        // Persist (When this creature dies, if it had no -1/-1 counters on it, return it to the battlefield under its owner's control with a -1/-1 counter on it.)
        //
        // Persist
        addCard(Zone.BATTLEFIELD, playerA, "Kitchen Finks", 2);

        /**
         * Deathtouch, lifelink
         * When Wurmcoil Engine dies, put a 3/3 colorless Wurm artifact creature token with
         * deathtouch and a 3/3 colorless Wurm artifact creature token with lifelink onto the battlefield.
         */
        addCard(Zone.BATTLEFIELD, playerB, "Wurmcoil Engine",1);
        addCard(Zone.BATTLEFIELD, playerB, "Torpor Orb",1);

        attack(2, playerB, "Wurmcoil Engine");
        block(2, playerA, "Kitchen Finks", "Wurmcoil Engine");
        block(2, playerA, "Kitchen Finks", "Wurmcoil Engine");

        setStopAt(2, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerB, "Wurmcoil Engine", 0);
        assertPermanentCount(playerB, "Wurm", 2);
        assertPermanentCount(playerA, "Kitchen Finks", 2);
        assertPowerToughness(playerA, "Kitchen Finks", 2,1, Filter.ComparisonScope.Any);
        assertPowerToughness(playerA, "Kitchen Finks", 3,2, Filter.ComparisonScope.Any);

        assertLife(playerA, 20); // No life from Kitchen Finks ETB becaus of Torpor Orb
        assertLife(playerB, 22); // AI assigns damage only 2 damage to one blocker so only 2 life link (It's a kind of bug (or bad play) of AI)

    }


    // some tests were moved to LastKnownInformationTest
}
