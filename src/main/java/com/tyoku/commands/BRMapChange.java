package com.tyoku.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tyoku.BattleRoyale;
import com.tyoku.dto.BRGameStatus;
import com.tyoku.tasks.Ending;
import com.tyoku.util.BRConst;
import com.tyoku.util.BRUtils;

public class BRMapChange extends BRCmdExe {

	public BRMapChange(BattleRoyale plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String paramString, String[] args) {
		if(args.length != 1){
			return false;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("プレイヤーのみのコマンド");
			return false;
		}


		Player player = (Player)sender;

		if(!BRGameStatus.OPENING.equals(this.plugin.getBrManager().getGameStatus())){
			player.sendMessage("現在は使用できないコマンドです。");
			return true;
		}


		if("yes".equals(args[0].toLowerCase())){
			this.plugin.getVotemap().put(player.getName(), "yes");
		}else if("no".equals(args[0].toLowerCase())){
			this.plugin.getVotemap().put(player.getName(), "no");
		}else{
			return false;
		}

		int yes = getYesCount();
		int no = getNoCount();

		BRUtils.announce(plugin, String.format(
				"MAP変更希望(YES:"+ChatColor.GOLD+"%d "+BRConst.MSG_SYS_COLOR+"NO:"+ChatColor.GOLD+"%d"+BRConst.MSG_SYS_COLOR+")"
				, yes, no));

		if(yes < no){
			return true;
		}

		if(isChangeable()){
			BRUtils.announce(this.plugin, "MAP変更可決");
			this.plugin.setCreateEnding(new Ending(plugin).runTask(plugin));
			return true;
		}else{
			BRUtils.announce(this.plugin, "参加プレイヤーが3人に満たない為マップ変更できません。");
			return true;
		}
	}

	private int getYesCount(){
		int ret = 0;
		for (String vote : this.plugin.getVotemap().values()) {
			if("yes".equals(vote)){
				ret++;
			}
		}
		return ret;
	}

	private int getNoCount(){
		int ret = 0;
		for (String vote : this.plugin.getVotemap().values()) {
			if("no".equals(vote)){
				ret++;
			}
		}
		return ret;
	}

	private boolean isChangeable(){
		int pb = BRUtils.getPlayerBalance(this.plugin);
		if(pb >= 3 ){
			return true;
		}
		return false;
	}

}