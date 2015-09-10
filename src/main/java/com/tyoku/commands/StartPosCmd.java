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
					sender.sendMessage(ChatColor.RED + "���޲���");
					return true;
				}

				if(arg3.length != 3 && arg3.length != 0){
					return false;
				}

				Player player = (Player) sender;
				Location myLoc = player.getLocation();

				//��`�������ˤ򱣳�
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
						//����������Ф�������ˤ��O��
						x = ix;
						y = iy;
						z = iz;
					}
				}

				//�O���ϕ���
				this.plugin.getBrConfig().setClassRoomPosX(x);
				this.plugin.getBrConfig().setClassRoomPosX(y);
				this.plugin.getBrConfig().setClassRoomPosX(z);
			    player.sendMessage(String.format(BRConst.MSG_SYS_COLOR + "���ڥ��ݩ`���(X:%d Y:%d Z:%d)���O�����ޤ�����", x,y,z));

			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player!");
				return false;
			}

		} catch (Exception e){
		}
		return true;
	}

}