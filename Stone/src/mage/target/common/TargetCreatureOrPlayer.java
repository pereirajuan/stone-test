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

package mage.target.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import mage.Constants.Zone;
import mage.MageObject;
import mage.abilities.Ability;
import mage.filter.Filter;
import mage.filter.common.FilterCreatureOrPlayer;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetImpl;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class TargetCreatureOrPlayer extends TargetImpl<TargetCreatureOrPlayer> {

	protected FilterCreatureOrPlayer filter;

	public TargetCreatureOrPlayer() {
		this(1, 1);
	}

	public TargetCreatureOrPlayer(int numTargets) {
		this(numTargets, numTargets);
	}

	public TargetCreatureOrPlayer(int minNumTargets, int maxNumTargets) {
		this.minNumberOfTargets = minNumTargets;
		this.maxNumberOfTargets = maxNumTargets;
		this.zone = Zone.ALL;
		this.filter = new FilterCreatureOrPlayer();
		this.targetName = filter.getMessage();
	}

	public TargetCreatureOrPlayer(final TargetCreatureOrPlayer target) {
		super(target);
		this.filter = target.filter.copy();
	}

	@Override
	public Filter getFilter() {
		return this.filter;
	}

	@Override
	public boolean canTarget(UUID id, Game game) {
		Permanent permanent = game.getPermanent(id);
		if (permanent != null) {
			return filter.match(permanent);
		}
		Player player = game.getPlayer(id);
		if (player != null)
			return filter.match(player);
		return false;
	}

	@Override
	public boolean canTarget(UUID id, Ability source, Game game) {
		Permanent permanent = game.getPermanent(id);
		MageObject targetSource = game.getObject(source.getSourceId());
		if (permanent != null) {
			if (source != null)
				return permanent.canBeTargetedBy(targetSource) && filter.match(permanent);
			else
				return filter.match(permanent);
		}
		Player player = game.getPlayer(id);
		if (player != null)
			if (source != null)
				return player.canBeTargetedBy(targetSource) && filter.match(player);
			else
				return filter.match(player);
		return false;
	}

	@Override
	public boolean canChoose(UUID sourceId, UUID sourceControllerId, Game game) {
		int count = 0;
		MageObject targetSource = game.getObject(sourceId);
		for (UUID playerId: game.getPlayer(sourceControllerId).getInRange()) {
			Player player = game.getPlayer(playerId);
			if (player != null && player.canBeTargetedBy(targetSource) && filter.match(player)) {
				count++;
				if (count >= this.minNumberOfTargets)
					return true;
			}
		}
		for (Permanent permanent: game.getBattlefield().getActivePermanents(FilterCreaturePermanent.getDefault(), sourceControllerId, game)) {
			if (permanent.canBeTargetedBy(targetSource) && filter.match(permanent)) {
				count++;
				if (count >= this.minNumberOfTargets)
					return true;
			}
		}
		return false;
	}

	@Override
	public List<UUID> possibleTargets(UUID sourceId, UUID sourceControllerId, Game game) {
		Map<Integer, UUID> possibleTargets = new HashMap<Integer, UUID>();
		MageObject targetSource = game.getObject(sourceId);
		for (UUID playerId: game.getPlayer(sourceControllerId).getInRange()) {
			Player player = game.getPlayer(playerId);
			if (player != null && player.canBeTargetedBy(targetSource) && filter.match(player)) {
				possibleTargets.put(player.hashCode(), playerId);
			}
		}
		for (Permanent permanent: game.getBattlefield().getActivePermanents(FilterCreaturePermanent.getDefault(), sourceControllerId, game)) {
			if (permanent.canBeTargetedBy(targetSource) && filter.match(permanent)) {
				possibleTargets.put(permanent.getValue().hashCode(), permanent.getId());
			}
		}
		return new ArrayList<UUID>(possibleTargets.values());
	}

	@Override
	public String getTargetedName(Game game) {
		StringBuilder sb = new StringBuilder();
		for (UUID targetId: getTargets()) {
			Permanent permanent = game.getPermanent(targetId);
			if (permanent != null) {
				sb.append(permanent.getName()).append(" ");
			}
			else {
				Player player = game.getPlayer(targetId);
				sb.append(player.getName()).append(" ");
			}
		}
		return sb.toString();
	}

	@Override
	public TargetCreatureOrPlayer copy() {
		return new TargetCreatureOrPlayer(this);
	}

}
