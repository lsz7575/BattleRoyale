package com.tyoku.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.tyoku.BattleRoyale;
import com.tyoku.dto.BRGameStatus;
import com.tyoku.dto.BRPlayer;
import com.tyoku.tasks.CreateDeadZone;
import com.tyoku.tasks.FirstInvincibleTime;
import com.tyoku.util.BRUtils;

public class BrGame extends BRCmdExe {

	public BrGame(BattleRoyale plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender paramCommandSender, Command paramCommand, String paramString,
			String[] paramArrayOfString) {
		if(paramArrayOfString.length != 1 && paramArrayOfString.length != 2 ){
			return false;
		}


		Player p = (Player)paramCommandSender;

		if(!"start".equals(paramArrayOfString[0]) && !p.isOp()){
			p.sendMessage(ChatColor.RED + "権限不足");
			return true;
		}

		if("start".equals(paramArrayOfString[0])){
			if(paramArrayOfString.length != 1){
				return false;
			}

			if(BRUtils.getPlayerBalance(plugin) < 3){
				System.out.println(BRUtils.getPlayerBalance(plugin));
				p.sendMessage(ChatColor.RED + "ゲーム開始には最低3名のプレイヤーが必要です。");
				return true;
			}

			if(!BRGameStatus.OPENING.equals(this.plugin.getBrManager().getGameStatus())){
				paramCommandSender.sendMessage(ChatColor.RED + "既に開始済みです。");
				return true;
			}

			this.plugin.getBrManager().setGameStatus(BRGameStatus.PREPARE);

			//参加者にバトロワMAPとアイテム配布
		    int i = 0;
		    for(Player plr : this.plugin.getServer().getOnlinePlayers()){
				//コンパスのターゲットを設定
				String pname = BRUtils.getRandomPlayer(plugin, plr.getName());
				BRPlayer brp = plugin.getPlayerStat().get(plr.getName());
				//Bukkit.broadcastMessage(ps[i].getName() + " -> "+ pname);
				if(brp == null){
					Bukkit.broadcastMessage("brpはnull");
				}
				brp.setCompassName(pname);

				plr.getInventory().addItem(new ItemStack(Material.CHEST,1));
				plr.getInventory().addItem(new ItemStack(Material.TORCH,10));
				plr.getInventory().addItem(BRUtils.getBRMap(this.plugin, plr, (short)i++));
			}

			//禁止エリア作成非同期処理
			int intervalSecond = plugin.getConfig().getInt("deadarea.interval.second");
			int intervalArea = plugin.getConfig().getInt("deadarea.interval.appenArea");
			if(intervalSecond == 0){
				plugin.getConfig().set("deadarea.interval.second", 60);
				intervalSecond = 60;
				plugin.saveConfig();
			}
			if(intervalArea == 0){
				plugin.getConfig().set("deadarea.interval.appenArea", 10);
				intervalArea = 10;
				plugin.saveConfig();
			}
			this.plugin.setCreateZoneTask(new CreateDeadZone(this.plugin, intervalArea).runTaskTimerAsynchronously(plugin,0, intervalSecond*20));
			this.plugin.setCreateZoneTask(new FirstInvincibleTime(this.plugin, 120).runTaskAsynchronously(plugin));

		}else if("stop".equals(paramArrayOfString[0])){
			if(paramArrayOfString.length != 1){
				return false;
			}
			BRUtils.announce(this.plugin, "ゲームを終了します！");
			this.plugin.getBrManager().setGameStatus(BRGameStatus.END);

		}else if("status".equals(paramArrayOfString[0])){
			if(paramArrayOfString.length != 1 && paramArrayOfString.length != 2 ){
				return false;
			}

			if(paramArrayOfString.length != 2){
				p.sendMessage(String.format(
						"バトロワ：%s"
						,this.plugin.getBrManager().getGameStatus()));
				this.plugin.getBrManager().setGameStatus(BRGameStatus.END);
			}else{
				if(paramCommandSender instanceof Player){
					BRPlayer brp = this.plugin.getPlayerStat().get(paramArrayOfString[1]);
					String pstat = brp==null?"?":brp.getStatus().toString();
					p.sendMessage(String.format(
							"バトロワ：%s is %s."
							, paramArrayOfString[1],pstat));
					this.plugin.getBrManager().setGameStatus(BRGameStatus.END);
				}else{
					paramCommandSender.sendMessage(ChatColor.RED + "You must be a player!");
				}
			}

		}else{
			return false;
		}
		return true;
	}

}