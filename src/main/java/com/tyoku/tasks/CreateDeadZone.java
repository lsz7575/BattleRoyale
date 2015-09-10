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
		BRUtils.announce(plugin, "10秒後に禁止エリアを設定、さらに次回の禁止エリアを設定します。禁止エリアに居るプレイヤーは爆死します。");
		for (int i = 10; i > 0; i--) {
			try {
				if(i <= 5 )
					BRUtils.announce(plugin, "禁止エリア確定まで"+i+"秒。");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
		//警告エリアをデッドエリアに指定して警告エリアをクリア
		this.plugin.getDeadAreaBlocks().addAll(this.plugin.getNextAreaBlocks());
		this.plugin.getNextAreaBlocks().clear();

		//禁止エリアから指定の数を
		for(int i = 0; i < this.appenAreaNum; i++){
			if(this.plugin.getRandomMapBlocks().size() == 0){
				break;
			}
			this.plugin.getNextAreaBlocks().add(this.plugin.getRandomMapBlocks().get(0));
			this.plugin.getRandomMapBlocks().remove(0);
		}

		BRUtils.announce(plugin, "禁止エリアと次回の予告エリアを設定しました。各自配布されたMAPを確認しましょう。");
	}
}