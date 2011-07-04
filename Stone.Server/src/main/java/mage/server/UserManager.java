/*
 *  Copyright 2011 BetaSteward_at_googlemail.com. All rights reserved.
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
package mage.server;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class UserManager {

	private final static UserManager INSTANCE = new UserManager();

	public static UserManager getInstance() {
		return INSTANCE;
	}

	private UserManager() {}

	private ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<UUID, User>();

	public User createUser(String userName, String host) {
		if (findUser(userName) != null)
			return null; //user already exists
		User user = new User(userName, host);
		users.put(user.getId(), user);
		return user;
	}
	
	public User getUser(UUID userId) {
		return users.get(userId);
	}
	
	public User findUser(String userName) {
		for (User user: users.values()) {
			if (user.getName().equals(userName))
				return user;
		}
		return null;
	}
	
	public Collection<User> getUsers() {
		return users.values();
	}
		
	public boolean connectToSession(String sessionId, UUID userId) {
		if (users.containsKey(userId)) {
			users.get(userId).setSessionId(sessionId);
			return true;
		}
		return false;
	}
	
	public void disconnect(UUID userId) {
		if (users.containsKey(userId)) {
			users.get(userId).setSessionId("");
		}
	}
	
	public boolean isAdmin(UUID userId) {
		if (users.containsKey(userId)) {
			return users.get(userId).getName().equals("Admin");
		}
		return false;
	}
	
}
