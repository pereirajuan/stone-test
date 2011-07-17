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

package mage.server;

import java.util.Date;
import java.util.UUID;
import mage.MageException;
import mage.interfaces.callback.ClientCallback;
import org.apache.log4j.Logger;
import org.jboss.remoting.callback.AsynchInvokerCallbackHandler;
import org.jboss.remoting.callback.Callback;
import org.jboss.remoting.callback.HandleCallbackException;
import org.jboss.remoting.callback.InvokerCallbackHandler;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class Session {

	private final static Logger logger = Logger.getLogger(Session.class);

	private String sessionId;
	private UUID userId;
	private String host;
	private int messageId = 0;
	private Date timeConnected;
	private boolean isAdmin = false;
	private AsynchInvokerCallbackHandler callbackHandler;

	public Session(String sessionId, InvokerCallbackHandler callbackHandler) {
		this.sessionId = sessionId;
		this.callbackHandler = (AsynchInvokerCallbackHandler) callbackHandler;
		this.isAdmin = false;
		this.timeConnected = new Date();
	}
	
	public void registerUser(String userName) throws MageException {
		this.isAdmin = false;
		if (userName.equals("Admin"))
			throw new MageException("User name already in use");
		User user = UserManager.getInstance().createUser(userName, host);
		if (user == null) {  // user already exists
			user = UserManager.getInstance().findUser(userName);
			if (user.getHost().equals(host)) {
				if (user.getSessionId().isEmpty())
					logger.info("Reconnecting session for " + userName);
				else
					throw new MageException("This machine is already connected");
			}
			else {
				throw new MageException("User name already in use");
			}
		}
		if (!UserManager.getInstance().connectToSession(sessionId, user.getId()))
			throw new MageException("Error connecting");
		this.userId = user.getId();
	}
	
	public void registerAdmin() {
		this.isAdmin = true;
		User user = UserManager.getInstance().createUser("Admin", host);
		this.userId = user.getId();
	}
	
	public String getId() {
		return sessionId;
	}
		
	public void disconnect() {
		UserManager.getInstance().disconnect(userId);
	}
	
	public void kill() {
		UserManager.getInstance().removeUser(userId);
	}
	
	synchronized void fireCallback(final ClientCallback call) {
		try {
			call.setMessageId(messageId++);
			callbackHandler.handleCallbackOneway(new Callback(call));
		} catch (HandleCallbackException ex) {
			logger.fatal("Session fireCallback error", ex);
			disconnect();
		}
	}

	public UUID getUserId() {
		return userId;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public String getHost() {
		return host;
	}
	
	public Date getConnectionTime() {
		return timeConnected;
	}

	void setHost(String hostAddress) {
		this.host = hostAddress;
	}
}
