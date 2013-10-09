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

import mage.MageException;
import mage.interfaces.callback.ClientCallback;
import mage.players.net.UserData;
import mage.players.net.UserGroup;
import mage.view.UserDataView;
import org.apache.log4j.Logger;
import org.jboss.remoting.callback.AsynchInvokerCallbackHandler;
import org.jboss.remoting.callback.Callback;
import org.jboss.remoting.callback.HandleCallbackException;
import org.jboss.remoting.callback.InvokerCallbackHandler;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mage.server.util.ConfigSettings;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class Session {

    private static final Logger logger = Logger.getLogger(Session.class);

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
        if (userName.equals("Admin")) {
            throw new MageException("User name already in use");
        }
        if (userName.length() > ConfigSettings.getInstance().getMaxUserNameLength()) {
            throw new MageException(new StringBuilder("User name may not be longer than ").append(ConfigSettings.getInstance().getMaxUserNameLength()).append(" characters").toString());
        }
        if (userName.length() < ConfigSettings.getInstance().getMinUserNameLength()) {
            throw new MageException(new StringBuilder("User name may not be shorter than ").append(ConfigSettings.getInstance().getMinUserNameLength()).append(" characters").toString());
        }
        Pattern p = Pattern.compile(ConfigSettings.getInstance().getUserNamePattern(), Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(userName);
        if (m.find()) {
           throw new MageException("User name '" + userName + "' includes not allowed characters: use a-z, A-Z and 0-9");
        }
        User user = UserManager.getInstance().createUser(userName, host);
        if (user == null) {  // user already exists
            user = UserManager.getInstance().findUser(userName);
            if (user.getHost().equals(host)) {
                if (user.getSessionId().isEmpty()) {
                    logger.info("Reconnecting session for " + userName);
                }
                else {
                    //throw new MageException("This machine is already connected");
                    //disconnect previous one
                    logger.info("Disconnecting another user instance: " + userName);
                    UserManager.getInstance().disconnect(user.getId());
                }
            }
            else {
                throw new MageException("User name " + userName + " already in use");
            }
        }
        if (!UserManager.getInstance().connectToSession(sessionId, user.getId())) {
            throw new MageException("Error connecting " + userName);
        }
        this.userId = user.getId();
    }

    public void registerAdmin() {
        this.isAdmin = true;
        User user = UserManager.getInstance().createUser("Admin", host);
        if (user == null) {
            user = UserManager.getInstance().findUser("Admin");
        }
        user.setUserData(new UserData(UserGroup.ADMIN, 0));
        this.userId = user.getId();
    }

    public boolean setUserData(String userName, UserDataView userDataView) {
        User user = UserManager.getInstance().findUser(userName);
        if (user != null) {
            UserData userData = new UserData(UserGroup.PLAYER, userDataView.getAvatarId());
            updateAvatar(userName, userData);
            user.setUserData(userData);
            return true;
        }
        return false;
    }

    private void updateAvatar(String userName, UserData userData) {
           //TODO: move to separate class
        //TODO: add for checking for private key
        if (userName.equals("nantuko")) {
            userData.setAvatarId(1000);
        } else if (userName.equals("i_no_k")) {
            userData.setAvatarId(1002);
        } else if (userName.equals("Askael")) {
            userData.setAvatarId(1004);
        } else if (userName.equals("North")) {
            userData.setAvatarId(1006);
        } else if (userName.equals("BetaSteward")) {
            userData.setAvatarId(1008);
        } else if (userName.equals("Arching")) {
            userData.setAvatarId(1010);
        } else if (userName.equals("loki")) {
            userData.setAvatarId(1012);
        } else if (userName.equals("Alive")) {
            userData.setAvatarId(1014);
        } else if (userName.equals("Rahan")) {
            userData.setAvatarId(1016);
        } else if (userName.equals("Ayrat")) {
            userData.setAvatarId(1018);
        }
    }

    public String getId() {
        return sessionId;
    }

    public void disconnect() {
        logger.info("session disconnected for user " + userId);
        UserManager.getInstance().disconnect(userId);
    }

    public void kill() {
        logger.info("session killed for user " + userId);
        UserManager.getInstance().removeUser(userId, User.DisconnectReason.Disconnected);
    }

    synchronized void fireCallback(final ClientCallback call) {
        try {
            call.setMessageId(messageId++);
            callbackHandler.handleCallbackOneway(new Callback(call));
        } catch (HandleCallbackException ex) {
            logger.fatal("Session fireCallback error: " + ex.getMessage(), ex);
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
