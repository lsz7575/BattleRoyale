package com.tyoku.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.tyoku.BattleRoyale;
import com.tyoku.dto.BRGameStatus;
import com.tyoku.util.BRConst;
import com.tyoku.util.BRUtils;

public class FirstInvincibleTime extends BukkitRunnable {
	private final BattleRoyale plugin;
	private final int time;

	public FirstInvincibleTime(BattleRoyale plugin, int time) {
		this.plugin = plugin;
		this.time = time;
	}

	@Override
	public void run() {

		BRUtils.announce(plugin, "�o���r�g��"+BRConst.MSG_IMPORTANT_NUM_COLOR+this.time+BRConst.MSG_IMPORTANT_COLOR+"�����⤷�ޤ����������g�ˤǤ���������`��Μʂ�򤷤Ƥ���������");
		for (int i = this.time; i > 0; i--) {
			try {
				if(i <= 5 || i == 30 )
					BRUtils.announce(plugin,"�o���r�g�K�ˤޤ�"+BRConst.MSG_IMPORTANT_NUM_COLOR+""+i+""+BRConst.MSG_IMPORTANT_COLOR+"�롣");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
		BRUtils.announce(plugin,"�o���r�g�K�ˡ��������������äƤ���������");
		this.plugin.getBrManager().setGameStatus(BRGameStatus.PLAYING);

	}

}