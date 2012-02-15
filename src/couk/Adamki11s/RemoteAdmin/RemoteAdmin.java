package couk.Adamki11s.RemoteAdmin;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import couk.Adamki11s.Sockets.RAServer;

public class RemoteAdmin extends JavaPlugin{
	
	private static final Logger log = Logger.getLogger("RemoteAdmin");
	public static final String prefix = "[RemoteAdmin]";

	@Override
	public void onDisable() {
		logInfo("RemoteAdmin un-loaded successfully.");
	}

	@Override
	public void onEnable() {
		logInfo("RemoteAdmin loaded successfully.");
		RAServer server = new RAServer(1234, "password");
		server.setActive(true);
		server.start();
	}
	
	public static void logInfo(String message){
		log.info(prefix + " " + message);
	}

}
