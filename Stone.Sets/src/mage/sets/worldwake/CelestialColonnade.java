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

package mage.sets.worldwake;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.common.EntersBattlefieldTappedAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.BecomesCreatureSourceEOTEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.abilities.keyword.VigilanceAbility;
import mage.abilities.mana.BlueManaAbility;
import mage.abilities.mana.WhiteManaAbility;
import mage.cards.CardImpl;
import mage.game.permanent.token.Token;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class CelestialColonnade extends CardImpl<CelestialColonnade> {

	public CelestialColonnade(UUID ownerId) {
		super(ownerId, 133, "Celestial Colonnade", Rarity.RARE, new CardType[]{CardType.LAND}, null);
		this.expansionSetCode = "WWK";
		this.addAbility(new EntersBattlefieldTappedAbility());
		this.addAbility(new BlueManaAbility());
		this.addAbility(new WhiteManaAbility());
		this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new BecomesCreatureSourceEOTEffect(new CelestialColonnadeToken(), "land"), new ManaCostsImpl("{3}{W}{U}")));
	}

	public CelestialColonnade(final CelestialColonnade card) {
		super(card);
	}

	@Override
	public CelestialColonnade copy() {
		return new CelestialColonnade(this);
	}

	@Override
	public String getArt() {
		return "126518_typ_reg_sty_010.jpg";
	}

}

class CelestialColonnadeToken extends Token {

	public CelestialColonnadeToken() {
		super("", "4/4 white and blue Elemental creature with flying and vigilance");
		cardType.add(CardType.CREATURE);
		subtype.add("Elemental");
		color.setBlue(true);
		color.setWhite(true);
		power = new MageInt(4);
		toughness = new MageInt(4);
		addAbility(FlyingAbility.getInstance());
		addAbility(VigilanceAbility.getInstance());
	}

}
