package org.mage.test.ai.bugs;

import org.mage.test.cards.*;
import mage.Constants;
import mage.Constants.PhaseStep;
import org.junit.Ignore;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 *
 * @author BetaSteward
 */
public class TestHavengulLich extends CardTestPlayerBase {
    
    @Test
    public void testCard() {
        addCard(Constants.Zone.GRAVEYARD, playerA, "Gravecrawler");
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Black Cat");        
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        
        castSpell(1, Constants.PhaseStep.PRECOMBAT_MAIN, playerA, "Gravecrawler");
        setStopAt(1, Constants.PhaseStep.BEGIN_COMBAT);
        execute();
        
        assertLife(playerA, 20);
        assertLife(playerB, 20);
        assertPermanentCount(playerA, "Gravecrawler", 1);
        assertGraveyardCount(playerA, 0);
    }

    @Test
    public void testCard1() {
        addCard(Constants.Zone.GRAVEYARD, playerA, "Gravecrawler");
        addCard(Constants.Zone.BATTLEFIELD, playerA, "Swamp");
        
        castSpell(1, Constants.PhaseStep.PRECOMBAT_MAIN, playerA, "Gravecrawler");
        setStopAt(1, Constants.PhaseStep.BEGIN_COMBAT);
        execute();
        
        assertLife(playerA, 20);
        assertLife(playerB, 20);
        assertPermanentCount(playerA, "Gravecrawler", 0);
        assertGraveyardCount(playerA, 1);
    }
    
}
