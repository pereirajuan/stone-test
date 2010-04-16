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

package mage.server.game;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import mage.server.SessionManager;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ReplayManager {
	private final static ReplayManager INSTANCE = new ReplayManager();

	public static ReplayManager getInstance() {
		return INSTANCE;
	}

	private ReplayManager() {}

	private ConcurrentHashMap<UUID, ReplaySession> replaySessions = new ConcurrentHashMap<UUID, ReplaySession>();

	public void replayGame(UUID sessionId, UUID tableId) {
		ReplaySession replaySession = new ReplaySession(tableId, sessionId);
		replaySessions.put(sessionId, replaySession);
		SessionManager.getInstance().getSession(sessionId).replayGame();
	}

	public void startReplay(UUID sessionId) {
		replaySessions.get(sessionId).replay();
	}

	public void stopReplay(UUID sessionId) {
		replaySessions.get(sessionId).stop();
	}

	public void nextPlay(UUID sessionId) {
		replaySessions.get(sessionId).next();
	}

	public void previousPlay(UUID sessionId) {
		replaySessions.get(sessionId).previous();
	}

}
