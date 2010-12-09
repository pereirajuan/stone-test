package mage.client.plugins.adapters;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

import mage.cards.MageCard;
import mage.cards.MagePermanent;
import mage.cards.action.ActionCallback;
import mage.cards.action.TransferData;
import mage.client.MageFrame;
import mage.client.cards.BigCard;
import mage.client.game.PlayAreaPanel;
import mage.client.plugins.impl.Plugins;
import mage.client.remote.Session;
import mage.client.util.DefaultActionCallback;
import mage.client.util.ImageHelper;
import mage.client.util.gui.ArrowBuilder;
import mage.client.util.gui.GuiDisplayUtil;
import mage.view.CardView;

import org.jdesktop.swingx.JXPanel;

public class MageActionCallback implements ActionCallback {

	private Popup popup;
	private BigCard bigCard; 
	protected static DefaultActionCallback defaultCallback = DefaultActionCallback.getInstance();
	protected static Session session = MageFrame.getSession();
	private CardView popupCard;
	private Thread t;
	private int state = 0;
	
	public MageActionCallback() {
	}
	
	public void setCardPreviewComponent(BigCard bigCard) {
		this.bigCard = bigCard;
	}
	
	public void refreshSession() {
		if (session == null) {
			session = MageFrame.getSession();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e, TransferData data) {
	}
	
	@Override
	public void mousePressed(MouseEvent e, TransferData data) {
		data.component.requestFocusInWindow();
		defaultCallback.mouseClicked(e, data.gameId, session, data.card);
	}

	@Override
	public void mouseEntered(MouseEvent e, final TransferData data) {
		this.popupCard = data.card;
		if (popup != null) {
			//DelayedViewerThread.getInstance().hide(data.popupText);
			popup.hide();
		}
		
		// Draw Arrows for targets
		List<UUID> targets = data.card.getTargets();
		if (targets != null) {
			Point parent = SwingUtilities.getRoot(data.component).getLocationOnScreen();
			Point me = new Point(data.locationOnScreen);
			me.translate(-parent.x, -parent.y);
			for (UUID uuid : targets) {
				//System.out.println("Getting play area panel for uuid: " + uuid);
				
				PlayAreaPanel p = session.getGame().getPlayers().get(uuid);
				if (p != null) {
					Point target = p.getLocationOnScreen();
					target.translate(-parent.x, -parent.y);
					ArrowBuilder.addArrow((int)me.getX() + 35, (int)me.getY(), (int)target.getX() + 40, (int)target.getY() - 40, Color.red);
				} else {
					for (PlayAreaPanel pa : session.getGame().getPlayers().values()) {
						MagePermanent permanent = pa.getBattlefieldPanel().getPermanents().get(uuid);
						if (permanent != null) {
							Point target = permanent.getLocationOnScreen();
							target.translate(-parent.x, -parent.y);
							ArrowBuilder.addArrow((int)me.getX() + 35, (int)me.getY(), (int)target.getX() + 40, (int)target.getY() + 10, Color.red);
						}
					}
				}
			}
		}
		
		// Draw Arrows for source
		if (data.card.isAbility()) {
			Point parent = SwingUtilities.getRoot(data.component).getLocationOnScreen();
			Point me = new Point(data.locationOnScreen);
			me.translate(-parent.x, -parent.y);
			UUID uuid = data.card.getParentId();
			for (PlayAreaPanel pa : session.getGame().getPlayers().values()) {
				MagePermanent permanent = pa.getBattlefieldPanel().getPermanents().get(uuid);
				if (permanent != null) {
					Point source = permanent.getLocationOnScreen();
					source.translate(-parent.x, -parent.y);
					ArrowBuilder.addArrow((int)source.getX() + 40, (int)source.getY() + 10, (int)me.getX() + 35, (int)me.getY() + 20, Color.blue);
				}
			}
		}
		
		showPopup(data);
	}

	private void showPopup(final TransferData data) {
		/*try {
			((JDesktopPane)session.getUI().getComponent(MageComponents.DESKTOP_PANE)).add(data.popupText, JLayeredPane.POPUP_LAYER);
			data.popupText.setBounds((int) data.locationOnScreen.getX() + data.popupOffsetX, (int) data.locationOnScreen.getY() + data.popupOffsetY + 40, 200, 200);
			data.popupText.setText("Test");
			DelayedViewerThread.getInstance().show((Component)data.popupText, 500);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}*/
		
		PopupFactory factory = PopupFactory.getSharedInstance();
		popup = factory.getPopup(data.component, data.popupText, (int) data.locationOnScreen.getX() + data.popupOffsetX, (int) data.locationOnScreen.getY() + data.popupOffsetY + 40);
		popup.show();
		// hack to get popup to resize to fit text
		popup.hide();
		popup = factory.getPopup(data.component, data.popupText, (int) data.locationOnScreen.getX() + data.popupOffsetX, (int) data.locationOnScreen.getY() + data.popupOffsetY + 40);
		popup.show();
	}
	
	@Override
	public void mouseMoved(MouseEvent e, TransferData data) {
		if (!Plugins.getInstance().isCardPluginLoaded()) {return;}
		if (bigCard == null) {return;}

		MageCard card = (MageCard) data.component;
		if (card.getOriginal().getId() != bigCard.getCardId()) {
			synchronized (MageActionCallback.class) {
				if (card.getOriginal().getId() != bigCard.getCardId()) {
					Image image = card.getImage();
					if (image != null && image instanceof BufferedImage) {
						image = ImageHelper.getResizedImage((BufferedImage) image, bigCard.getWidth(), bigCard.getHeight());
						bigCard.setCard(card.getOriginal().getId(), image, card.getOriginal().getRules());
						bigCard.showTextComponent();
						if (card.getOriginal().isAbility()) {
							bigCard.showTextComponent();
						} else {
							bigCard.hideTextComponent();
						};  
					} else {
						JXPanel panel = GuiDisplayUtil.getDescription(card.getOriginal(), bigCard.getWidth(), bigCard.getHeight());
						panel.setVisible(true);
						bigCard.hideTextComponent();
						bigCard.addJXPanel(card.getOriginal().getId(), panel);
					}
				}
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e, final TransferData data) {
		this.popupCard = null;
		//DelayedViewerThread.getInstance().hide(data.popupText);
		if (popup != null) {
			popup.hide();
		}
		ArrowBuilder.removeAllArrows();
	}

}
