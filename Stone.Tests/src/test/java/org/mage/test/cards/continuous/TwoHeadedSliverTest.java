package org.mage.test.cards.continuous;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

import static junit.framework.TestCase.assertEquals;

/**
 * @author LevelX2
 */
public class TwoHeadedSliverTest extends CardTestPlayerBase {

    @Test
    public void testCantBeBlockedByOneEffectAbility() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 2);
        // All Sliver creatures have "This creature can't be blocked except by two or more creatures."
        addCard(Zone.HAND, playerA, "Two-Headed Sliver");

        addCard(Zone.BATTLEFIELD, playerB, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerB, "Plains");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Two-Headed Sliver");

        attack(3, playerA, "Two-Headed Sliver");
        // Block has to fail, because Two-Headed Sliver can't be blocked except by two or more creatures
        block(3, playerB, "Silvercoat Lion", "Two-Headed Sliver");

        setStopAt(3, PhaseStep.END_TURN);

        try {
            execute();
        } catch (UnsupportedOperationException e) {
            assertEquals("Two-Headed Sliver is blocked by 1 creature(s). It has to be blocked by 2 or more.", e.getMessage());
        }

    }
}
