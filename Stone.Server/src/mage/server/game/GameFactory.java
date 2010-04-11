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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mage.game.Game;
import mage.server.Main;
import mage.util.Logging;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class GameFactory {

	private final static GameFactory INSTANCE = new GameFactory();
	private final static Logger logger = Logging.getLogger(GameFactory.class.getName());

	private Map<String, Class> gameTypes = new HashMap<String, Class>();

	public static GameFactory getInstance() {
		return INSTANCE;
	}

	private GameFactory() {}

	public Game createGame(String gameType) {

		Game game;
		Constructor<?> con;
		try {
			con = gameTypes.get(gameType).getConstructor(new Class[]{});
			game = (Game)con.newInstance(new Object[] {});
		} catch (Exception ex) {
			logger.log(Level.SEVERE, null, ex);
			return null;
		}
		logger.info("Game created: " + game.getId().toString());

		return game;
	}

	public Set<String> getGameTypes() {
		return gameTypes.keySet();
	}

	public void addGameType(String name, Class gameType) {
		if (gameType != null)
			this.gameTypes.put(name, gameType);
	}

}
