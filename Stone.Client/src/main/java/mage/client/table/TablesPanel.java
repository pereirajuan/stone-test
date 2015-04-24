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

/*
 * TablesPanel.java
 *
 * Created on 15-Dec-2009, 10:54:01 PM
 */

package mage.client.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import mage.cards.decks.importer.DeckImporterUtil;
import mage.client.MageFrame;
import mage.client.chat.ChatPanel;
import mage.client.components.MageComponents;
import mage.client.deckeditor.table.TableModel;
import mage.client.dialog.JoinTableDialog;
import mage.client.dialog.NewTableDialog;
import mage.client.dialog.NewTournamentDialog;
import mage.client.dialog.PreferencesDialog;
import mage.client.dialog.TableWaitingDialog;
import mage.client.util.ButtonColumn;
import mage.client.util.MageTableRowSorter;
import mage.client.util.gui.GuiDisplayUtil;
import mage.constants.MatchTimeLimit;
import mage.constants.MultiplayerAttackOption;
import mage.constants.RangeOfInfluence;
import mage.game.match.MatchOptions;
import mage.remote.MageRemoteException;
import mage.remote.Session;
import mage.view.MatchView;
import mage.view.RoomUsersView;
import mage.view.TableView;
import org.apache.log4j.Logger;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class TablesPanel extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(TablesPanel.class);

    private TableTableModel tableModel;
    private MatchesTableModel matchesModel;
    private UUID roomId;
    private UpdateTablesTask updateTablesTask;
    private UpdatePlayersTask updatePlayersTask;
    private UpdateMatchesTask updateMatchesTask;
    private JoinTableDialog joinTableDialog;
    private NewTableDialog newTableDialog;
    private NewTournamentDialog newTournamentDialog;
    private GameChooser gameChooser;
    private Session session;
    private List<String> messages;
    private int currentMessage;
    private MageTableRowSorter activeTablesSorter;
    
    /** Creates new form TablesPanel */
    public TablesPanel() {

        tableModel = new TableTableModel();
        matchesModel = new MatchesTableModel();
        gameChooser = new GameChooser();

        initComponents();
        tableModel.setSession(session);

        
        filterStateBar.setBackground(new Color(250, 250, 250, 150));
        filterStateBar.setOpaque(true); // false = transparent

        filterTypeBar.setBackground(new Color(250, 250, 250, 150));
        filterTypeBar.setOpaque(true); // false = transparent

        filterFormatBar1.setBackground(new Color(250, 250, 250, 150));
        filterFormatBar1.setOpaque(true); // false = transparent

        filterFormatBar2.setBackground(new Color(250, 250, 250, 150));
        filterFormatBar2.setOpaque(true); // false = transparent

        tableTables.createDefaultColumnsFromModel();
        activeTablesSorter = new MageTableRowSorter(tableModel);
        tableTables.setRowSorter(activeTablesSorter);
        
        tableCompleted.setRowSorter(new MageTableRowSorter(matchesModel));

        chatPanel.useExtendedView(ChatPanel.VIEW_MODE.NONE);
        chatPanel.setBorder(null);
        chatPanel.setChatType(ChatPanel.ChatType.TABLES);

        JComponent[] components = new JComponent[] {chatPanel, jSplitPane1, jScrollPane1, jScrollPane2, jPanel1, jPanel3};
        for (JComponent component : components) {
            component.setOpaque(false);
        }

        jScrollPane1.getViewport().setBackground(new Color(255,255,255,50));
        jScrollPane2.getViewport().setBackground(new Color(255,255,255,50));
     
        Action openTableAction;
        openTableAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int modelRow = Integer.valueOf( e.getActionCommand() );
                UUID tableId = (UUID)tableModel.getValueAt(modelRow, TableTableModel.ACTION_COLUMN + 3);
                UUID gameId = (UUID)tableModel.getValueAt(modelRow, TableTableModel.ACTION_COLUMN + 2);
                String action = (String)tableModel.getValueAt(modelRow, TableTableModel.ACTION_COLUMN);
                String deckType = (String)tableModel.getValueAt(modelRow, TableTableModel.COLUMN_DECK_TYPE);
                String status = (String)tableModel.getValueAt(modelRow, TableTableModel.COLUMN_STATUS);
                boolean isTournament = (Boolean)tableModel.getValueAt(modelRow, TableTableModel.ACTION_COLUMN + 1);
                String owner = (String)tableModel.getValueAt(modelRow, 1);
                switch (action) {
                    case "Join":
                        if (owner.equals(session.getUserName())) {
                            try {
                                JDesktopPane desktopPane = (JDesktopPane) MageFrame.getUI().getComponent(MageComponents.DESKTOP_PANE);
                                JInternalFrame[] windows = desktopPane.getAllFramesInLayer(javax.swing.JLayeredPane.DEFAULT_LAYER);
                                for (JInternalFrame frame : windows) {
                                    if (frame.getTitle().equals("Waiting for players")) {
                                        frame.toFront();
                                        frame.setVisible(true);
                                        try {
                                            frame.setSelected(true);
                                        } catch (PropertyVetoException ve) {
                                            logger.error(ve);
                                        }
                                    }

                                }
                            } catch (InterruptedException ex) {
                                logger.error(ex);
                            }
                            return;
                        }
                        if (isTournament) {
                            logger.info("Joining tournament " + tableId);
                            if (deckType.startsWith("Limited")) {
                                if (!status.endsWith("PW")) {
                                    session.joinTournamentTable(roomId, tableId, session.getUserName(), "Human", 1, null, "");
                                } else {
                                    joinTableDialog.showDialog(roomId, tableId, true, deckType.startsWith("Limited"));
                                }
                            } else {
                                joinTableDialog.showDialog(roomId, tableId, true, deckType.startsWith("Limited"));
                            }
                        } else {
                            logger.info("Joining table " + tableId);
                            joinTableDialog.showDialog(roomId, tableId, false, false);
                        }  
                        break;
                    case "Remove":
                        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to remove table?", "Removing table", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            session.removeTable(roomId, tableId);
                        }  
                        break;
                    case "Show":
                        if (isTournament) {
                            logger.info("Showing tournament table " + tableId);
                            session.watchTable(roomId, tableId);
                        }  
                        break;
                    case "Watch":
                        if (!isTournament) {
                            logger.info("Watching table " + tableId);
                            session.watchTable(roomId, tableId);
                        }  
                        break;
                    case "Replay":
                        logger.info("Replaying game " + gameId);
                        session.replayGame(gameId);
                        break;
                }
             }
         };

        Action closedTableAction;        
        closedTableAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int modelRow = Integer.valueOf( e.getActionCommand() );
                String action = (String)matchesModel.getValueAt(modelRow, MatchesTableModel.ACTION_COLUMN);
                switch (action) {
                    case "Replay":                
                        List<UUID> gameList  = matchesModel.getListofGames(modelRow);
                        if (gameList != null && gameList.size() > 0) {
                            if (gameList.size() == 1) {
                                session.replayGame(gameList.get(0));
                            }
                            else {
                                gameChooser.show(gameList, MageFrame.getDesktop().getMousePosition());
                            }
                        }
                        // MageFrame.getDesktop().showTournament(tournamentId);
                        break;
                    case "Show":;
                        if (matchesModel.isTournament(modelRow)) {
                            logger.info("Showing tournament table " + matchesModel.getTableId(modelRow));
                            session.watchTable(roomId, matchesModel.getTableId(modelRow));
                        }                         
                        break;
                }
            }
        };
                
             
        // !!!! adds action buttons to the table panel (don't delete this)
        new ButtonColumn(tableTables, openTableAction, TableTableModel.ACTION_COLUMN);
        new ButtonColumn(tableCompleted, closedTableAction, MatchesTableModel.ACTION_COLUMN);
        // !!!!
    }


    private void saveDividerLocations() {
        // save panel sizes and divider locations.
        Rectangle rec = MageFrame.getDesktop().getBounds();
        String sb = Double.toString(rec.getWidth()) + "x" + Double.toString(rec.getHeight());
        PreferencesDialog.saveValue(PreferencesDialog.KEY_MAGE_PANEL_LAST_SIZE, sb);
        PreferencesDialog.saveValue(PreferencesDialog.KEY_TABLES_DIVIDER_LOCATION_1, Integer.toString(this.jSplitPane1.getDividerLocation()));
        PreferencesDialog.saveValue(PreferencesDialog.KEY_TABLES_DIVIDER_LOCATION_2, Integer.toString(this.jSplitPane2.getDividerLocation()));
        PreferencesDialog.saveValue(PreferencesDialog.KEY_TABLES_DIVIDER_LOCATION_3, Integer.toString(chatPanel.getSplitDividerLocation()));
    }

    private void restoreDividerLocations() {
        Rectangle rec = MageFrame.getDesktop().getBounds();
        if (rec != null) {
            String size = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_MAGE_PANEL_LAST_SIZE, null);
            String sb = Double.toString(rec.getWidth()) + "x" + Double.toString(rec.getHeight());
            // use divider positions only if screen size is the same as it was the time the settings were saved
            if (size != null && size.equals(sb)) {
                String location = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_TABLES_DIVIDER_LOCATION_1, null);
                if (location != null && jSplitPane1 != null) {
                    jSplitPane1.setDividerLocation(Integer.parseInt(location));
                }
                this.btnStateFinished.setSelected(PreferencesDialog.getCachedValue(PreferencesDialog.KEY_TABLES_SHOW_COMPLETED, "Yes").equals("Yes"));
                if (this.btnStateFinished.isSelected()) {
                    this.jSplitPane2.setDividerLocation(-1);
                }
                else {
                    location = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_TABLES_DIVIDER_LOCATION_2, null);
                    if (location != null && jSplitPane2 != null) {
                        jSplitPane2.setDividerLocation(Integer.parseInt(location));
                    }
                }                
                location = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_TABLES_DIVIDER_LOCATION_3, null);
                if (location != null && chatPanel != null) {
                    chatPanel.setSplitDividerLocation(Integer.parseInt(location));
                }
            }
        }
    }
    
    public Map<String, JComponent> getUIComponents() {
        Map<String, JComponent> components = new HashMap<>();

        components.put("jScrollPane1", jScrollPane1);
        components.put("jScrollPane1ViewPort", jScrollPane1.getViewport());
        components.put("jPanel1", jPanel1);
        components.put("tablesPanel", this);

        return components;
    }

    public void updateTables(Collection<TableView> tables) {
        try {
            tableModel.loadData(tables);
            this.tableTables.repaint();
        } catch (MageRemoteException ex) {
            hideTables();
        }
    }

    public void updateMatches(Collection<MatchView> matches) {
        try {
            matchesModel.loadData(matches);
            this.tableCompleted.repaint();
        } catch (MageRemoteException ex) {
            hideTables();
        }
    }

    public void startTasks() {
        if (session != null) {
            if (updateTablesTask == null || updateTablesTask.isDone()) {
                updateTablesTask = new UpdateTablesTask(session, roomId, this);
                updateTablesTask.execute();
            }
            if (updatePlayersTask == null || updatePlayersTask.isDone()) {
                updatePlayersTask = new UpdatePlayersTask(session, roomId, this.chatPanel);
                updatePlayersTask.execute();
            }
            if (this.btnStateFinished.isSelected()) {
                if (updateMatchesTask == null || updateMatchesTask.isDone()) {
                    updateMatchesTask = new UpdateMatchesTask(session, roomId, this);
                    updateMatchesTask.execute();
                }
            }
            else {
                   if (updateMatchesTask != null) {
                        updateMatchesTask.cancel(true);
                   }
            }
        }
    }

    public void stopTasks() {
        if (updateTablesTask != null) {
            updateTablesTask.cancel(true);
        }
        if (updatePlayersTask != null) {
            updatePlayersTask.cancel(true);
        }
        if (updateMatchesTask != null) {
            updateMatchesTask.cancel(true);
        }
    }

    public void showTables(UUID roomId) {
        this.roomId = roomId;
        session = MageFrame.getSession();
        UUID chatRoomId = null;
        if (session != null) {
            btnQuickStart.setVisible(session.isTestMode());
            gameChooser.init(session);
            chatRoomId = session.getRoomChatId(roomId);
        }
        if (newTableDialog == null) {
            newTableDialog = new NewTableDialog();
            MageFrame.getDesktop().add(newTableDialog, JLayeredPane.MODAL_LAYER);
        }
        if (newTournamentDialog == null) {
            newTournamentDialog = new NewTournamentDialog();
            MageFrame.getDesktop().add(newTournamentDialog, JLayeredPane.MODAL_LAYER);
        }
        if (joinTableDialog == null) {
            joinTableDialog = new JoinTableDialog();
            MageFrame.getDesktop().add(joinTableDialog, JLayeredPane.MODAL_LAYER);
        }
        if (chatRoomId != null) {
            this.chatPanel.connect(chatRoomId);
            startTasks();
            this.setVisible(true);
            this.repaint();
        } else {
            hideTables();
        }
        tableModel.setSession(session);

        reloadMessages();

        MageFrame.getUI().addButton(MageComponents.NEW_GAME_BUTTON, btnNewTable);
        
        // divider locations have to be set with delay else values set are overwritten with system defaults
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                restoreDividerLocations();
            }
        }, 300, TimeUnit.MILLISECONDS);       
        
    }

    protected void reloadMessages() {
        // reload server messages
        List<String> serverMessages = session.getServerMessages();
        synchronized (this) {
            this.messages = serverMessages;
            this.currentMessage = 0;
        }
        if (serverMessages == null || serverMessages.isEmpty()) {
            this.jPanel2.setVisible(false);
        } else {
            this.jPanel2.setVisible(true);
            this.jLabel2.setText(serverMessages.get(0));
            this.jButton1.setVisible(serverMessages.size() > 1);
        }
    }

    public void hideTables() {
        this.saveDividerLocations();
        for (Component component : MageFrame.getDesktop().getComponents()) {
            if (component instanceof TableWaitingDialog) {
                ((TableWaitingDialog)component).closeDialog();
            }
        }
        stopTasks();
        this.chatPanel.disconnect();

        Component c = this.getParent();
        while (c != null && !(c instanceof TablesPane)) {
            c = c.getParent();
        }
        if (c != null) {
            ((TablesPane)c).hideFrame();
        }
    }

    public ChatPanel getChatPanel() {
        return this.chatPanel;
    }

    private void activeTablesFilter() {
        // state
        StringBuilder stateFilter = new StringBuilder();
        if (btnStateWaiting.isSelected()) {
            stateFilter.append("Waiting");
        }
        if (btnStateActive.isSelected()) {
            if (stateFilter.length() > 0) {
                stateFilter.append("|");
            }            
            stateFilter.append("Dueling|Constructing|Drafting|Sideboard");
        }        
        RowFilter rf = RowFilter.regexFilter(stateFilter.toString(), 4);
        activeTablesSorter.setRowFilter(rf);        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnNewTable = new javax.swing.JButton();
        btnNewTournament = new javax.swing.JButton();
        filterStateBar = new javax.swing.JToolBar();
        btnStateWaiting = new javax.swing.JToggleButton();
        btnStateActive = new javax.swing.JToggleButton();
        btnStateFinished = new javax.swing.JToggleButton();
        filterTypeBar = new javax.swing.JToolBar();
        btnTypeMatch = new javax.swing.JToggleButton();
        btnTypeTourneyConstructed = new javax.swing.JToggleButton();
        btnTypeTourneyLimited = new javax.swing.JToggleButton();
        filterFormatBar1 = new javax.swing.JToolBar();
        btnFormatBlock = new javax.swing.JToggleButton();
        btnFormatStandard = new javax.swing.JToggleButton();
        btnFormatModern = new javax.swing.JToggleButton();
        btnFormatCommander = new javax.swing.JToggleButton();
        filterFormatBar2 = new javax.swing.JToolBar();
        btnFormatLegacy = new javax.swing.JToggleButton();
        btnFormatVintage = new javax.swing.JToggleButton();
        btnFormatOther = new javax.swing.JToggleButton();
        btnFormatTinyLeader = new javax.swing.JToggleButton();
        btnQuickStart = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        chatPanel = new mage.client.chat.ChatPanel(true);
        jPanel3 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableTables = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableCompleted = new javax.swing.JTable();

        btnNewTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/match_new.png"))); // NOI18N
        btnNewTable.setToolTipText("Creates a new match table.");
        btnNewTable.setMargin(new java.awt.Insets(10, 10, 10, 10));
        btnNewTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewTableActionPerformed(evt);
            }
        });

        btnNewTournament.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/tourney_new.png"))); // NOI18N
        btnNewTournament.setToolTipText("Creates a new tourney table.");
        btnNewTournament.setMargin(new java.awt.Insets(10, 10, 10, 10));
        btnNewTournament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewTournamentActionPerformed(evt);
            }
        });

        filterStateBar.setFloatable(false);
        filterStateBar.setForeground(new java.awt.Color(102, 102, 255));
        filterStateBar.setFocusable(false);

        btnStateWaiting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/state_waiting_off.png"))); // NOI18N
        btnStateWaiting.setSelected(true);
        btnStateWaiting.setToolTipText("Show tables waiting for players.");
        btnStateWaiting.setActionCommand("stateWait");
        btnStateWaiting.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnStateWaiting.setFocusPainted(false);
        btnStateWaiting.setFocusable(false);
        btnStateWaiting.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStateWaiting.setMaximumSize(new java.awt.Dimension(27, 27));
        btnStateWaiting.setMinimumSize(new java.awt.Dimension(27, 27));
        btnStateWaiting.setPreferredSize(new java.awt.Dimension(23, 23));
        btnStateWaiting.setRequestFocusEnabled(false);
        btnStateWaiting.setRolloverEnabled(false);
        btnStateWaiting.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/state_waiting.png"))); // NOI18N
        btnStateWaiting.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStateWaiting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStateWaitingActionPerformed(evt);
            }
        });
        filterStateBar.add(btnStateWaiting);

        btnStateActive.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/state_active.png"))); // NOI18N
        btnStateActive.setSelected(true);
        btnStateActive.setToolTipText("Show tables with active matches.");
        btnStateActive.setActionCommand("stateActive");
        btnStateActive.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnStateActive.setFocusPainted(false);
        btnStateActive.setFocusable(false);
        btnStateActive.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStateActive.setMaximumSize(new java.awt.Dimension(27, 27));
        btnStateActive.setMinimumSize(new java.awt.Dimension(27, 27));
        btnStateActive.setPreferredSize(new java.awt.Dimension(23, 23));
        btnStateActive.setRequestFocusEnabled(false);
        btnStateActive.setRolloverEnabled(false);
        btnStateActive.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/state_active.png"))); // NOI18N
        btnStateActive.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStateActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStateActiveActionPerformed(evt);
            }
        });
        filterStateBar.add(btnStateActive);

        btnStateFinished.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/state_finished.png"))); // NOI18N
        btnStateFinished.setSelected(true);
        btnStateFinished.setToolTipText("<HTML>Toggles the visibility of the table of completed <br>matches and tournaments in the lower area.\n<br>Showing the last 50 finished matches.");
        btnStateFinished.setActionCommand("stateFinished");
        btnStateFinished.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnStateFinished.setFocusPainted(false);
        btnStateFinished.setFocusable(false);
        btnStateFinished.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStateFinished.setMaximumSize(new java.awt.Dimension(27, 27));
        btnStateFinished.setMinimumSize(new java.awt.Dimension(27, 27));
        btnStateFinished.setPreferredSize(new java.awt.Dimension(23, 23));
        btnStateFinished.setRequestFocusEnabled(false);
        btnStateFinished.setRolloverEnabled(false);
        btnStateFinished.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/state_finished.png"))); // NOI18N
        btnStateFinished.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStateFinished.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStateFinishedActionPerformed(evt);
            }
        });
        filterStateBar.add(btnStateFinished);

        filterTypeBar.setFloatable(false);
        filterTypeBar.setFocusable(false);

        btnTypeMatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/type_match.png"))); // NOI18N
        btnTypeMatch.setSelected(true);
        btnTypeMatch.setActionCommand("typeMatch");
        btnTypeMatch.setFocusable(false);
        btnTypeMatch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTypeMatch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        filterTypeBar.add(btnTypeMatch);

        btnTypeTourneyConstructed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/type_tourney_construced.png"))); // NOI18N
        btnTypeTourneyConstructed.setSelected(true);
        btnTypeTourneyConstructed.setActionCommand("typeTourneyConstructed");
        btnTypeTourneyConstructed.setFocusable(false);
        btnTypeTourneyConstructed.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTypeTourneyConstructed.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        filterTypeBar.add(btnTypeTourneyConstructed);

        btnTypeTourneyLimited.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/type_tourney_limited.png"))); // NOI18N
        btnTypeTourneyLimited.setSelected(true);
        btnTypeTourneyLimited.setActionCommand("typeTourneyLimited");
        btnTypeTourneyLimited.setFocusable(false);
        btnTypeTourneyLimited.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTypeTourneyLimited.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        filterTypeBar.add(btnTypeTourneyLimited);

        filterFormatBar1.setFloatable(false);
        filterFormatBar1.setFocusable(false);

        btnFormatBlock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/format_block.png"))); // NOI18N
        btnFormatBlock.setSelected(true);
        btnFormatBlock.setFocusable(false);
        btnFormatBlock.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFormatBlock.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        filterFormatBar1.add(btnFormatBlock);

        btnFormatStandard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/format_standard.png"))); // NOI18N
        btnFormatStandard.setSelected(true);
        btnFormatStandard.setFocusable(false);
        btnFormatStandard.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFormatStandard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        filterFormatBar1.add(btnFormatStandard);

        btnFormatModern.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/format_modern.png"))); // NOI18N
        btnFormatModern.setSelected(true);
        filterFormatBar1.add(btnFormatModern);

        btnFormatCommander.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/format_commander.png"))); // NOI18N
        btnFormatCommander.setSelected(true);
        btnFormatCommander.setFocusable(false);
        btnFormatCommander.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFormatCommander.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        filterFormatBar1.add(btnFormatCommander);

        filterFormatBar2.setFloatable(false);
        filterFormatBar2.setToolTipText("All tables with active matches");
        filterFormatBar2.setFocusable(false);

        btnFormatLegacy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/format_legacy.png"))); // NOI18N
        btnFormatLegacy.setSelected(true);
        btnFormatLegacy.setFocusable(false);
        btnFormatLegacy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFormatLegacy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        filterFormatBar2.add(btnFormatLegacy);

        btnFormatVintage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/format_vintage.png"))); // NOI18N
        btnFormatVintage.setSelected(true);
        btnFormatVintage.setFocusable(false);
        btnFormatVintage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFormatVintage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        filterFormatBar2.add(btnFormatVintage);

        btnFormatOther.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/format_other.png"))); // NOI18N
        btnFormatOther.setSelected(true);
        filterFormatBar2.add(btnFormatOther);

        btnFormatTinyLeader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/buttons/format_tiny_leader.png"))); // NOI18N
        btnFormatTinyLeader.setSelected(true);
        filterFormatBar2.add(btnFormatTinyLeader);

        btnQuickStart.setText("Quick Start");
        btnQuickStart.setFocusable(false);
        btnQuickStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnQuickStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnQuickStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNewTable)
                .addGap(6, 6, 6)
                .addComponent(btnNewTournament)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(filterStateBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filterTypeBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(filterFormatBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterFormatBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnQuickStart)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnNewTable)
                        .addComponent(btnNewTournament))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(filterStateBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(filterTypeBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(filterFormatBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnQuickStart))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(filterFormatBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setPreferredSize(new java.awt.Dimension(664, 39));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Message of the Day:");
        jLabel1.setAlignmentY(0.3F);

        jLabel2.setText("You are playing Mage version 0.7.5. Welcome! -- Mage dev team --");

        jButton1.setText("Next");
        jButton1.setMaximumSize(new java.awt.Dimension(55, 25));
        jButton1.setMinimumSize(new java.awt.Dimension(55, 25));
        jButton1.setPreferredSize(new java.awt.Dimension(55, 25));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerSize(10);
        jSplitPane1.setResizeWeight(1.0);

        chatPanel.setMinimumSize(new java.awt.Dimension(100, 43));
        jSplitPane1.setRightComponent(chatPanel);

        jSplitPane2.setBorder(null);
        jSplitPane2.setDividerSize(10);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(0.5);

        jScrollPane1.setBorder(null);

        tableTables.setModel(this.tableModel);
        jScrollPane1.setViewportView(tableTables);

        jSplitPane2.setLeftComponent(jScrollPane1);

        jScrollPane2.setBorder(null);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(23, 0));

        tableCompleted.setModel(this.matchesModel);
        jScrollPane2.setViewportView(tableCompleted);

        jSplitPane2.setRightComponent(jScrollPane2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 908, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 908, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSplitPane1)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 598, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

        private void btnNewTournamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewTournamentActionPerformed
            newTournamentDialog.showDialog(roomId);
}//GEN-LAST:event_btnNewTournamentActionPerformed

        private void btnQuickStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickStartActionPerformed
            TableView table;
            try {
                File f = new File("test.dck");
                if (!f.exists()) {
                    JOptionPane.showMessageDialog(null, "Couldn't find test.dck file for quick game start", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                MatchOptions options = new MatchOptions("1", "Two Player Duel");
                options.getPlayerTypes().add("Human");
                options.getPlayerTypes().add("Computer - mad");
                options.setDeckType("Limited");
                options.setAttackOption(MultiplayerAttackOption.LEFT);
                options.setRange(RangeOfInfluence.ALL);
                options.setWinsNeeded(1);
                options.setMatchTimeLimit(MatchTimeLimit.NONE);
                options.setFreeMulligans(2);
                table = session.createTable(roomId,    options);

                session.joinTable(roomId, table.getTableId(), "Human", "Human", 1, DeckImporterUtil.importDeck("test.dck"),"");
                session.joinTable(roomId, table.getTableId(), "Computer", "Computer - mad", 5, DeckImporterUtil.importDeck("test.dck"),"");
                session.startMatch(roomId, table.getTableId());
            } catch (HeadlessException ex) {
                handleError(ex);
            }
}//GEN-LAST:event_btnQuickStartActionPerformed

    private void btnNewTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewTableActionPerformed
        newTableDialog.showDialog(roomId);
    }//GEN-LAST:event_btnNewTableActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        synchronized (this) {
            if (messages != null && !messages.isEmpty()) {
                currentMessage++;
                if (currentMessage >= messages.size()) {
                    currentMessage = 0;
                }
                this.jLabel2.setText(messages.get(currentMessage));
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnStateWaitingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStateWaitingActionPerformed
        activeTablesFilter();
    }//GEN-LAST:event_btnStateWaitingActionPerformed

    private void btnStateActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStateActiveActionPerformed
        activeTablesFilter();
    }//GEN-LAST:event_btnStateActiveActionPerformed

    private void btnStateFinishedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStateFinishedActionPerformed
        if (this.btnStateFinished.isSelected()) {
            this.jSplitPane2.setDividerLocation(-1);
        }
        else {
            this.jSplitPane2.setDividerLocation(this.jPanel3.getHeight());
        }
        PreferencesDialog.saveValue(PreferencesDialog.KEY_TABLES_SHOW_COMPLETED, (btnStateFinished.isSelected()?"Yes":"No"));
        this.startTasks();
    }//GEN-LAST:event_btnStateFinishedActionPerformed

    private void handleError(Exception ex) {
        logger.fatal("Error loading deck: ", ex);
        JOptionPane.showMessageDialog(MageFrame.getDesktop(), "Error loading deck.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnFormatBlock;
    private javax.swing.JToggleButton btnFormatCommander;
    private javax.swing.JToggleButton btnFormatLegacy;
    private javax.swing.JToggleButton btnFormatModern;
    private javax.swing.JToggleButton btnFormatOther;
    private javax.swing.JToggleButton btnFormatStandard;
    private javax.swing.JToggleButton btnFormatTinyLeader;
    private javax.swing.JToggleButton btnFormatVintage;
    private javax.swing.JButton btnNewTable;
    private javax.swing.JButton btnNewTournament;
    private javax.swing.JButton btnQuickStart;
    private javax.swing.JToggleButton btnStateActive;
    private javax.swing.JToggleButton btnStateFinished;
    private javax.swing.JToggleButton btnStateWaiting;
    private javax.swing.JToggleButton btnTypeMatch;
    private javax.swing.JToggleButton btnTypeTourneyConstructed;
    private javax.swing.JToggleButton btnTypeTourneyLimited;
    private mage.client.chat.ChatPanel chatPanel;
    private javax.swing.JToolBar filterFormatBar1;
    private javax.swing.JToolBar filterFormatBar2;
    private javax.swing.JToolBar filterStateBar;
    private javax.swing.JToolBar filterTypeBar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTable tableCompleted;
    private javax.swing.JTable tableTables;
    // End of variables declaration//GEN-END:variables
   
}

class TableTableModel extends AbstractTableModel {

    public static final int COLUMN_DECK_TYPE = 0; // column the deck type is located (starting with 0) Start string is used to check for Limited
    public static final int COLUMN_INFO = 3;
    public static final int COLUMN_STATUS = 4;
    public static final int ACTION_COLUMN = 6; // column the action is located (starting with 0)

    private final String[] columnNames = new String[]{"Deck Type", "Owner / Players", "Game Type", "Info", "Status", "Created / Started", "Action"};
    private TableView[] tables = new TableView[0];
    private static final DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");;

    private Session session;
    
    public void loadData(Collection<TableView> tables) throws MageRemoteException {
        this.tables = tables.toArray(new TableView[0]);
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return tables.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public Object getValueAt(int arg0, int arg1) {
        switch (arg1) {
            case 0:
                return tables[arg0].getDeckType();
            case 1:
                return tables[arg0].getControllerName();
            case 2:
                return tables[arg0].getGameType();
            case 3:
                return tables[arg0].getAdditionalInfo();
            case 4:
                return tables[arg0].getTableStateText();
            case 5:
                return timeFormatter.format(tables[arg0].getCreateTime());
            case 6:
                switch (tables[arg0].getTableState()) {

                    case WAITING:
                        String owner = tables[arg0].getControllerName();
                        if (session != null && owner.equals(session.getUserName())) {
                            return "";
                        }
                        return "Join";
                    case CONSTRUCTING:
                    case DRAFTING:
                        if (tables[arg0].isTournament()) {
                            return "Show";
                        }
                    case DUELING:
                        if (tables[arg0].isTournament()) {
                            return "Show";
                        } else {
                            owner = tables[arg0].getControllerName();
                            if (session != null && owner.equals(session.getUserName())) {
                                  return "";
                            }
                            return "Watch";
                        }                        
                    default:
                        return "";
                }
            case 7:
                return tables[arg0].isTournament();
            case 8:
                if (!tables[arg0].getGames().isEmpty()) {
                    return tables[arg0].getGames().get(0);
                }
                return null;
            case 9:
                return tables[arg0].getTableId();
        }
        return "";
    }

    @Override
    public String getColumnName(int columnIndex) {
        String colName = "";

        if (columnIndex <= getColumnCount()) {
            colName = columnNames[columnIndex];
        }

        return colName;
    }

    @Override
    public Class getColumnClass(int columnIndex){
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == ACTION_COLUMN;
    }

}

class UpdateTablesTask extends SwingWorker<Void, Collection<TableView>> {

    private final Session session;
    private final UUID roomId;
    private final TablesPanel panel;

    private static final Logger logger = Logger.getLogger(UpdateTablesTask.class);

    private int count = 0;

    UpdateTablesTask(Session session, UUID roomId, TablesPanel panel) {
        this.session = session;
        this.roomId = roomId;
        this.panel = panel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (!isCancelled()) {
            Collection<TableView> tables = session.getTables(roomId);
            if (tables != null) {
                this.publish(tables);
            }
            Thread.sleep(3000);
        }
        return null;
    }

    @Override
    protected void process(List<Collection<TableView>> view) {
        panel.updateTables(view.get(0));
        count++;
        if (count > 60) {
            count = 0;
            panel.reloadMessages();
        }
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            logger.fatal("Update Tables Task error", ex);
        } catch (CancellationException ex) {}
    }

}

class UpdatePlayersTask extends SwingWorker<Void, Collection<RoomUsersView>> {

    private final Session session;
    private final UUID roomId;
    private final ChatPanel chat;

    private static final Logger logger = Logger.getLogger(UpdatePlayersTask.class);

    UpdatePlayersTask(Session session, UUID roomId, ChatPanel chat) {
        this.session = session;
        this.roomId = roomId;
        this.chat = chat;
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (!isCancelled()) {
            this.publish(session.getRoomUsers(roomId));
            Thread.sleep(3000);
        }
        return null;
    }

    @Override
    protected void process(List<Collection<RoomUsersView>> roomUserInfo) {
        chat.setRoomUserInfo(roomUserInfo);
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            logger.fatal("Update Players Task error", ex);
        } catch (CancellationException ex) {}
    }

}

class MatchesTableModel extends AbstractTableModel {

    public static final int ACTION_COLUMN = 6; // column the action is located (starting with 0)
    public static final int GAMES_LIST_COLUMN = 7;
    private final String[] columnNames = new String[]{"Deck Type", "Players", "Game Type", "Result", "Start Time", "End Time","Action"};
    private MatchView[] matches = new MatchView[0];
    private static final DateFormat timeFormatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public void loadData(Collection<MatchView> matches) throws MageRemoteException {
        this.matches = matches.toArray(new MatchView[0]);
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return matches.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int arg0, int arg1) {
        switch (arg1) {
            case 0:
                return matches[arg0].getDeckType();
            case 1:
                return matches[arg0].getPlayers();
            case 2:
                return matches[arg0].getGameType();
            case 3:
                return matches[arg0].getResult();
            case 4:
                if (matches[arg0].getStartTime() != null) {
                    return timeFormatter.format(matches[arg0].getStartTime());
                } else {
                    return "";
                }
            case 5:
                if (matches[arg0].getEndTime() != null) {
                    return timeFormatter.format(matches[arg0].getEndTime());
                } else {
                    return "";
                }
            case 6:
                if (matches[arg0].isTournament()) {
                    return "Show";
                } else {
                    if (matches[arg0].isReplayAvailable()) {
                        return "Replay";
                    } else {
                        return "None";
                    }
                }               
            case 7:
                return matches[arg0].getGames();
        }
        return "";
    }
    
    public List<UUID> getListofGames (int row) {
         return matches[row].getGames();
    }
    
    public boolean isTournament(int row) {
         return matches[row].isTournament();
    }

    public UUID getMatchId(int row) {
         return matches[row].getMatchId();
    }
    
    public UUID getTableId(int row) {
         return matches[row].getTableId();
    }
    
    
    @Override
    public String getColumnName(int columnIndex) {
        String colName = "";

        if (columnIndex <= getColumnCount()) {
            colName = columnNames[columnIndex];
        }

        return colName;
    }

    @Override
    public Class getColumnClass(int columnIndex){
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == ACTION_COLUMN;
    }

}

class UpdateMatchesTask extends SwingWorker<Void, Collection<MatchView>> {

    private final Session session;
    private final UUID roomId;
    private final TablesPanel panel;

    private static final Logger logger = Logger.getLogger(UpdateTablesTask.class);

    UpdateMatchesTask(Session session, UUID roomId, TablesPanel panel) {
        this.session = session;
        this.roomId = roomId;
        this.panel = panel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (!isCancelled()) {
            Collection<MatchView> matches = session.getFinishedMatches(roomId);
            if (matches != null) {
                this.publish(matches);
            }
            Thread.sleep(10000);
        }
        return null;
    }

    @Override
    protected void process(List<Collection<MatchView>> view) {
        panel.updateMatches(view.get(0));
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            logger.fatal("Update Matches Task error", ex);
        } catch (CancellationException ex) {}
    }

}

class GameChooser extends JPopupMenu {

    private Session session;

    public void init(Session session) {
        this.session = session;
    }

    public void show(List<UUID> games, Point p) {
        if (p == null) {
            return;
        }
        this.removeAll();
        for (UUID gameId: games) {
            this.add(new GameChooserAction(gameId, gameId.toString()));
        }
        this.show(MageFrame.getDesktop(), p.x, p.y);
        GuiDisplayUtil.keepComponentInsideScreen(p.x, p.y, this);
    }

    private class GameChooserAction extends AbstractAction {

        private final UUID id;

        public GameChooserAction(UUID id, String choice) {
            this.id = id;
            putValue(Action.NAME, choice);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            session.replayGame(id);
            setVisible(false);
        }

    }

}
