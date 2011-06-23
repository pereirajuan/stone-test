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

package mage.server;

import mage.server.util.PluginClassLoader;
import java.io.File;
import java.io.FilenameFilter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import javax.management.MBeanServer;
import mage.game.match.MatchType;
import mage.game.tournament.TournamentType;
import mage.interfaces.MageServer;
import mage.server.game.DeckValidatorFactory;
import mage.server.game.GameFactory;
import mage.server.game.PlayerFactory;
import mage.server.tournament.TournamentFactory;
import mage.server.util.ConfigSettings;
import mage.server.util.config.Plugin;
import mage.server.util.config.GamePlugin;
import mage.utils.MageVersion;
import org.apache.log4j.Logger;
import org.jboss.remoting.InvocationRequest;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.ServerInvocationHandler;
import org.jboss.remoting.ServerInvoker;
import org.jboss.remoting.callback.InvokerCallbackHandler;
import org.jboss.remoting.callback.ServerInvokerCallbackHandler;
import org.jboss.remoting.transport.Connector;
import org.jboss.remoting.transporter.TransporterServer;
import org.w3c.dom.Element;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class Main {

	private static Logger logger = Logger.getLogger(Main.class);

	private final static String testModeArg = "-testMode=";
	private final static String adminPasswordArg = "-adminPassword=";
	private final static String pluginFolder = "plugins";
	private static MageVersion version = new MageVersion(0, 8, 0, "");

	public static PluginClassLoader classLoader = new PluginClassLoader();
	public static TransporterServer server;
	protected static boolean testMode;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

		logger.info("Starting MAGE server version " + version);
		logger.info("Logging level: " + logger.getEffectiveLevel());
		deleteSavedGames();
		ConfigSettings config = ConfigSettings.getInstance();
		for (GamePlugin plugin: config.getGameTypes()) {
			GameFactory.getInstance().addGameType(plugin.getName(), loadGameType(plugin), loadPlugin(plugin));
		}
		for (GamePlugin plugin: config.getTournamentTypes()) {
			TournamentFactory.getInstance().addTournamentType(plugin.getName(), loadTournamentType(plugin), loadPlugin(plugin));
		}
		for (Plugin plugin: config.getPlayerTypes()) {
			PlayerFactory.getInstance().addPlayerType(plugin.getName(), loadPlugin(plugin));
		}
		for (Plugin plugin: config.getDeckTypes()) {
			DeckValidatorFactory.getInstance().addDeckType(plugin.getName(), loadPlugin(plugin));
		}
		String adminPassword = "";
		for (String arg: args) {
			if (arg.startsWith(testModeArg)) {
				testMode = Boolean.valueOf(arg.replace(testModeArg, ""));
			}
			else if (arg.startsWith(adminPasswordArg)) {
				adminPassword = arg.replace(adminPasswordArg, "");
			}
		}
		String host = getServerAddress();
		int port = config.getPort();
		String locatorURI = "bisocket://" + host + ":" + port;
		try {
			InvokerLocator serverLocator = new InvokerLocator(locatorURI);
			server = new MageTransporterServer(serverLocator, new MageServerImpl(adminPassword, testMode), MageServer.class.getName(), new MageServerInvocationHandler());
			server.start();
			logger.info("Started MAGE server - listening on " + host + ":" + port);
			if (testMode)
				logger.info("MAGE server running in test mode");

		} catch (Exception ex) {
			logger.fatal("Failed to start server - " + host + ":" + port, ex);
		}

    }

	static class MageTransporterServer extends TransporterServer {
		
		Connector connector;
		
		public MageTransporterServer(InvokerLocator locator, Object target, String subsystem, MageServerInvocationHandler callback) throws Exception {
			super(locator, target, subsystem);
			connector.addInvocationHandler("callback", callback);
		}
		
		public Connector getConnector() throws Exception {
			return connector;
		}
		
		@Override
		protected Connector getConnector(InvokerLocator locator, Map config, Element xmlConfig) throws Exception {
			Connector c = super.getConnector(locator, config, xmlConfig);
			this.connector = c;
			return c;
		}
	}
	
	static class MageServerInvocationHandler implements ServerInvocationHandler {

		@Override
		public void setMBeanServer(MBeanServer server) {}

		@Override
		public void setInvoker(ServerInvoker invoker) {}

		@Override
		public Object invoke(final InvocationRequest invocation) throws Throwable {
			return null;
		}

		@Override
		public void addListener(InvokerCallbackHandler callbackHandler) {
			ServerInvokerCallbackHandler handler = (ServerInvokerCallbackHandler) callbackHandler;
			try {
				String sessionId = handler.getClientSessionId();
				InetAddress clientAddress = handler.getCallbackClient().getAddressSeenByServer();
				SessionManager.getInstance().createSession(sessionId, callbackHandler, clientAddress.getHostAddress());
			} catch (Throwable ex) {
				logger.fatal("", ex);
			}
		}

		@Override
		public void removeListener(InvokerCallbackHandler callbackHandler) {
			ServerInvokerCallbackHandler handler = (ServerInvokerCallbackHandler) callbackHandler;
			String sessionId = handler.getCallbackClient().getSessionId();
			SessionManager.getInstance().removeSession(sessionId);
		}
		
	}

	private static String getServerAddress() {
		try {
			String foundIP = "";
			for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
				NetworkInterface iface = interfaces.nextElement( );
				if (iface.isLoopback())
					continue;
				for (InterfaceAddress addr: iface.getInterfaceAddresses())
				{
					InetAddress iaddr = addr.getAddress();
					if (iaddr instanceof Inet4Address) {
						foundIP = iaddr.getHostAddress();
						break;
					}
				}
				if (foundIP.length() > 0)
					break;
			}
			if (foundIP.length() > 0)
				return foundIP;
		} catch (SocketException ex) {
			logger.warn("Could not get server address: ", ex);
		}
		return null;
	}
	
	private static Class<?> loadPlugin(Plugin plugin) {
		try {
			classLoader.addURL(new File(pluginFolder + "/" + plugin.getJar()).toURI().toURL());
			logger.info("Loading plugin: " + plugin.getClassName());
			return Class.forName(plugin.getClassName(), true, classLoader);
		} catch (ClassNotFoundException ex) {
			logger.warn("Plugin not Found:" + plugin.getJar() + " - check plugin folder");
		} catch (Exception ex) {
			logger.fatal("Error loading plugin " + plugin.getJar(), ex);
		}
		return null;
	}

	private static MatchType loadGameType(GamePlugin plugin) {
		try {
			classLoader.addURL(new File(pluginFolder + "/" + plugin.getJar()).toURI().toURL());
			logger.info("Loading game type: " + plugin.getClassName());
			return (MatchType) Class.forName(plugin.getTypeName(), true, classLoader).newInstance();
		} catch (ClassNotFoundException ex) {
			logger.warn("Game type not found:" + plugin.getJar() + " - check plugin folder");
		} catch (Exception ex) {
			logger.fatal("Error loading game type " + plugin.getJar(), ex);
		}
		return null;
	}

	private static TournamentType loadTournamentType(GamePlugin plugin) {
		try {
			classLoader.addURL(new File(pluginFolder + "/" + plugin.getJar()).toURI().toURL());
			logger.info("Loading tournament type: " + plugin.getClassName());
			return (TournamentType) Class.forName(plugin.getTypeName(), true, classLoader).newInstance();
		} catch (ClassNotFoundException ex) {
			logger.warn("Tournament type not found:" + plugin.getJar() + " - check plugin folder");
		} catch (Exception ex) {
			logger.fatal("Error loading game type " + plugin.getJar(), ex);
		}
		return null;
	}

	private static void deleteSavedGames() {
		File directory = new File("saved/");
		if (!directory.exists())
			directory.mkdirs();
		File[] files = directory.listFiles(
			new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".game");
				}
			}
		);
		for (File file : files)
		{
			file.delete();
		}
	}

	public static MageVersion getVersion() {
		return version;
	}

	public static boolean isTestMode() {
		return testMode;
	}
}
