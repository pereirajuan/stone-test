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

package mage.client.remote;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import mage.cards.decks.Deck;
import mage.client.MageFrame;
import mage.client.chat.ChatPanel;
import mage.client.constants.Constants.DeckEditorMode;
import mage.client.draft.DraftPanel;
import mage.client.game.GamePanel;
import mage.client.plugins.impl.Plugins;
import mage.client.util.AudioManager;
import mage.client.util.DeckUtil;
import mage.client.util.GameManager;
import mage.client.util.object.SaveObjectUtil;
import mage.interfaces.callback.CallbackClient;
import mage.interfaces.callback.ClientCallback;
import mage.utils.CompressUtil;
import mage.view.AbilityPickerView;
import mage.view.ChatMessage;
import mage.view.DeckView;
import mage.view.DraftClientMessage;
import mage.view.DraftView;
import mage.view.GameClientMessage;
import mage.view.GameEndView;
import mage.view.GameView;
import mage.view.TableClientMessage;
import org.apache.log4j.Logger;


/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class CallbackClientImpl implements CallbackClient {

    private static final Logger logger = Logger.getLogger(CallbackClientImpl.class);

    private UUID clientId;
    private MageFrame frame;
    private int messageId = 0;
    private boolean firstRun;

    public CallbackClientImpl(MageFrame frame) {
        this.clientId = UUID.randomUUID();
        this.frame = frame;
        this.firstRun = true;
    }

    @Override
    public synchronized void processCallback(final ClientCallback callback) {
        logger.info(callback.getMessageId() + " - " + callback.getMethod());
        SaveObjectUtil.saveObject(callback.getData(), callback.getMethod());
        callback.setData(CompressUtil.decompress(callback.getData()));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info(callback.getMessageId() + " -- " + callback.getMethod());
                    if (callback.getMethod().equals("startGame")) {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        GameManager.getInstance().setCurrentPlayerUUID(message.getPlayerId());
                        gameStarted(message.getGameId(), message.getPlayerId());
                    }
                    else if(callback.getMethod().equals("startTournament")) {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        tournamentStarted(message.getGameId(), message.getPlayerId());
                    }
                    else if(callback.getMethod().equals("startDraft")) {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        draftStarted(message.getGameId(), message.getPlayerId());
                    }
                    else if (callback.getMethod().equals("replayGame")) {
                        replayGame(callback.getObjectId());
                    }
                    else if (callback.getMethod().equals("showTournament")) {
                        showTournament((UUID) callback.getObjectId());
                    }
                    else if (callback.getMethod().equals("watchGame")) {
                        watchGame((UUID) callback.getObjectId());
                    }
                    else if (callback.getMethod().equals("chatMessage")) {
                        ChatMessage message = (ChatMessage) callback.getData();
                        ChatPanel panel = MageFrame.getChat(callback.getObjectId());
                        if (panel != null) {
                            // play the to the message connected sound
                            if (message.getSoundToPlay() != null) {
                                switch (message.getSoundToPlay()) {
                                    case PlayerLeft:
                                        AudioManager.playPlayerLeft();
                                        break;
                                    case  PlayerSubmittedDeck:
                                        AudioManager.playPlayerSubmittedDeck();
                                        break;
                                }
                            }
                            // send start message to chat if needed
                            if (!panel.isStartMessageDone()) {
                                createChatStartMessage(panel);
                            }
                            // send the message itself
                            if (message.isUserMessage() && panel.getConnectedChat() != null) {
                                panel.getConnectedChat().receiveMessage(message.getUsername(), message.getMessage(), message.getTime(), ChatMessage.MessageColor.BLACK);
                            } else {
                                panel.receiveMessage(message.getUsername(), message.getMessage(), message.getTime(), message.getColor());
                            }
                            
                        }
                    } else if (callback.getMethod().equals("serverMessage")) {
                        if (callback.getData() != null) {
                            ChatMessage message = (ChatMessage) callback.getData();
                            if (message.getColor().equals(ChatMessage.MessageColor.RED)) {
                                JOptionPane.showMessageDialog(null, message.getMessage(), "Server message", JOptionPane.WARNING_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, message.getMessage(), "Server message", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } else if (callback.getMethod().equals("joinedTable")) {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        joinedTable(message.getRoomId(), message.getTableId(), message.getFlag());
                    }
                    else if (callback.getMethod().equals("replayInit")) {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.init((GameView) callback.getData());
                        }
                    }
                    else if (callback.getMethod().equals("replayDone")) {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.endMessage((String) callback.getData(), callback.getMessageId());
                        }
                    }
                    else if (callback.getMethod().equals("replayUpdate")) {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.updateGame((GameView) callback.getData());
                        }
                    }
                    else if (callback.getMethod().equals("gameInit")) {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.init((GameView) callback.getData());
                        }
                    }
                    else if (callback.getMethod().equals("gameOver")) {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.endMessage((String) callback.getData(), callback.getMessageId());
                        }
                    }
                    else if (callback.getMethod().equals("gameError")) {
                        frame.showErrorDialog("Game Error", (String) callback.getData());
                    }
                    else if (callback.getMethod().equals("gameAsk")) {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.ask(message.getMessage(), message.getGameView(), callback.getMessageId());
                        }
                    }
                    else if (callback.getMethod().equals("gameTarget")) {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.pickTarget(message.getMessage(), message.getCardsView(), message.getGameView(),
                                    message.getTargets(), message.isFlag(), message.getOptions(), callback.getMessageId());
                        }
                    }
                    else if (callback.getMethod().equals("gameSelect")) {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.select(message.getMessage(), message.getGameView(), callback.getMessageId());
                        }
                    }
                    else if (callback.getMethod().equals("gameChooseAbility")) {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.pickAbility((AbilityPickerView) callback.getData());
                        }
                    }
                    else if (callback.getMethod().equals("gameChoosePile")) {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.pickPile(message.getMessage(), message.getPile1(), message.getPile2());
                        }
                    }
                    else if (callback.getMethod().equals("gameChoose")) {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.getChoice(message.getMessage(), message.getStrings());
                        }
                    }
                    else if (callback.getMethod().equals("gamePlayMana")) {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.playMana(message.getMessage(), message.getGameView(), callback.getMessageId());
                        }
                    }
                    else if (callback.getMethod().equals("gamePlayXMana")) {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.playXMana(message.getMessage(), message.getGameView(), callback.getMessageId());
                        }
                    }
                    else if (callback.getMethod().equals("gameSelectAmount")) {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.getAmount(message.getMin(), message.getMax(), message.getMessage());
                        }
                    }
                    else if (callback.getMethod().equals("gameUpdate")) {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.updateGame((GameView) callback.getData());
                        }
                    }
                    else if (callback.getMethod().equals("endGameInfo")) {
                        MageFrame.getInstance().showGameEndDialog((GameEndView) callback.getData());
                    }
                    else if (callback.getMethod().equals("showUserMessage")) {
                        List<String> messageData = (List<String>) callback.getData();
                        if (messageData.size() == 2) {
                            JOptionPane.showMessageDialog(null, messageData.get(1), messageData.get(0), JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    else if (callback.getMethod().equals("gameInform")) {

                        if (callback.getMessageId() > messageId) {
                            GameClientMessage message = (GameClientMessage) callback.getData();
                            GamePanel panel = MageFrame.getGame(callback.getObjectId());
                            if (panel != null) {
                                panel.inform(message.getMessage(), message.getGameView(), callback.getMessageId());
                            }
                        }
                        else {
                            logger.warn("message out of sequence - ignoring");
                        }
                    }
                    else if (callback.getMethod().equals("gameInformPersonal")) {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            JOptionPane.showMessageDialog(panel, message.getMessage(), "Game message",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    else if (callback.getMethod().equals("sideboard")) {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        DeckView deckView = message.getDeck();
                        Deck deck = DeckUtil.construct(deckView);
                        if (message.getFlag()) {
                            construct(deck, message.getTableId(), message.getTime());
                        }
                        else {
                            sideboard(deck, message.getTableId(), message.getTime());
                        }
                    }
                    else if (callback.getMethod().equals("construct")) {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        DeckView deckView = message.getDeck();
                        Deck deck = DeckUtil.construct(deckView);
                        construct(deck, message.getTableId(), message.getTime());
                    }
                    else if (callback.getMethod().equals("draftOver")) {
                        DraftPanel panel = MageFrame.getDraft(callback.getObjectId());
                        if (panel != null) {
                            panel.hideDraft();
                        }
                    }
                    else if (callback.getMethod().equals("draftPick")) {
                        DraftClientMessage message = (DraftClientMessage) callback.getData();
                        DraftPanel panel = MageFrame.getDraft(callback.getObjectId());
                        if (panel != null) {
                            panel.loadBooster(message.getDraftPickView());
                        }
                    }
                    else if (callback.getMethod().equals("draftUpdate")) {
                        DraftPanel panel = MageFrame.getDraft(callback.getObjectId());
                        if (panel != null) {
                            panel.updateDraft((DraftView) callback.getData());
                        }
                    }
                    else if (callback.getMethod().equals("draftInform")) {
                        if (callback.getMessageId() > messageId) {
                            DraftClientMessage message = (DraftClientMessage) callback.getData();
                        }
                        else {
                            logger.warn("message out of sequence - ignoring");
                        }
                    }
                    else if (callback.getMethod().equals("draftInit")) {
                        DraftClientMessage message = (DraftClientMessage) callback.getData();
                        DraftPanel panel = MageFrame.getDraft(callback.getObjectId());
                        if (panel != null) {
                            panel.loadBooster(message.getDraftPickView());
                        }
                    }
                    else if (callback.getMethod().equals("tournamentInit")) {

                    }
                    messageId = callback.getMessageId();
                }
                catch (Exception ex) {
                    handleException(ex);
                }
            }
        });
    }

    private void createChatStartMessage(ChatPanel chatPanel) {
        chatPanel.setStartMessageDone(true);
        ChatPanel usedPanel = chatPanel;
        if (chatPanel.getConnectedChat() != null) {
            usedPanel = chatPanel.getConnectedChat();
        }
        switch (usedPanel.getChatType()) {
            case GAME:
                usedPanel.receiveMessage("", "You may use hot keys to play faster: " + "" +
                        "\nTurn Mousewheel - Show big image of card your mousepointer hovers over" +
                        "\nF2 - Confirm \"Ok\", \"Yes\" or \"Done\" button" +
                        "\nF4 - Skip current turn but stop on declare attackers" +
                        "\nF9 - Skip everything until your next turn" +
                        "\nF3 - Undo F4/F9", "", ChatMessage.MessageColor.ORANGE);
                break;
            case TOURNAMENT:
                usedPanel.receiveMessage("", "On this panel you can see the players, their state and the results of the games of the tournament. Also you can chat with the competitors of the tournament.", "", ChatMessage.MessageColor.ORANGE);
                break;
            case TABLES:
                usedPanel.receiveMessage("",
                        "Download card images by using the \"Images\" menu to the top right ." +
                        "\nDownload icons and symbols by using the \"Symbols\" menu to the top right.",
                        "", ChatMessage.MessageColor.ORANGE);
                break;

        }      
    }

    public UUID getId() {
        return clientId;
    }

    private void joinedTable(UUID roomId, UUID tableId, boolean isTournament) {
        try {
            frame.showTableWaitingDialog(roomId, tableId, isTournament);
        }
         catch (Exception ex) {
            handleException(ex);
        }
   }

    protected void gameStarted(final UUID gameId, final UUID playerId) {
        try {
            frame.showGame(gameId, playerId);
            logger.info("Game " + gameId + " started for player " + playerId);
        }
        catch (Exception ex) {
            handleException(ex);
        }

        if (Plugins.getInstance().isCounterPluginLoaded()) {
            Plugins.getInstance().addGamesPlayed();
        }
    }

    protected void draftStarted(UUID draftId, UUID playerId) {
        try {
            frame.showDraft(draftId);
            logger.info("Draft " + draftId + " started for player " + playerId);
        }
        catch (Exception ex) {
            handleException(ex);
        }
    }

    protected void tournamentStarted(UUID tournamentId, UUID playerId) {
        try {
            frame.showTournament(tournamentId);
            logger.info("Tournament " + tournamentId + " started for player " + playerId);
        }
        catch (Exception ex) {
            handleException(ex);
        }
    }

    /**
     * Shows the tournament info panel for a tournament
     * 
     * @param tournamentId
     */
    protected void showTournament(UUID tournamentId) {
        try {
            frame.showTournament(tournamentId);
            logger.info("Showing tournament " + tournamentId);
        }
        catch (Exception ex) {
            handleException(ex);
        }
    }

    protected void watchGame(UUID gameId) {
        try {
            frame.watchGame(gameId);
            logger.info("Watching game " + gameId);
        }
        catch (Exception ex) {
            handleException(ex);
        }
    }
    protected void replayGame(UUID gameId) {
        try {
            frame.replayGame(gameId);
            logger.info("Replaying game");
        }
        catch (Exception ex) {
            handleException(ex);
        }
    }

    protected void sideboard(Deck deck, UUID tableId, int time) {
        frame.showDeckEditor(DeckEditorMode.Sideboard, deck, tableId, time);
    }

    protected void construct(Deck deck, UUID tableId, int time) {
        frame.showDeckEditor(DeckEditorMode.Limited, deck, tableId, time);
    }

    private void handleException(Exception ex) {
        logger.fatal("Client error\n", ex);
        frame.showError("Error: " + ex.getMessage());
    }

}
