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

package mage.remote;

import mage.MageException;
import mage.cards.decks.DeckCardLists;
import mage.constants.Constants.SessionState;
import mage.game.GameException;
import mage.game.match.MatchOptions;
import mage.game.tournament.TournamentOptions;
import mage.interfaces.Action;
import mage.interfaces.MageClient;
import mage.interfaces.MageServer;
import mage.interfaces.ServerState;
import mage.interfaces.callback.ClientCallback;
import mage.utils.CompressUtil;
import mage.view.*;
import org.apache.log4j.Logger;
import org.jboss.remoting.*;
import org.jboss.remoting.callback.Callback;
import org.jboss.remoting.callback.HandleCallbackException;
import org.jboss.remoting.callback.InvokerCallbackHandler;
import org.jboss.remoting.transport.bisocket.Bisocket;
import org.jboss.remoting.transport.socket.SocketWrapper;
import org.jboss.remoting.transporter.TransporterClient;

import java.net.*;
import java.util.*;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class SessionImpl implements Session {

    private final static Logger logger = Logger.getLogger(SessionImpl.class);

    private String sessionId;
    private MageServer server;
    private MageClient client;
    private Client callbackClient;
    private ServerState serverState;
    private SessionState sessionState = SessionState.DISCONNECTED;
    private Connection connection;

    private Action embeddedMageServerAction;

    private static boolean debugMode = false;
    private static boolean standalone = true;

    private boolean canceled = false;

    static {
        debugMode = System.getProperty("debug.mage") != null;
        if (System.getProperty("skip.standalone") != null) {
            standalone = false;
        }
    }

    public SessionImpl(MageClient client) {
        this.client = client;
    }

    @Override
    public synchronized boolean connect(Connection connection) {
        if (isConnected()) {
            disconnect(true);
        }
        this.connection = connection;
        this.canceled = false;
        return connect();
    }

    @Override
    public boolean stopConnecting() {
        canceled = true;
        return true;
    }

    @Override
    public boolean connect() {

        /*if (standalone && connection.getHost().equals("localhost")) {
            runEmbeddedMageServer();
        }*/

        sessionState = SessionState.CONNECTING;
        try {
            System.setProperty("http.nonProxyHosts", "code.google.com");
            System.setProperty("socksNonProxyHosts", "code.google.com");

            // clear previous values
            System.clearProperty("socksProxyHost");
            System.clearProperty("socksProxyPort");
            System.clearProperty("http.proxyHost");
            System.clearProperty("http.proxyPort");

            switch (connection.getProxyType()) {
                case SOCKS:
                    System.setProperty("socksProxyHost", connection.getProxyHost());
                    System.setProperty("socksProxyPort", Integer.toString(connection.getProxyPort()));
                    break;
                case HTTP:
                    System.setProperty("http.proxyHost", connection.getProxyHost());
                    System.setProperty("http.proxyPort", Integer.toString(connection.getProxyPort()));
                    Authenticator.setDefault(new MageAuthenticator(connection.getProxyUsername(), connection.getProxyPassword()));
                    break;
            }
            InvokerLocator clientLocator = new InvokerLocator(connection.getURI());
            Map<String, String> metadata = new HashMap<String, String>();
            metadata.put(SocketWrapper.WRITE_TIMEOUT, "2000");
            metadata.put("generalizeSocketException", "true");
            server = (MageServer) TransporterClient.createTransporterClient(clientLocator.getLocatorURI(), MageServer.class, metadata);

            Map<String, String> clientMetadata = new HashMap<String, String>();
            clientMetadata.put(SocketWrapper.WRITE_TIMEOUT, "2000");
            clientMetadata.put("generalizeSocketException", "true");
            clientMetadata.put(Client.ENABLE_LEASE, "true");
            clientMetadata.put(Remoting.USE_CLIENT_CONNECTION_IDENTITY, "true");
            callbackClient = new Client(clientLocator, "callback", clientMetadata);

            Map<String, String> listenerMetadata = new HashMap<String, String>();
            if (debugMode) {
                // prevent client from disconnecting while debugging
                listenerMetadata.put(ConnectionValidator.VALIDATOR_PING_PERIOD, "1000000");
                listenerMetadata.put(ConnectionValidator.VALIDATOR_PING_TIMEOUT, "900000");
            } else {
                listenerMetadata.put(ConnectionValidator.VALIDATOR_PING_PERIOD, "10000");
                listenerMetadata.put(ConnectionValidator.VALIDATOR_PING_TIMEOUT, "9000");
            }
            callbackClient.connect(new ClientConnectionListener(), listenerMetadata);

            Map<String, String> callbackMetadata = new HashMap<String, String>();
            callbackMetadata.put(Bisocket.IS_CALLBACK_SERVER, "true");
            CallbackHandler callbackHandler = new CallbackHandler();
            callbackClient.addListener(callbackHandler, callbackMetadata);
            callbackClient.invoke("");

            this.sessionId = callbackClient.getSessionId();
            boolean registerResult = false;
            if (connection.getPassword() == null) {
                UserDataView userDataView = new UserDataView(connection.getAvatarId());
                // for backward compatibility. don't remove twice call - first one does nothing but for version checking
                registerResult = server.registerClient(connection.getUsername(), sessionId, client.getVersion());
                server.setUserData(connection.getUsername(), sessionId, userDataView);
            } else {
                registerResult = server.registerAdmin(connection.getPassword(), sessionId, client.getVersion());
            }
            if (registerResult) {
                sessionState = SessionState.CONNECTED;
                serverState = server.getServerState();
                logger.info("Connected to MAGE server at " + connection.getHost() + ":" + connection.getPort());
                client.connected("Connected to " + connection.getHost() + ":" + connection.getPort() + " ");
                return true;
            }
            disconnect(false);
            client.showMessage("Unable to connect to server.");
        } catch (MalformedURLException ex) {
            logger.fatal("", ex);
            client.showMessage("Unable to connect to server. "  + ex.getMessage());
        } catch (MageVersionException ex) {
            if (!canceled) {
                client.showMessage("Unable to connect to server. "  + ex.getMessage());
            }
            // TODO: download client that matches server version
        } catch (CannotConnectException ex) {
            if (!canceled) {
                handleCannotConnectException(ex);
            }
        } catch (Throwable t) {
            logger.fatal("Unable to connect to server - ", t);
            if (!canceled) {
                disconnect(false);
                client.showMessage("Unable to connect to server.  "  + t.getMessage());
            }
        }
        return false;
    }

    private void runEmbeddedMageServer() {
        if (embeddedMageServerAction != null) {
            try {
                embeddedMageServerAction.execute();
            } catch (MageException e) {
                logger.error(e);
            }
        }
    }

    private void handleCannotConnectException(CannotConnectException ex) {
        logger.warn("Cannot connect", ex);
        Throwable t = ex.getCause();
        String message = "";
        while (t != null) {
            if (t instanceof ConnectException) {
                message = "Server is likely offline.";
                break;
            }
            if (t instanceof SocketException) {
                message = "Check your internet connection.";
                break;
            }
            if (t instanceof SocketTimeoutException) {
                message = "Server is not responding.";
                break;
            }
            t = t.getCause();
        }
        client.showMessage("Unable to connect to server. " + message);
    }

    @Override
    public synchronized void disconnect(boolean showMessage) {
        if (isConnected())
            sessionState = SessionState.DISCONNECTING;
        if (connection == null)
            return;
        try {
            callbackClient.disconnect();
            TransporterClient.destroyTransporterClient(server);
        } catch (Throwable ex) {
            logger.fatal("Error disconnecting ...", ex);
        }
        if (sessionState == SessionState.DISCONNECTING || sessionState == SessionState.CONNECTING) {
            sessionState = SessionState.DISCONNECTED;
            logger.info("Disconnected ... ");
        }
        client.disconnected();
        if (showMessage)
            client.showError("Network error.  You have been disconnected");
    }

    @Override
    public synchronized boolean sendFeedback(String title, String type, String message, String email) {
        if (isConnected()) {
            try {
                server.sendFeedbackMessage(sessionId, connection.getUsername(), title, type, message, email);
                return true;
            } catch (MageException e) {
                logger.error(e);
            }
        }
        return false;
    }

    class CallbackHandler implements InvokerCallbackHandler {
        @Override
        public void handleCallback(Callback callback) throws HandleCallbackException {
            //logger.info("callback handler");
            client.processCallback((ClientCallback)callback.getCallbackObject());
        }
    }

    class ClientConnectionListener implements ConnectionListener {
        @Override
        public void handleConnectionException(Throwable throwable, Client client) {
            logger.info("connection to server lost - " + throwable.getMessage());
            disconnect(true);
        }
    }

    @Override
    public boolean isConnected() {
        if (callbackClient == null)
            return false;
        return callbackClient.isConnected();
    }

    @Override
    public String[] getPlayerTypes() {
        return serverState.getPlayerTypes();
    }

    @Override
    public List<GameTypeView> getGameTypes() {
        return serverState.getGameTypes();
    }

    @Override
    public String[] getDeckTypes() {
        return serverState.getDeckTypes();
    }

    @Override
    public List<TournamentTypeView> getTournamentTypes() {
        return serverState.getTournamentTypes();
    }

    @Override
    public boolean isTestMode() {
        if (serverState != null)
            return serverState.isTestMode();
        return false;
    }

    @Override
    public UUID getMainRoomId() {
        try {
            if (isConnected())
                return server.getMainRoomId();
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public UUID getRoomChatId(UUID roomId) {
        try {
            if (isConnected())
                return server.getRoomChatId(roomId);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public UUID getTableChatId(UUID tableId) {
        try {
            if (isConnected())
                return server.getTableChatId(tableId);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public UUID getGameChatId(UUID gameId) {
        try {
            if (isConnected())
                return server.getGameChatId(gameId);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public TableView getTable(UUID roomId, UUID tableId) {
        try {
            if (isConnected())
                return server.getTable(roomId, tableId);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public boolean watchTable(UUID roomId, UUID tableId) {
        try {
            if (isConnected()) {
                server.watchTable(sessionId, roomId, tableId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean joinTable(UUID roomId, UUID tableId, String playerName, String playerType, int skill, DeckCardLists deckList) {
        try {
            if (isConnected())
                return server.joinTable(sessionId, roomId, tableId, playerName, playerType, skill, deckList);
        } catch (GameException ex) {
            handleGameException(ex);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean joinTournamentTable(UUID roomId, UUID tableId, String playerName, String playerType, int skill) {
        try {
            if (isConnected())
                return server.joinTournamentTable(sessionId, roomId, tableId, playerName, playerType, skill);
        } catch (GameException ex) {
            handleGameException(ex);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public Collection<TableView> getTables(UUID roomId) throws MageRemoteException {
        try {
            if (isConnected())
                return server.getTables(roomId);
        } catch (MageException ex) {
            handleMageException(ex);
            throw new MageRemoteException();
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public Collection<MatchView> getFinishedMatches(UUID roomId) throws MageRemoteException {
        try {
            if (isConnected())
                return server.getFinishedMatches(roomId);
        } catch (MageException ex) {
            handleMageException(ex);
            throw new MageRemoteException();
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public Collection<String> getConnectedPlayers(UUID roomId) throws MageRemoteException {
        try {
            if (isConnected())
                return server.getConnectedPlayers(roomId);
        } catch (MageException ex) {
            handleMageException(ex);
            throw new MageRemoteException();
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public TournamentView getTournament(UUID tournamentId) throws MageRemoteException {
        try {
            if (isConnected())
                return server.getTournament(tournamentId);
        } catch (MageException ex) {
            handleMageException(ex);
            throw new MageRemoteException();
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public UUID getTournamentChatId(UUID tournamentId) {
        try {
            if (isConnected())
                return server.getTournamentChatId(tournamentId);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public boolean sendPlayerUUID(UUID gameId, UUID data) {
        try {
            if (isConnected()) {
                server.sendPlayerUUID(gameId, sessionId, data);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean sendPlayerBoolean(UUID gameId, boolean data) {
        try {
            if (isConnected()) {
                server.sendPlayerBoolean(gameId, sessionId, data);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean sendPlayerInteger(UUID gameId, int data) {
        try {
            if (isConnected()) {
                server.sendPlayerInteger(gameId, sessionId, data);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean sendPlayerString(UUID gameId, String data) {
        try {
            if (isConnected()) {
                server.sendPlayerString(gameId, sessionId, data);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public DraftPickView sendCardPick(UUID draftId, UUID cardId) {
        try {
            if (isConnected())
                return server.sendCardPick(draftId, sessionId, cardId);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public boolean joinChat(UUID chatId) {
        try {
            if (isConnected()) {
                server.joinChat(chatId, sessionId, connection.getUsername());
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean leaveChat(UUID chatId) {
//        lock.readLock().lock();
        try {
            if (isConnected()) {
                server.leaveChat(chatId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
//        } finally {
//            lock.readLock().unlock();
        }
        return false;
    }

    @Override
    public boolean sendChatMessage(UUID chatId, String message) {
//        lock.readLock().lock();
        try {
            if (isConnected()) {
                server.sendChatMessage(chatId, connection.getUsername(), message);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
//        } finally {
//            lock.readLock().unlock();
        }
        return false;
    }

    @Override
    public boolean sendBroadcastMessage(String message) {
        try {
            if (isConnected()) {
                server.sendBroadcastMessage(sessionId, message);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);
        }
        return false;
    }

    @Override
    public boolean joinGame(UUID gameId) {
        try {
            if (isConnected()) {
                server.joinGame(gameId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean joinDraft(UUID draftId) {
        try {
            if (isConnected()) {
                server.joinDraft(draftId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean joinTournament(UUID tournamentId) {
        try {
            if (isConnected()) {
                server.joinTournament(tournamentId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean watchGame(UUID gameId) {
        try {
            if (isConnected()) {
                server.watchGame(gameId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean replayGame(UUID gameId) {
        try {
            if (isConnected()) {
                server.replayGame(gameId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public TableView createTable(UUID roomId, MatchOptions matchOptions) {
        try {
            if (isConnected())
                return server.createTable(sessionId, roomId, matchOptions);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public TableView createTournamentTable(UUID roomId, TournamentOptions tournamentOptions) {
        try {
            if (isConnected())
                return server.createTournamentTable(sessionId, roomId, tournamentOptions);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public boolean isTableOwner(UUID roomId, UUID tableId) {
        try {
            if (isConnected())
                return server.isTableOwner(sessionId, roomId, tableId);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean removeTable(UUID roomId, UUID tableId) {
        try {
            if (isConnected()) {
                server.removeTable(sessionId, roomId, tableId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean removeTable(UUID tableId) {
        try {
            if (isConnected()) {
                server.removeTable(sessionId, tableId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean swapSeats(UUID roomId, UUID tableId, int seatNum1, int seatNum2) {
        try {
            if (isConnected()) {
                server.swapSeats(sessionId, roomId, tableId, seatNum1, seatNum2);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean leaveTable(UUID roomId, UUID tableId) {
        try {
            if (isConnected()) {
                server.leaveTable(sessionId, roomId, tableId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean startGame(UUID roomId, UUID tableId) {
        try {
            if (isConnected()) {
                server.startMatch(sessionId, roomId, tableId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean startTournament(UUID roomId, UUID tableId) {
        try {
            if (isConnected()) {
                server.startTournament(sessionId, roomId, tableId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean startChallenge(UUID roomId, UUID tableId, UUID challengeId) {
        try {
            if (isConnected()) {
                server.startChallenge(sessionId, roomId, tableId, challengeId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean submitDeck(UUID tableId, DeckCardLists deck) {
        try {
            if (isConnected())
                return server.submitDeck(sessionId, tableId, deck);
        } catch (GameException ex) {
            handleGameException(ex);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean updateDeck(UUID tableId, DeckCardLists deck) {
        try {
            if (isConnected()) {
                server.updateDeck(sessionId, tableId, deck);
                return true;
            }
        } catch (GameException ex) {
            handleGameException(ex);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean concedeGame(UUID gameId) {
        try {
            if (isConnected()) {
                server.concedeGame(gameId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean stopWatching(UUID gameId) {
        try {
            if (isConnected()) {
                server.stopWatching(gameId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean startReplay(UUID gameId) {
        try {
            if (isConnected()) {
                server.startReplay(gameId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean stopReplay(UUID gameId) {
        try {
            if (isConnected()) {
                server.stopReplay(gameId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean nextPlay(UUID gameId) {
        try {
            if (isConnected()) {
                server.nextPlay(gameId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean previousPlay(UUID gameId) {
        try {
            if (isConnected()) {
                server.previousPlay(gameId, sessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean skipForward(UUID gameId, int moves) {
        try {
            if (isConnected()) {
                server.skipForward(gameId, sessionId, moves);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public boolean cheat(UUID gameId, UUID playerId, DeckCardLists deckList) {
        try {
            if (isConnected()) {
                server.cheat(gameId, sessionId, playerId, deckList);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    @Override
    public List<UserView> getUsers() {
        try {
            if (isConnected())
                return server.getUsers(sessionId);
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return null;
    }

    @Override
    public List<String> getServerMessages() {
        try {
            if (isConnected())
                return (List<String>) CompressUtil.decompress(server.getServerMessagesCompressed(sessionId));
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);
        }
        return null;
    }

    @Override
    public boolean disconnectUser(String userSessionId) {
        try {
            if (isConnected()) {
                server.disconnectUser(sessionId, userSessionId);
                return true;
            }
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);        
        }
        return false;
    }

    private void handleThrowable(Throwable t) {
        logger.fatal("Communication error", t);
        sessionState = SessionState.SERVER_UNAVAILABLE;
        disconnect(true);
    }

    private void handleMageException(MageException ex) {
        logger.fatal("Server error", ex);
    }

    private void handleGameException(GameException ex) {
        logger.warn(ex.getMessage());
        client.showError(ex.getMessage());
    }


    @Override
    public String getUserName() {
        return connection.getUsername();
    }

    @Override
    public boolean updateAvatar(int avatarId) {
        try {
            if (isConnected()) {
                UserDataView userDataView = new UserDataView(avatarId);
                server.setUserData(connection.getUsername(), sessionId, userDataView);
            }
            return true;
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);
        }
        return false;
    }

    @Override
    public void setEmbeddedMageServerAction(Action embeddedMageServerAction) {
        this.embeddedMageServerAction = embeddedMageServerAction;
    }

    @Override
    public boolean ping() {
        try {
            if (isConnected()) {
                server.ping(sessionId);
            }
            return true;
        } catch (MageException ex) {
            handleMageException(ex);
        } catch (Throwable t) {
            handleThrowable(t);
        }
        return false;
    }
}

class MageAuthenticator extends Authenticator {

    private String username;
    private String password;

    public MageAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication () {
        return new PasswordAuthentication (username, password.toCharArray());
    }
}