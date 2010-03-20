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

package mage.target;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import mage.Constants.Zone;
import mage.abilities.Ability;
import mage.filter.Filter;
import mage.game.Game;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public interface Target extends Serializable {

	public boolean isChosen();
	public boolean doneChosing();
	public void clearChosen();
	public boolean canChoose(UUID sourceId, Game game);
	public String getMessage();
	public String getTargetName();
	public String getTargetedName(Game game);
	public Zone getZone();
	public boolean isLegal(Game game);
	public boolean canTarget(UUID id, Game game);
	public void addTarget(UUID id, Game game);

	public int getNumberOfTargets();
	public int getMaxNumberOfTargets();
	public List<UUID> getTargets();
	public Filter getFilter();
	public Ability getAbility();
	public void setAbility(Ability ability);
	
	public boolean isRequired();
	public void setRequired(boolean required);

	public UUID getLastTarget();
	public UUID getFirstTarget();
}
