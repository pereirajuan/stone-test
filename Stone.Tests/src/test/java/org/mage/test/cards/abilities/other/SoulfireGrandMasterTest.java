/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */

package org.mage.test.cards.abilities.other;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Ignore;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 *
 * @author BetaSteward
 */
public class SoulfireGrandMasterTest extends CardTestPlayerBase {

    /** 
     * Soulfire Grand Master
     * Creature — Human Monk 2/2, 1W (2)
     * Lifelink
     * Instant and sorcery spells you control have lifelink.
     * {2}{U/R}{U/R}: The next time you cast an instant or sorcery spell from 
     * your hand this turn, put that card into your hand instead of into your 
     * graveyard as it resolves.
     *
     */

    @Ignore // at this time player.getPlayable() does not account for spells that gain abilities
    @Test
    public void testSpellsGainLifelink() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Soulfire Grand Master");
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertGraveyardCount(playerA, "Lightning Bolt", 1);
        assertHandCount(playerA, "Lightning Bolt", 0);
        assertLife(playerB, 17);
        assertLife(playerA, 23);

    }

    @Ignore // at this time player.getPlayable() does not account for spells that gain abilities
    @Test
    public void testSpellsReturnToHand() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.BATTLEFIELD, playerA, "Soulfire Grand Master");
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{2}{U/R}{U/R}:");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertGraveyardCount(playerA, "Lightning Bolt", 0);
        assertHandCount(playerA, "Lightning Bolt", 1);
        assertLife(playerA, 23);
        assertLife(playerB, 17);

    }
    
}
