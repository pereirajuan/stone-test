package org.mage.plugins.theme;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import mage.components.ImagePanel;
import mage.interfaces.plugin.ThemePlugin;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.apache.log4j.Logger;

@PluginImplementation
@Author(name = "nantuko")
public class ThemePluginImpl implements ThemePlugin {

	private final static Logger log = Logger.getLogger(ThemePluginImpl.class);

	@Init
	public void init() {
	}

	@PluginLoaded
	public void newPlugin(ThemePlugin plugin) {
		log.info(plugin.toString() + " has been loaded.");
	}

	public String toString() {
		return "[Theme plugin, version 0.4]";
	}

	public void applyInGame(Map<String, JComponent> ui) {
		String filename = "/wood.png";
		try {
			InputStream is = this.getClass().getResourceAsStream(filename);

			if (is == null) {
				throw new FileNotFoundException("Couldn't find " + filename + " in resources.");
			}

			BufferedImage background = ImageIO.read(is);

			if (background == null) {
				throw new FileNotFoundException("Couldn't find " + filename + " in resources.");
			}

			if (ui.containsKey("gamePanel") && ui.containsKey("jLayeredPane")) {
				ImagePanel bgPanel = new ImagePanel(background, ImagePanel.TILED);

				unsetOpaque(ui.get("jSplitPane1"));
				unsetOpaque(ui.get("pnlBattlefield"));
				unsetOpaque(ui.get("jPanel3"));
				unsetOpaque(ui.get("hand"));
				unsetOpaque(ui.get("chatPanel"));

				ui.get("gamePanel").remove(ui.get("jLayeredPane"));
				bgPanel.add(ui.get("jLayeredPane"));
				ui.get("gamePanel").add(bgPanel);
			} else {
				log.error("error: no components");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return;
		}
	}

	public JComponent updateTable(Map<String, JComponent> ui) {
		String filename = "/background.png";
		try {
			InputStream is = this.getClass().getResourceAsStream(filename);

			if (is == null)
				throw new FileNotFoundException("Couldn't find " + filename + " in resources.");

			BufferedImage background = ImageIO.read(is);

			if (background == null)
				throw new FileNotFoundException("Couldn't find " + filename + " in resources.");

			ImagePanel bgPanel = new ImagePanel(background, ImagePanel.SCALED);

			unsetOpaque(ui.get("jScrollPane1"));
			unsetOpaque(ui.get("jPanel1"));
			unsetOpaque(ui.get("tablesPanel"));
			JComponent viewport = ui.get("jScrollPane1ViewPort");
			if (viewport != null) {
				viewport.setBackground(new Color(255,255,255,50));
			}
			return bgPanel;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	private void unsetOpaque(JComponent c) {
		if (c != null) {
			c.setOpaque(false);
		}
	}
}
