package com.tyoku.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tyoku.BattleRoyale;
import com.tyoku.dto.BRBuilding;

public class BRBuildCmd extends BRCmdExe {

	public BRBuildCmd(BattleRoyale plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String paramString, String[] args) {
		if(args.length != 1 && args.length != 2  && args.length != 3){
			return false;
		}
		try {
			if ((sender instanceof Player)) {
				Player player = (Player)sender;

				if(!player.isOp()){
					player.sendMessage(ChatColor.RED + "���޲���");
					return true;
				}

				//һ�E
				if("list".equals(args[0])){
					if(args.length != 1){
						return false;
					}
					StringBuffer sb = new StringBuffer();
					sb.append(ChatColor.YELLOW + "BR���B�" + ChatColor.GOLD);
					if(this.plugin.getBrBuilding().size() > 0){
						for(BRBuilding brb : this.plugin.getBrBuilding().values()){
							sb.append(brb.getName()+",");
						}
					}else{
						sb.append("���h����Ƥ��ޤ���");
					}
					player.sendMessage(sb.toString());
					return true;
				}

				//���B
				if("create".equals(args[0])){
					if(args.length != 2  && args.length != 3){
						player.sendMessage(ChatColor.YELLOW + "�����������`�꤬����ޤ���");
						return false;
					}

					boolean ignore = true;
					if(args.length == 3){
						ignore = false;
					}

					BRBuilding brb = this.plugin.getBrBuilding().get(args[1]);
					if(brb == null || !brb.isCreatable()){
						player.sendMessage(ChatColor.YELLOW + "���B�郎���ڤ��ʤ��������B�Ǥ���״�B�ǤϤ���ޤ���");
					}
					return brb.create(player.getWorld(), player.getLocation(), ignore);
				}

				//����
				if("save".equals(args[0])){
					if(args.length != 2){
						player.sendMessage(ChatColor.YELLOW + "�����������`�꤬����ޤ���");
						return false;
					}

					if(this.plugin.getLocation1() == null
							|| this.plugin.getLocation2() == null
							|| this.plugin.getLocationBuild() == null){
						player.sendMessage(ChatColor.YELLOW + "���`����󡸣��������۩`�ࡹȫ���O�����Ƥ���������");
						return true;
					}

					BRBuilding brb = new BRBuilding(player, args[1], this.plugin.getLocation1(), this.plugin.getLocation2(), this.plugin.getLocationBuild());

					if(!brb.isCreatable()){
						player.sendMessage(ChatColor.YELLOW + "���B�����Τ�����ޤ���");
					}else if(brb.save(this.plugin)){
						player.sendMessage(ChatColor.YELLOW + "���B��򱣴椷�ޤ�����"+brb.getName()+" �֥�å���:"+brb.getBlockNum());
					}else{
						player.sendMessage(ChatColor.YELLOW + "���B��α����ʧ����������`");
					}
					return true;
				}

				//�O��
				if("set".equals(args[0])){
					if(args.length != 2){
						return false;
					}

					if("1".equals(args[1])){
						this.plugin.setLocation1(player.getLocation());
						player.sendMessage(String.format(ChatColor.YELLOW + "���`����󣱤�����(X:%d, Y:%d, Z:%d)���O�����ޤ�����"
								, this.plugin.getLocation1().getBlockX()
								, this.plugin.getLocation1().getBlockY()
								, this.plugin.getLocation1().getBlockZ()));
					}
					if("2".equals(args[1])){
						this.plugin.setLocation2(player.getLocation());
						player.sendMessage(String.format(ChatColor.YELLOW + "���`�����2������(X:%d, Y:%d, Z:%d)���O�����ޤ�����"
								, this.plugin.getLocation2().getBlockX()
								, this.plugin.getLocation2().getBlockY()
								, this.plugin.getLocation2().getBlockZ()));
					}
					if("home".equals(args[1])){
						this.plugin.setLocationBuild(player.getLocation());
						player.sendMessage(String.format(ChatColor.YELLOW + "���B�۩`�������(X:%d, Y:%d, Z:%d)���O�����ޤ�����"
								, this.plugin.getLocationBuild().getBlockX()
								, this.plugin.getLocationBuild().getBlockY()
								, this.plugin.getLocationBuild().getBlockZ()));
					}
					return true;
				}

			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player!");
				return false;
			}

		} catch (Exception e){
			return false;
		}
		return false;
	}

}