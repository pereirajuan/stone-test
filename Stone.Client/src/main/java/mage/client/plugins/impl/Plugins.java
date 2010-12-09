package mage.client.plugins.impl;

import java.awt.Image;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import mage.cards.CardDimensions;
import mage.cards.MageCard;
import mage.cards.MagePermanent;
import mage.cards.action.impl.EmptyCallback;
import mage.client.cards.BigCard;
import mage.client.cards.Card;
import mage.client.cards.Permanent;
import mage.client.plugins.MagePlugins;
import mage.client.plugins.adapters.MageActionCallback;
import mage.client.util.Config;
import mage.client.util.DefaultActionCallback;
import mage.constants.Constants;
import mage.interfaces.PluginException;
import mage.interfaces.plugin.CardPlugin;
import mage.interfaces.plugin.CounterPlugin;
import mage.interfaces.plugin.ThemePlugin;
import mage.util.Logging;
import mage.view.CardView;
import mage.view.PermanentView;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;


public class Plugins implements MagePlugins {

	private final static MagePlugins fINSTANCE =  new Plugins();
	private final static Logger logger = Logging.getLogger(Plugins.class.getName());
	private static PluginManager pm;
	
	private ThemePlugin themePlugin = null;
	private CardPlugin cardPlugin = null;
	private CounterPlugin counterPlugin = null;
	protected static DefaultActionCallback defaultCallback = DefaultActionCallback.getInstance();
	private static final EmptyCallback emptyCallback = new EmptyCallback();
	private static final MageActionCallback mageActionCallback = new MageActionCallback();
	
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
		this.themePlugin = pm.getPlugin(ThemePlugin.class);
		logger.log(Level.INFO, "Done.");
	}
	
	@Override
	public void shutdown() {
		if (pm != null) pm.shutdown();
	}

	@Override
	public void updateGamePanel(Map<String, JComponent> ui) {
		if (themePlugin == null) return;
		themePlugin.applyInGame(ui);
	}

	@Override
	public JComponent updateTablePanel(Map<String, JComponent> ui) {
		if (themePlugin == null) return null;
		return themePlugin.updateTable(ui);
	}
	
	@Override
	public MagePermanent getMagePermanent(PermanentView card, BigCard bigCard, CardDimensions dimension, UUID gameId) {
		if (cardPlugin != null) {
			mageActionCallback.refreshSession();
			mageActionCallback.setCardPreviewComponent(bigCard);
			return cardPlugin.getMagePermanent(card, dimension, gameId, mageActionCallback);
		} else {
			return new Permanent(card, bigCard, Config.dimensions, gameId);
		}
	}
	
	@Override
	public MageCard getMageCard(CardView card, BigCard bigCard, CardDimensions dimension, UUID gameId) {
		if (cardPlugin != null) {
			mageActionCallback.refreshSession();
			mageActionCallback.setCardPreviewComponent(bigCard);
			return cardPlugin.getMageCard(card, dimension, gameId, mageActionCallback);
		} else {
			return new Card(card, bigCard, Config.dimensions, gameId);
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
	public void downloadImage(Set<mage.cards.Card> allCards) {
		if (this.cardPlugin != null) this.cardPlugin.downloadImages(allCards);
	}
	
	@Override
	public void downloadSymbols() {
		if (this.cardPlugin != null) this.cardPlugin.downloadSymbols();
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
	public void addGamesPlayed() {
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
	}

	@Override
	public boolean isCounterPluginLoaded() {
		return this.counterPlugin != null;
	}
	
	@Override
	public boolean isThemePluginLoaded() {
		return this.themePlugin != null;
	}
	
	@Override
	public Image getManaSymbolImage(String symbol) {
		if (this.cardPlugin != null) {
			return this.cardPlugin.getManaSymbolImage(symbol);
		}
		return null;
	}

	@Override
	public void onAddCard(MagePermanent card) {
		if (this.cardPlugin != null) {
			this.cardPlugin.onAddCard(card);
		}
	}

	@Override
	public void onRemoveCard(MagePermanent card) {
		if (this.cardPlugin != null) {
			this.cardPlugin.onRemoveCard(card);
		}
	}
}
