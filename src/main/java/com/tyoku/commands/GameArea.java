package com.tyoku.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tyoku.BattleRoyale;

public class GameArea extends BRCmdExe{

	public GameArea(BattleRoyale plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String paramString, String[] args) {
		try {
			if ((sender instanceof Player)) {
				if(!sender.isOp()){
					sender.sendMessage(ChatColor.RED + "���޲���");
					return true;
				}

				if(args.length != 1){
					return false;
				}
				int num = Integer.parseInt(args[0]);

				Player player = (Player) sender;

				//�O���ϕ���
			    this.plugin.getConfig().set("gamearea.glid",num);
			    this.plugin.saveConfig();
			    this.plugin.getBrConfig().setGameGridSize(num);
			    player.sendMessage(String.format("���`�२�ꥢ�Ϸ�У����%d�ޥ��Ǥ���", num));

			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player!");
				return false;
			}

		} catch (Exception e){
			return false;
		}
		return true;
	}

}