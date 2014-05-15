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

/*
 * DraftPanel.java
 *
 * Created on Jan 7, 2011, 2:15:48 PM
 */

package mage.client.draft;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import mage.client.MageFrame;
import mage.client.deckeditor.SortSettingDraft;
import mage.client.plugins.impl.Plugins;
import mage.client.util.CardsViewUtil;
import mage.client.util.Event;
import mage.client.util.Listener;
import mage.remote.Session;
import mage.view.CardsView;
import mage.view.DraftPickView;
import mage.view.DraftView;
import mage.view.SimpleCardView;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class DraftPanel extends javax.swing.JPanel {

    private UUID draftId;
    private Session session;
    private Timer countdown;
    private int timeout;
    private boolean picked;

    private static final CardsView emptyView = new CardsView();

    /** Creates new form DraftPanel */
    public DraftPanel() {
        initComponents();

        draftBooster.setOpaque(false);
        draftPicks.setSortSetting(SortSettingDraft.getInstance());
        draftPicks.setOpaque(false);
        draftLeftPane.setOpaque(false);

        countdown = new Timer(1000,
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (--timeout > 0) {
                        setTimeout(timeout);
                    }
                    else {
                        setTimeout(0);
                        countdown.stop();
                    }
                }
            }
        );
    }

    public void cleanUp() {
        draftPicks.cleanUp();
        draftBooster.clear();

        if (countdown != null) {
            countdown.stop();
            for (ActionListener al : countdown.getActionListeners()) {
                countdown.removeActionListener(al);
            }
        }
    }

    public synchronized void showDraft(UUID draftId) {
        this.draftId = draftId;
        session = MageFrame.getSession();
        MageFrame.addDraft(draftId, this);
        if (!session.joinDraft(draftId)) {
            hideDraft();
        }
    }

    public void updateDraft(DraftView draftView) {        
        this.txtPack1.setText(draftView.getSets().get(0));
        this.txtPack2.setText(draftView.getSets().get(1));
        this.txtPack3.setText(draftView.getSets().get(2));
        this.chkPack1.setSelected(draftView.getBoosterNum() > 0);
        this.chkPack2.setSelected(draftView.getBoosterNum() > 1);
        this.chkPack3.setSelected(draftView.getBoosterNum() > 2);
        this.txtCardNo.setText(Integer.toString(draftView.getCardNum()));
    }

    public void loadBooster(DraftPickView draftPickView) {
        draftBooster.loadBooster(CardsViewUtil.convertSimple(draftPickView.getBooster()), bigCard);
        draftPicks.loadCards(CardsViewUtil.convertSimple(draftPickView.getPicks()), bigCard, null);
        this.draftBooster.clearCardEventListeners();
        this.draftBooster.addCardEventListener(
            new Listener<Event> () {
                @Override
                public void event(Event event) {
                    if (event.getEventName().equals("pick-a-card")) {
                        SimpleCardView source = (SimpleCardView) event.getSource();
                        DraftPickView view = session.sendCardPick(draftId, source.getId());
                        if (view != null) {
                            draftBooster.loadBooster(emptyView, bigCard);
                            draftPicks.loadCards(CardsViewUtil.convertSimple(view.getPicks()), bigCard, null);
                            Plugins.getInstance().getActionCallback().hidePopup();
                            setMessage("Waiting for other players");
                        }
                    }
                }
            }
        );
        setMessage("Pick a card");
        countdown.stop();
        this.timeout = draftPickView.getTimeout();
        setTimeout(timeout);
        if (timeout != 0) {
            countdown.start();
        }
    }
    
    private void setTimeout(int s){
        int minute = s/60;
        int second = s - (minute*60);
        String text;
        if(minute < 10){
            text = "0" + Integer.toString(minute) + ":";
        }else{
            text = Integer.toString(minute) + ":";
        }
        if(second < 10){
            text = text + "0" + Integer.toString(second);
        }else{
            text = text + Integer.toString(second);
        }
        this.txtTimeRemaining.setText(text);
    }

    public void hideDraft() {
        Component c = this.getParent();
        while (c != null && !(c instanceof DraftPane)) {
            c = c.getParent();
        }
        if (c != null) {
            ((DraftPane)c).removeDraft();
        }
    }

    protected void setMessage(String message) {
        this.lblMessage.setText(message);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        draftLeftPane = new javax.swing.JPanel();
        btnQuitTournament = new javax.swing.JButton();
        lblPack1 = new javax.swing.JLabel();
        txtPack1 = new javax.swing.JTextField();
        chkPack1 = new javax.swing.JCheckBox();
        lblPack2 = new javax.swing.JLabel();
        txtPack2 = new javax.swing.JTextField();
        chkPack2 = new javax.swing.JCheckBox();
        lblPack3 = new javax.swing.JLabel();
        txtPack3 = new javax.swing.JTextField();
        chkPack3 = new javax.swing.JCheckBox();
        lblCardNo = new javax.swing.JLabel();
        txtCardNo = new javax.swing.JTextField();
        txtTimeRemaining = new javax.swing.JTextField();
        lblMessage = new javax.swing.JLabel();
        bigCard = new mage.client.cards.BigCard();
        draftPicks = new mage.client.cards.CardsList();
        draftBooster = new mage.client.cards.DraftGrid();

        draftLeftPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnQuitTournament.setText("Quit Tournament");
        btnQuitTournament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitTournamentActionPerformed(evt);
            }
        });

        lblPack1.setText("Pack 1:");

        txtPack1.setEditable(false);
        txtPack1.setEnabled(false);
        txtPack1.setPreferredSize(new java.awt.Dimension(130, 22));

        lblPack2.setText("Pack 2:");

        txtPack2.setEditable(false);
        txtPack2.setEnabled(false);
        txtPack2.setPreferredSize(new java.awt.Dimension(130, 22));

        lblPack3.setText("Pack 3:");

        txtPack3.setEditable(false);
        txtPack3.setEnabled(false);
        txtPack3.setPreferredSize(new java.awt.Dimension(130, 22));

        lblCardNo.setText("Card #:");

        txtCardNo.setEditable(false);
        txtCardNo.setEnabled(false);

        txtTimeRemaining.setEditable(false);
        txtTimeRemaining.setForeground(java.awt.Color.red);
        txtTimeRemaining.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTimeRemaining.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout draftLeftPaneLayout = new javax.swing.GroupLayout(draftLeftPane);
        draftLeftPane.setLayout(draftLeftPaneLayout);
        draftLeftPaneLayout.setHorizontalGroup(
            draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(draftLeftPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCardNo)
                    .addGroup(draftLeftPaneLayout.createSequentialGroup()
                        .addGroup(draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, draftLeftPaneLayout.createSequentialGroup()
                                .addComponent(lblPack2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPack2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, draftLeftPaneLayout.createSequentialGroup()
                                .addComponent(lblPack1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPack1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, draftLeftPaneLayout.createSequentialGroup()
                                .addComponent(lblPack3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCardNo)
                                    .addComponent(txtPack3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtTimeRemaining))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkPack3)
                            .addComponent(chkPack2)
                            .addComponent(chkPack1)))
                    .addGroup(draftLeftPaneLayout.createSequentialGroup()
                        .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                        .addContainerGap())))
            .addGroup(draftLeftPaneLayout.createSequentialGroup()
                .addGroup(draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bigCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(draftLeftPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnQuitTournament)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        draftLeftPaneLayout.setVerticalGroup(
            draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, draftLeftPaneLayout.createSequentialGroup()
                .addComponent(btnQuitTournament)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPack1)
                    .addComponent(txtPack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPack1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPack2)
                    .addComponent(txtPack2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPack2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPack3)
                    .addComponent(txtPack3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPack3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(draftLeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCardNo)
                    .addComponent(txtCardNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTimeRemaining, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bigCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        draftBooster.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout draftBoosterLayout = new javax.swing.GroupLayout(draftBooster);
        draftBooster.setLayout(draftBoosterLayout);
        draftBoosterLayout.setHorizontalGroup(
            draftBoosterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
        );
        draftBoosterLayout.setVerticalGroup(
            draftBoosterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 452, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(draftLeftPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(draftPicks, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
                    .addComponent(draftBooster, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(draftLeftPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(draftPicks, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(draftBooster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnQuitTournamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitTournamentActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to quit the tournament?", "Confirm quit tournament", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            MageFrame.getSession().quitDraft(draftId);
            MageFrame.removeDraft(draftId);
        }
    }//GEN-LAST:event_btnQuitTournamentActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private mage.client.cards.BigCard bigCard;
    private javax.swing.JButton btnQuitTournament;
    private javax.swing.JCheckBox chkPack1;
    private javax.swing.JCheckBox chkPack2;
    private javax.swing.JCheckBox chkPack3;
    private mage.client.cards.DraftGrid draftBooster;
    private javax.swing.JPanel draftLeftPane;
    private mage.client.cards.CardsList draftPicks;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCardNo;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblPack1;
    private javax.swing.JLabel lblPack2;
    private javax.swing.JLabel lblPack3;
    private javax.swing.JTextField txtCardNo;
    private javax.swing.JTextField txtPack1;
    private javax.swing.JTextField txtPack2;
    private javax.swing.JTextField txtPack3;
    private javax.swing.JTextField txtTimeRemaining;
    // End of variables declaration//GEN-END:variables

}
