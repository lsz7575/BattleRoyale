package com.tyoku.commands;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.tyoku.BattleRoyale;

public abstract class BRCmdExe implements CommandExecutor {
	protected Logger log;
	protected BattleRoyale plugin;
	public BRCmdExe(BattleRoyale plugin) {
		this.log = plugin.getLogger();
		this.plugin = plugin;
	}

	@Override
	abstract public boolean onCommand(CommandSender sender, Command command, String paramString,String[] args);

}
