package com.tyoku.tasks;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.tyoku.BattleRoyale;
import com.tyoku.dto.BRGameStatus;
import com.tyoku.util.BRUtils;
import com.tyoku.util.CommonUtil;

public class Ending extends BukkitRunnable {
	private final BattleRoyale plugin;

	public Ending(BattleRoyale plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if(this.plugin.getBrManager().getGameStatus().equals(BRGameStatus.END)){
			Bukkit.shutdown();
			return;
		}
		this.plugin.getBrManager().setGameStatus(BRGameStatus.END);
		BRUtils.announce(plugin, "サーバを10秒後に再起動します。");
    	Player[] players = CommonUtil.getOnlinePlayers();
        for(int i = 0; i < players.length; i++){
            for(int j = 0; j < players.length; j++){
            	players[i].showPlayer(players[j]);
            }
        }
		for (int i = 10; i > 0; i--) {
			try {
				if(i <= 5 || i == 10 )
					BRUtils.announce(plugin, String.format("再起動まで%d秒", i));
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
		//Bukkit.shutdown();
		stopServer();
	}

	/**
	 * kickAllPlayer
	 */
	void clearServer() {
		Player[] players = CommonUtil.getOnlinePlayers();
		for (Player player : players) {
			player.kickPlayer("Good Bye!!!");
		}
	}
		// shut the server down!
	// hack into craftbukkit in order to do this
	// since bukkit doesn't normally allow access -_-
	// full kudos to the Redecouverte at:
	// http://forums.bukkit.org/threads/send-commands-to-console.3241/
	// for the code on executing commands as the console
	boolean stopServer() {
		// log it and empty out the server first
		this.plugin.getLogger().info("Restarting...");
		clearServer();
		try {
			File file = new File(this.plugin.getDataFolder().getAbsolutePath() + File.separator + "restart.txt");
			this.plugin.getLogger().info("Touching restart.txt at: " + file.getAbsolutePath());
			if (file.exists()) {
				file.setLastModified(System.currentTimeMillis());
			} else {
				file.createNewFile();
			}
		} catch (Exception e) {
			this.plugin.getLogger().info("Something went wrong while touching restart.txt!");
			return false;
		}
		try {
            this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "stop");
		} catch (Exception e) {
			this.plugin.getLogger().info("Something went wrong while saving & stoping!");
			return false;
		}
		return true;
	}
}