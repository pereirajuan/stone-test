/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mage.client.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import mage.client.MageFrame;
import mage.client.dialog.PreferencesDialog;

/**
 *
 * @author LevelX2
 */
public class GUISizeHelper {

    // relate the native image card size to a value of the size scale
    final static int CARD_IMAGE_WIDTH = 312;
    final static int CARD_IMAGE_HEIGHT = 445;
    final static int CARD_IMAG_VALUE = 42;

    public static String basicSymbolSize = "small";

    public static int symbolCardSize = 15;
    public static int symbolTableSize = 15;
    public static int symbolChatSize = 15;
    public static int symbolDialogSize = 15;
    public static int symbolTooltipSize = 15;
    public static int symbolPaySize = 15;
    public static int symbolEditorSize = 15;

    public static int tableHeaderHeight = 24;
    public static int tableRowHeight = 20;

    public static int dividerBarSize;
    public static int scrollBarSize;

    public static int flagHeight;

    public static int cardTooltipFontSize = 15;

    public static Font chatFont = new java.awt.Font("Arial", 0, 12);
    public static Font tableFont = new java.awt.Font("Arial", 0, 12);
    public static Font balloonTooltipFont = new java.awt.Font("Arial", 0, 12);
    public static Font menuFont = new java.awt.Font("Arial", 0, 12);

    public static Font gameRequestsFont = new java.awt.Font("Arial", 0, 12);

    public static Font gameDialogAreaFontBig = new java.awt.Font("Arial", 0, 12);
    public static Font gameDialogAreaFontSmall = new java.awt.Font("Arial", 0, 12);

    public static Dimension handCardDimension;
    public static int stackWidth;

    public static Dimension otherZonesCardDimension;
    public static Dimension battlefieldCardDimension;

    public static Dimension editorCardDimension;
    public static int editorCardOffsetSize;

    public static int getTableRowHeight() {
        int fontSize = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_TABLE_FONT_SIZE, 14);
        return fontSize + 6;
    }

    public static Font getTabFont() {
        int fontSize = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_TABLE_FONT_SIZE, 14);
        return new java.awt.Font("Arial", 0, fontSize);
    }

    public static void changeGUISize() {
        calculateGUISizes();
        MageFrame.getInstance().changeGUISize();
    }

    public static void calculateGUISizes() {
        int tableFontSize = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_TABLE_FONT_SIZE, 14);
        tableFont = new java.awt.Font("Arial", 0, tableFontSize);
        tableRowHeight = tableFontSize + 4;
        tableHeaderHeight = tableFontSize + 10;
        symbolTableSize = tableFontSize;
        flagHeight = tableFontSize - 2;
        balloonTooltipFont = new Font("Arial", 0, tableFontSize);
        if (tableFontSize > 15) {
            symbolEditorSize = tableFontSize - 5;
            dividerBarSize = 10 + (tableFontSize / 4);
            scrollBarSize = 14 + (tableFontSize / 4);
        } else {
            symbolEditorSize = tableFontSize;
            dividerBarSize = 10;
            scrollBarSize = 14;
        }

        // used for popup menus
        int dialogFontSize = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_DIALOG_FONT_SIZE, 14);
        menuFont = new Font("Arial", 0, dialogFontSize);
        gameRequestsFont = new Font("Arial", 0, dialogFontSize);
        symbolDialogSize = dialogFontSize;

        int chatFontSize = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_CHAT_FONT_SIZE, 14);
        chatFont = new java.awt.Font("Arial", 0, chatFontSize);
        symbolChatSize = chatFontSize;

        cardTooltipFontSize = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_TOOLTIP_SIZE, 14);
        symbolTooltipSize = cardTooltipFontSize;

        int handCardSize = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_CARD_HAND_SIZE, 14);
        handCardDimension = new Dimension(CARD_IMAGE_WIDTH * handCardSize / 42, CARD_IMAGE_HEIGHT * handCardSize / 42);
        stackWidth = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_STACK_WIDTH, 30);

        int otherZonesCardSize = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_CARD_OTHER_ZONES_SIZE, 14);
        otherZonesCardDimension = new Dimension(CARD_IMAGE_WIDTH * otherZonesCardSize / 42, CARD_IMAGE_HEIGHT * otherZonesCardSize / 42);

        int battlefieldCardSize = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_CARD_BATTLEFIELD_SIZE, 14);
        battlefieldCardDimension = new Dimension(CARD_IMAGE_WIDTH * battlefieldCardSize / 42, CARD_IMAGE_HEIGHT * battlefieldCardSize / 42);

        int editorCardSize = PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_CARD_EDITOR_SIZE, 14);
        editorCardDimension = new Dimension(CARD_IMAGE_WIDTH * editorCardSize / 42, CARD_IMAGE_HEIGHT * editorCardSize / 42);
        editorCardOffsetSize = 2 * PreferencesDialog.getCachedValue(PreferencesDialog.KEY_GUI_CARD_OFFSET_SIZE, 14) - 10;
    }

    public static void changePopupMenuFont(JPopupMenu popupMenu) {
        for (Component comp : popupMenu.getComponents()) {
            if (comp instanceof JMenuItem) {
                comp.setFont(GUISizeHelper.menuFont);
                if (comp instanceof JMenu) {
                    comp.setFont(GUISizeHelper.menuFont);
                    for (Component subComp : ((JMenu) comp).getMenuComponents()) {
                        subComp.setFont(GUISizeHelper.menuFont);
                    }
                }
            }
        }
    }
}
