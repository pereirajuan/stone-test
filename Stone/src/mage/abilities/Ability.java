/*
* Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are
* permitted provided that the following conditions are met:
*
*    1. Redistributions of source code must retain the above copyright notice, this list of
*       conditions and the following disclaimer.
*
*    2. Redistributions in binary form must reproduce the above copyright notice, this list
*       of conditions and the following disclaimer in the documentation and/or other materials
*       provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
* FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation are those of the
* authors and should not be interpreted as representing official policies, either expressed
* or implied, of BetaSteward_at_googlemail.com.
*/

package mage.abilities;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import mage.Constants.AbilityType;
import mage.Constants.EffectType;
import mage.Constants.Zone;
import mage.abilities.costs.AlternativeCost;
import mage.abilities.costs.Cost;
import mage.abilities.costs.Costs;
import mage.abilities.costs.mana.ManaCost;
import mage.abilities.costs.mana.ManaCosts;
import mage.abilities.effects.Effect;
import mage.abilities.effects.Effects;
import mage.choices.Choice;
import mage.choices.Choices;
import mage.game.Game;
import mage.target.Target;
import mage.target.Targets;

public interface Ability extends Serializable {

	public UUID getId();
	public void newId();
	public AbilityType getAbilityType();
	public UUID getControllerId();
	public UUID getSourceId();
	public Costs<Cost> getCosts();
	public void addCost(Cost cost);
	public ManaCosts<ManaCost> getManaCosts();
	public ManaCosts<ManaCost> getManaCostsToPay();
	public void addManaCost(ManaCost cost);
	public List<AlternativeCost> getAlternativeCosts();
	public void addAlternativeCost(AlternativeCost cost);
	public Costs<Cost> getOptionalCosts();
	public void addOptionalCost(Cost cost);
	public Effects getEffects();
	public Effects getEffects(EffectType effectType);
	public void addEffect(Effect effect);
	public Targets getTargets();
	public UUID getFirstTarget();
	public void addTarget(Target target);
	public Choices getChoices();
	public void addChoice(Choice choice);
	public Zone getZone();
	public boolean isUsesStack();
	public String getRule();
	public String getRule(boolean all);
	public String getRule(String source);
	public boolean activate(Game game, boolean noMana);
	public boolean resolve(Game game);
	public void reset(Game game);
	public boolean checkIfClause(Game game);

	public void setControllerId(UUID controllerId);
	public void setSourceId(UUID sourceID);

	public Ability copy();
}
