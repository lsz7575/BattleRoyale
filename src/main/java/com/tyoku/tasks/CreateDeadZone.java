package com.tyoku.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.tyoku.BattleRoyale;
import com.tyoku.util.BRUtils;

public class CreateDeadZone extends BukkitRunnable {
	private final BattleRoyale plugin;
	private final int appenAreaNum;

	public CreateDeadZone(BattleRoyale plugin, int appenAreaNum) {
		this.plugin = plugin;
		this.appenAreaNum = appenAreaNum;
	}

	@Override
	public void run() {
		BRUtils.announce(plugin, "10����˽�ֹ���ꥢ���O��������˴λؤν�ֹ���ꥢ���O�����ޤ�����ֹ���ꥢ�˾Ӥ�ץ쥤��`�ϱ������ޤ���");
		for (int i = 10; i > 0; i--) {
			try {
				if(i <= 5 )
					BRUtils.announce(plugin, "��ֹ���ꥢ�_���ޤ�"+i+"�롣");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
		//���楨�ꥢ��ǥåɥ��ꥢ��ָ�����ƾ��楨�ꥢ�򥯥ꥢ
		this.plugin.getDeadAreaBlocks().addAll(this.plugin.getNextAreaBlocks());
		this.plugin.getNextAreaBlocks().clear();

		//��ֹ���ꥢ����ָ��������
		for(int i = 0; i < this.appenAreaNum; i++){
			if(this.plugin.getRandomMapBlocks().size() == 0){
				break;
			}
			this.plugin.getNextAreaBlocks().add(this.plugin.getRandomMapBlocks().get(0));
			this.plugin.getRandomMapBlocks().remove(0);
		}

		BRUtils.announce(plugin, "��ֹ���ꥢ�ȴλؤ���楨�ꥢ���O�����ޤ����������䲼���줿MAP��_�J���ޤ��礦��");
	}
}