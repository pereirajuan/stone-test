package mage.cards.action;

import java.awt.event.MouseEvent;

public interface ActionCallback {
    void mouseClicked(MouseEvent e, TransferData data);
    void mousePressed(MouseEvent e, TransferData data);
    void mouseMoved(MouseEvent e, TransferData data);
    void mouseEntered(MouseEvent e, TransferData data);
    void mouseExited(MouseEvent e, TransferData dat);
    void hidePopup();
}
