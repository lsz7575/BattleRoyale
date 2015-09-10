package com.tyoku.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tyoku.BattleRoyale;
import com.tyoku.util.BRConst;
import com.tyoku.util.BRUtils;

public class StartPosCmd extends BRCmdExe{

	public StartPosCmd(BattleRoyale plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] arg3) {
		try {
			if ((sender instanceof Player)) {
				if(!sender.isOp()){
					sender.sendMessage(ChatColor.RED + "権限不足");
					return true;
				}

				if(arg3.length != 3 && arg3.length != 0){
					return false;
				}

				Player player = (Player) sender;
				Location myLoc = player.getLocation();

				//ユーザの座標を保持
				int x = myLoc.getBlockX();
				int y = myLoc.getBlockY();
				int z = myLoc.getBlockZ();
				x = myLoc.getBlockX();
				y = myLoc.getBlockY();
				z = myLoc.getBlockZ();
				if(arg3.length == 3){
					Integer ix = BRUtils.String2Integer(arg3[0]);
					Integer iy = BRUtils.String2Integer(arg3[1]);
					Integer iz = BRUtils.String2Integer(arg3[2]);
					if(ix != null && iy != null && iz != null){
						//引数があればそれを座標に設定
						x = ix;
						y = iy;
						z = iz;
					}
				}

				//設定上書き
				this.plugin.getBrConfig().setClassRoomPosX(x);
				this.plugin.getBrConfig().setClassRoomPosX(y);
				this.plugin.getBrConfig().setClassRoomPosX(z);
			    player.sendMessage(String.format(BRConst.MSG_SYS_COLOR + "初期スポーンを(X:%d Y:%d Z:%d)に設定しました。", x,y,z));

			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player!");
				return false;
			}

		} catch (Exception e){
		}
		return true;
	}

}