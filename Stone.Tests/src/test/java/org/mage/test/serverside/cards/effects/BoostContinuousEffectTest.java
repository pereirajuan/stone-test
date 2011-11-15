package org.mage.test.serverside.cards.effects;

import mage.filter.Filter;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestBase;

/**
 * Tests continuous boost effect like "White creatures you control get +1/+1".
 *
 * @author ayratn
 */
public class BoostContinuousEffectTest extends CardTestBase {

	@Test
	public void testHonorOfThePoor() throws Exception {
		load("M11/Honor of the Pure.test");
		execute();

		checkPermanentPT(playerA, "Tine Shrike", 3, 2, Filter.ComparisonScope.Any);
		checkPermanentPT(playerA, "Runeclaw Bear", 2, 2, Filter.ComparisonScope.Any);
	}
}
