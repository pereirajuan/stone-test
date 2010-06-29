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

package mage.sets.magic2010;

import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Duration;
import mage.Constants.TargetController;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.RequirementAttackEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.Permanent;
import mage.sets.Magic2010;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class AlluringSiren extends CardImpl {

	public AlluringSiren(UUID ownerId) {
		super(ownerId, "Alluring Siren", new CardType[]{CardType.CREATURE}, "{1}{U}");
		this.expansionSetId = Magic2010.getInstance().getId();
		this.color.setBlue(true);
		this.subtype.add("Siren");
		this.art = "121568_typ_reg_sty_010.jpg";
		this.power = new MageInt(1);
		this.toughness = new MageInt(1);
		Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new AlluringSirenEffect(), new TapSourceCost());
		TargetCreaturePermanent target = new TargetCreaturePermanent();
		target.setTargetController(TargetController.OPPONENT);
		ability.addTarget(target);
		this.addAbility(ability);
	}

}

class AlluringSirenEffect extends RequirementAttackEffect {

	public AlluringSirenEffect() {
		super(Duration.OneUse);
	}

	@Override
	public boolean applies(GameEvent event, Game game) {
		if (event.getType().equals(EventType.DECLARE_ATTACKERS_STEP_PRE) && event.getPlayerId().equals(this.source.getFirstTarget()))
			return true;
		if (event.getType().equals(EventType.END_PHASE_POST) && event.getPlayerId().equals(this.source.getFirstTarget()))
			used = true;
		return false;
	}

	@Override
	public boolean apply(Game game) {
		Permanent creature = game.getPermanent(this.source.getFirstTarget());
		if (creature != null) {
			if (creature.canAttack(game)) {
				game.getCombat().declareAttacker(creature.getId(), this.source.getControllerId(), game);
			}
		}
		return false;
	}

	@Override
	public String getText() {
		return "Target creature an opponent controls attacks you this turn if able.";
	}
}