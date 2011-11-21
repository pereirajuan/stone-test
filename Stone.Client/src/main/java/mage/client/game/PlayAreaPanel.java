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
 * PlayAreaPanel.java
 *
 * Created on Dec 22, 2009, 10:41:54 AM
 */

package mage.client.game;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import mage.client.MageFrame;
import mage.client.cards.BigCard;
import mage.sets.Sets;
import mage.view.PlayerView;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class PlayAreaPanel extends javax.swing.JPanel {

	UUID playerId;
	UUID gameId;

    /** Creates new form PlayAreaPanel */
    public PlayAreaPanel() {
        initComponents();
		setOpaque(false);
		jPanel1.setOpaque(false);
        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);
        battlefieldPanel.setOpaque(false);
	}

	public PlayAreaPanel(PlayerView player, BigCard bigCard, UUID gameId, boolean me) {
		this();
		init(player, bigCard, gameId);
		update(player);
	}

	public void init(PlayerView player, BigCard bigCard, UUID gameId) {
		this.playerPanel.init(gameId, player.getPlayerId(), bigCard);
		this.battlefieldPanel.init(gameId, bigCard);
		if (MageFrame.getSession().isTestMode()) {
			this.playerId = player.getPlayerId();
			this.gameId = gameId;
			this.btnCheat.setVisible(true);
		}
		else {
			this.btnCheat.setVisible(false);
		}
	}

	public void update(PlayerView player) {
		this.playerPanel.update(player);
		this.battlefieldPanel.update(player.getBattlefield());
	}

	public mage.client.game.BattlefieldPanel getBattlefieldPanel() {
		return battlefieldPanel;
	}

    @SuppressWarnings("unchecked")
    private void initComponents() {
		setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0)));
        jPanel1 = new javax.swing.JPanel();
        playerPanel = new PlayerPanelExt();
		playerPanel.setPreferredSize(new Dimension(92, 212));
        btnCheat = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        battlefieldPanel = new mage.client.game.BattlefieldPanel(jScrollPane1);

        btnCheat.setText("Cheat");
        btnCheat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout gl_jPanel1 = new javax.swing.GroupLayout(jPanel1);
        gl_jPanel1.setHorizontalGroup(
        	gl_jPanel1.createParallelGroup(Alignment.LEADING)
        		.addComponent(playerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        gl_jPanel1.setVerticalGroup(
        	gl_jPanel1.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_jPanel1.createSequentialGroup()
        			.addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, 212, Short.MAX_VALUE)
        			.addContainerGap())
        );
        jPanel1.setLayout(gl_jPanel1);

        jScrollPane1.setViewportView(battlefieldPanel);
		Border empty = new EmptyBorder(0,0,0,0);
		jScrollPane1.setBorder(empty);
		jScrollPane1.setViewportBorder(empty);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
        			.addGap(0))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(Alignment.LEADING, layout.createSequentialGroup()
        			.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(jScrollPane1)
        				.addComponent(jPanel1, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE))
        			.addGap(0))
        );
        this.setLayout(layout);
    }// </editor-fold>//GEN-END:initComponents

    public void sizePlayer() {
    	this.playerPanel.sizePlayerPanel();
    }
    
	private void btnCheatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheatActionPerformed
		try {
			MageFrame.getSession().cheat(gameId, playerId, Sets.loadDeck("cheat.dck"));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(PlayAreaPanel.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(PlayAreaPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}//GEN-LAST:event_btnCheatActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private mage.client.game.BattlefieldPanel battlefieldPanel;
	private javax.swing.JButton btnCheat;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    //private mage.client.game.ManaPool manaPool;
    private PlayerPanelExt playerPanel;
    // End of variables declaration//GEN-END:variables

}
