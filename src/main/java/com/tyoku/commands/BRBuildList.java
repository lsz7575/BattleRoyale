package com.tyoku.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.tyoku.BattleRoyale;

public class BRBuildList extends BRCmdExe {

	public BRBuildList(BattleRoyale plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String paramString, String[] args) {
		if(this.plugin.getCreatedBrBuilds().isEmpty()){
			sender.sendMessage("BR秀夛麗はありません。");
			return true;
		}
		for(String str : this.plugin.getCreatedBrBuilds()){
			sender.sendMessage(str);
		}
		return true;
	}

}