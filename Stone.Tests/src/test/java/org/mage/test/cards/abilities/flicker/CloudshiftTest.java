package org.mage.test.cards.abilities.flicker;

import mage.abilities.keyword.FirstStrikeAbility;
import mage.abilities.keyword.IntimidateAbility;
import mage.abilities.keyword.LifelinkAbility;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.game.permanent.Permanent;
import org.junit.Assert;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 *
 * @author noxx
 */
public class CloudshiftTest extends CardTestPlayerBase {

    /**
     * Tests that casting Cloudshift makes targeting spell fizzling
     */
    @Test
    public void testSpellFizzle() {
        addCard(Zone.BATTLEFIELD, playerA, "Elite Vanguard");
        addCard(Zone.BATTLEFIELD, playerA, "Plains");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain");

        addCard(Zone.HAND, playerA, "Cloudshift");
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Elite Vanguard");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Cloudshift", "Elite Vanguard");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        // should be alive because of Cloudshift
        assertPermanentCount(playerA, "Elite Vanguard", 1);
    }

    /**
     * Tests that copy effect is discarded and Clone can enter as a copy of another creature.
     * Also tests that copy two creature won't 'collect' abilities, after 'Cloudshift' effect Clone should enter as a copy of another creature.
     */
    @Test
    public void testCopyEffectDiscarded() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 4);

        addCard(Zone.BATTLEFIELD, playerB, "Knight of Meadowgrain");
        addCard(Zone.BATTLEFIELD, playerB, "Heirs of Stromkirk");

        addCard(Zone.HAND, playerA, "Clone");
        addCard(Zone.HAND, playerA, "Cloudshift");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Clone");
        setChoice(playerA, "Knight of Meadowgrain");
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Cloudshift", "Knight of Meadowgrain"); // clone has name of copied permanent
        setChoice(playerA, "Heirs of Stromkirk");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        Permanent clone = getPermanent("Heirs of Stromkirk", playerA.getId());
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone.getAbilities().contains(IntimidateAbility.getInstance()));
        Assert.assertFalse(clone.getAbilities().contains(LifelinkAbility.getInstance()));
        Assert.assertFalse(clone.getAbilities().contains(FirstStrikeAbility.getInstance()));
    }

    @Test
    public void testEquipmentDetached() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerA, "Bonesplitter");

        addCard(Zone.HAND, playerA, "Cloudshift");

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip {1}", "Silvercoat Lion");
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Cloudshift", "Silvercoat Lion");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        Permanent bonesplitter = getPermanent("Bonesplitter", playerA.getId());
        Permanent silvercoatLion = getPermanent("Silvercoat Lion", playerA.getId());

        assertLife(playerA, 20);
        Assert.assertTrue(silvercoatLion.getAttachments().isEmpty());
        Assert.assertTrue("Bonesplitter must not be connected to Silvercoat Lion",bonesplitter.getAttachedTo() == null);
        Assert.assertEquals("Silvercoat Lion's power without equipment has to be 2",2, silvercoatLion.getPower().getValue());
        Assert.assertEquals("Silvercoat Lion's toughness has to be 2",2, silvercoatLion.getToughness().getValue());
    }

    /**
     * Tests that casting Cloudshift makes creature able to block again
     * if it before was targeted with can't block effect
     *
     */
    @Test
    public void testCreatureCanBlockAgainAfterCloudshift() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains");
        addCard(Zone.BATTLEFIELD, playerA, "Timberland Guide");
        addCard(Zone.BATTLEFIELD, playerB, "Mountain", 3);

        addCard(Zone.HAND, playerA, "Cloudshift");
        addCard(Zone.HAND, playerB, "Fervent Cathar");

        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, "Fervent Cathar");
        addTarget(playerB, "Timberland Guide");
        attack(2, playerB, "Fervent Cathar");
        castSpell(2, PhaseStep.DECLARE_ATTACKERS, playerA, "Cloudshift", "Timberland Guide");
        block(2, playerA, "Timberland Guide", "Fervent Cathar");

        setStopAt(2, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertLife(playerA, 20);
        assertLife(playerB, 20);

        // blocked and therefore no more on the battlefield
        assertPermanentCount(playerB, "Fervent Cathar", 0);
        assertPermanentCount(playerA, "Timberland Guide", 0);


    }

    @Test
    public void testThatCardIsHandledAsNewInstanceAfterCloudshift() {
        // Whenever another creature enters the battlefield under your control, you gain life equal to that creature's toughness.
        // {1}{G}{W}, {T}: Populate. (Put a token onto the battlefield that's a copy of a creature token you control.)
        addCard(Zone.BATTLEFIELD, playerA, "Trostani, Selesnya's Voice");
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 4);
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);
        addCard(Zone.HAND, playerA, "Grizzly Bears"); // 2/2
        addCard(Zone.HAND, playerA, "Giant Growth");
        addCard(Zone.HAND, playerA, "Cloudshift");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Grizzly Bears");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Giant Growth", "Grizzly Bears", "you gain life equal to that creature's toughness");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Cloudshift", "Grizzly Bears", null, "you gain life equal to that creature's toughness");
        
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        
        execute();
        
        assertLife(playerA, 27); // 5 from the first with Giant Growth + 2 from the second bear.
    }
    
}
