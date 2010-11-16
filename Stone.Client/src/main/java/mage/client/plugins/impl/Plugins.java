package mage.client.plugins.impl;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import mage.cards.Card;
import mage.cards.CardDimensions;
import mage.cards.MagePermanent;
import mage.cards.action.impl.EmptyCallback;
import mage.client.cards.BigCard;
import mage.client.cards.Permanent;
import mage.client.plugins.MagePlugins;
import mage.client.util.Config;
import mage.client.util.DefaultActionCallback;
import mage.constants.Constants;
import mage.interfaces.PluginException;
import mage.interfaces.plugin.CardPlugin;
import mage.interfaces.plugin.CounterPlugin;
import mage.interfaces.plugin.ThemePlugin;
import mage.util.Logging;
import mage.view.PermanentView;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;


public class Plugins implements MagePlugins {

	private static final MagePlugins fINSTANCE =  new Plugins();
	private static PluginManager pm;
	private final static Logger logger = Logging.getLogger(Plugins.class.getName());
	private CardPlugin cardPlugin = null;
	private CounterPlugin counterPlugin = null;
	protected static DefaultActionCallback defaultCallback = DefaultActionCallback.getInstance();
	private static final EmptyCallback emptyCallback = new EmptyCallback();
	
	public static MagePlugins getInstance() {
		return fINSTANCE;
	}
	
	@Override
	public void loadPlugins() {
		logger.log(Level.INFO, "Loading plugins...");
		pm = PluginManagerFactory.createPluginManager();
		pm.addPluginsFrom(new File(Constants.PLUGINS_DIRECTORY).toURI());
		this.cardPlugin = pm.getPlugin(CardPlugin.class);
		this.counterPlugin = pm.getPlugin(CounterPlugin.class);
		logger.log(Level.INFO, "Done.");
	}
	
	@Override
	public void shutdown() {
		if (pm != null) pm.shutdown();
	}

	@Override
	public void updateGamePanel(Map<String, JComponent> ui) {
		PluginManagerUtil pmu = new PluginManagerUtil(pm);
		
		for (ThemePlugin pl : pmu.getPlugins(ThemePlugin.class)) {
			pl.applyInGame(ui);
		}
	}

	@Override
	public void updateOnTable(Map<String, JComponent> ui) {
		PluginManagerUtil pmu = new PluginManagerUtil(pm);
		
		for (ThemePlugin pl : pmu.getPlugins(ThemePlugin.class)) {
			pl.applyOnTable(ui);
		}
	}
	
	@Override
	public MagePermanent getMagePermanent(final PermanentView card, BigCard bigCard, CardDimensions dimension, final UUID gameId) {
		if (cardPlugin != null) {
			return cardPlugin.getMagePermanent(card, dimension, gameId, emptyCallback);
		} else {
			return new Permanent(card, bigCard, Config.dimensions, gameId);
		}
	}
	
	@Override
	public boolean isCardPluginLoaded() {
		return this.cardPlugin != null;
	}

	@Override
	public void sortPermanents(Map<String, JComponent> ui, Collection<MagePermanent> permanents) {
		if (this.cardPlugin != null) this.cardPlugin.sortPermanents(ui, permanents);
	}

	@Override
	public void downloadImage(Set<Card> allCards) {
		if (this.cardPlugin != null) this.cardPlugin.downloadImages(allCards);
	}

	@Override
	public int getGamesPlayed() {
		if (this.counterPlugin != null) {
			synchronized(Plugins.class) {
				try {
					return this.counterPlugin.getGamePlayed();
				} catch (PluginException e) {
					logger.log(Level.SEVERE, e.getMessage());
					throw new RuntimeException(e);
				}
			}
		}
		return -1;
	}

	@Override
	public int addGamesPlayed() {
		if (this.counterPlugin != null) {
			synchronized(Plugins.class) {
				try {
					this.counterPlugin.addGamePlayed();
				} catch (PluginException e) {
					logger.log(Level.SEVERE, e.getMessage());
					throw new RuntimeException(e);
				}
			}
		}
		return 0;
	}

	@Override
	public boolean isCounterPluginLoaded() {
		return this.counterPlugin != null;
	}
}
