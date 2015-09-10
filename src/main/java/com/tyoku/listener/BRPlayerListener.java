package com.tyoku.listener;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.kotake545.spectator.SpectatorUtil;
import com.tyoku.BattleRoyale;
import com.tyoku.Packet.ParticleAPI;
import com.tyoku.dto.BRBuilding;
import com.tyoku.dto.BRGameStatus;
import com.tyoku.dto.BRPlayer;
import com.tyoku.dto.BRPlayerStatus;
import com.tyoku.util.BRConst;
import com.tyoku.util.BRUtils;
import com.tyoku.util.CommonUtil;

public class BRPlayerListener implements Listener {
	private Logger log;
	private BattleRoyale plugin;

	/**
	 * 建造物の建築のため、開始前のイベントキャンセル（ベッドとかドアの対応）
	 * @param event
	 */
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;
		if (this.plugin.getBrManager().getGameStatus().equals(BRGameStatus.OPENING)) {
			event.setCancelled(true);
		}
	}

	public BRPlayerListener(BattleRoyale battleRoyale) {
		this.plugin = battleRoyale;
		this.log = battleRoyale.getLogger();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		try{
			// プリセット位置へプレイヤーを飛ばす
			int x = this.plugin.getBrConfig().getClassRoomPosX();
			int z = this.plugin.getBrConfig().getClassRoomPosZ();
			int y = this.plugin.getBrConfig().getClassRoomPosY();

			if (x == 1000 && y == 1000 && z == 1000) {
				x = player.getLocation().getBlockX();
				z = player.getLocation().getBlockZ() + 12;
				y = player.getLocation().getBlockY() + 6;
				this.plugin.getBrConfig().setClassRoomPosX(x);
				this.plugin.getBrConfig().setClassRoomPosZ(z);
				this.plugin.getBrConfig().setClassRoomPosY(y);
			}
			if (!this.plugin.isRoomCreated()) {
				BRBuilding brb = this.plugin.getBrBuilding().get("classroom");
				if (brb != null && brb.isCreatable()) {
					brb.create(player.getWorld(), new Location(player.getWorld(), player.getLocation().getBlockX(),
							player.getLocation().getBlockY(), player.getLocation().getBlockZ()), true);
					player.getWorld().setSpawnLocation(x, y, z);
				}
				this.plugin.setRoomCreated(true);
			}
			World w = player.getWorld();
			Location nLoc = new Location(w, x, y, z);
			this.log.info(String.format("プレイヤーを(X:%d Y:%d Z:%d)へ転送", x, y, z));
			player.teleport(nLoc);
			BRUtils.clearPlayerStatus(player);

			String appendMsg = "";
			BRPlayer brps = new BRPlayer();
			brps.setName(player.getName());
			if (BRGameStatus.OPENING.equals(this.plugin.getBrManager().getGameStatus())) {
				player.setDisplayName(BRConst.LIST_COLOR_PLAYER + player.getName());
				player.setPlayerListName(player.getDisplayName());
				// プレイヤーリスト作成
				brps.setStatus(BRPlayerStatus.PLAYING);
			} else {
				brps.setStatus(BRPlayerStatus.DEAD);
				SpectatorUtil.setSpectator(player); //観戦させる
				player.setPlayerListName(BRConst.LIST_COLOR_DEAD + player.getName());
				appendMsg = "ゲームは既に始まっています。次回、ご参加ください。";
			}
			this.plugin.getPlayerStat().put(brps.getName(), brps);

			// インベントリを空にする。
			player.getInventory().clear();

			player.sendMessage(ChatColor.GOLD + "バトロワへようこそ！" + appendMsg);
			player.sendMessage(ChatColor.GOLD + "現在テスト公開してます。ご意見、ご要望などはSkype:tyoku123までメッセージをどうぞ！");
			player.sendMessage(ChatColor.GOLD + "ソース公開、意見求む（汚いですが…）https://github.com/tyoku/battleroyale/");
			player.sendMessage(ChatColor.GOLD + "開始時に配られる地図には禁止エリアと、赤い点でBR特製建造物があるかもしれません。");
			player.sendMessage(ChatColor.GOLD + "コンパスは他の生存プレイヤーを指しているでしょう。");
			player.sendMessage(ChatColor.GOLD + "ゲーム開始時にサバイバルグッズ入りのチェストをお渡しします。");
			player.sendMessage(ChatColor.GOLD + "ゲーム開始コマンド:/brgame start");
			player.sendMessage(ChatColor.GOLD + "ワールド変更投票:/brvotemap [yes|no]");
			player.sendMessage(ChatColor.GOLD + "さぁ、人を集めて" + ChatColor.RED + "殺し合い" + ChatColor.GOLD + "をしてください。");
		}catch(Exception ex){
			System.out.println(ex.getStackTrace());
		}

	}

	@EventHandler
	public void onPlayerChangedWorld(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if (this.plugin.getBrManager().getGameStatus().equals(BRGameStatus.OPENING)) {
			event.setCancelled(true);
		}
		Player player = event.getPlayer();
		Block block = event.getBlock();
		BRPlayer brp = this.plugin.getPlayerStat().get(player.getName());
		if (brp != null && brp.getStatus().equals(BRPlayerStatus.PLAYING) && brp.isFiestChestOpend()
				&& block.getType().equals(Material.CHEST)) {
			Chest chest = (Chest) block.getState();
			Inventory inventory = chest.getInventory();
			for (ItemStack is : BRUtils.getFirstItemStacks()) {
				inventory.addItem(is);
			}
			this.plugin.getPlayerStat().get(player.getName()).setFiestChestOpend(false);
		}

	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		BRUtils.clearPlayerStatus(player);
		BRUtils.teleportRoom(plugin, event.getPlayer());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		BRPlayer brp = this.plugin.getPlayerStat().get(player.getName());

		if (brp == null || !player.isOnline()
				|| !BRGameStatus.PLAYING.equals(this.plugin.getBrManager().getGameStatus())) {
			return;
		}

		if (BRPlayerStatus.DEAD.equals(brp.getStatus())){
			ParticleAPI.sendPlayer(player, ParticleAPI.EnumParticle.WATER_BUBBLE, player.getEyeLocation(), 0.3f,0.3f,0.3f, 0.3f, 10);
			return;
		}

		// 自分を見ている人のコンパスを制御
		Player[] ps = CommonUtil.getOnlinePlayers();
		for (int i = 0; i < ps.length; i++) {
			BRPlayer tmpbrp = plugin.getPlayerStat().get(ps[i].getName());
			//log.info("CompassAllow:" + player.getName() + " -> " + ps[i].getName());
			if (tmpbrp != null && !tmpbrp.getStatus().equals(BRPlayerStatus.DEAD)
					&& tmpbrp.getCompassName().equals(player.getName())) {
				ps[i].setCompassTarget(player.getLocation());
			}
		}

		if (!BRUtils.isGameArea(this.plugin, player)) {
			player.sendMessage(ChatColor.RED + "ゲームエリア外に出た為、5秒後に爆死します。");
			// 殺してステータス変更
			BRUtils.deadCount(player, 5);
			brp.setStatus(BRPlayerStatus.DEAD);
			player.setPlayerListName(BRConst.LIST_COLOR_DEAD + player.getName());
			plugin.getPlayerStat().put(player.getName(), brp);

		} else if (BRUtils.isDeadArea(this.plugin, player)) {
			player.sendMessage(ChatColor.RED + "禁止エリアに進入しました。5秒後に爆死します。");
			// 殺してステータス変更
			BRUtils.deadCount(player, 5);
			brp.setStatus(BRPlayerStatus.DEAD);
			player.setPlayerListName(BRConst.LIST_COLOR_DEAD + player.getName());
			plugin.getPlayerStat().put(player.getName(), brp);

		} else if (BRUtils.isAlertArea(this.plugin, player)) {
			player.sendMessage(ChatColor.RED + "エリア外付近です。エリア外に出ると爆死します。");
		} else {
			// player.sendMessage(ChatColor.GOLD + "ゲームエリア内");
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (plugin.getBrManager().getGameStatus().equals(BRGameStatus.OPENING)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		// プレイヤーへのダメージ制限
		if (!this.plugin.getBrManager().getGameStatus().equals(BRGameStatus.PLAYING)) {
			event.setCancelled(true);
		}

		// 死者からのダメージ制限
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (e.getDamager() instanceof Player) {
				Player player = (Player) e.getDamager();
				BRPlayer brp = this.plugin.getPlayerStat().get(player.getName());
				if (brp == null || BRPlayerStatus.DEAD.equals(brp.getStatus())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageEvent event){
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		// プレイヤーへのダメージ制限
		if (!this.plugin.getBrManager().getGameStatus().equals(BRGameStatus.PLAYING)) {
			event.setCancelled(true);
		}
		// 死者からのダメージ制限
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (e.getDamager() instanceof Player) {
				Player player = (Player) e.getDamager();
				BRPlayer brp = this.plugin.getPlayerStat().get(player.getName());
				if (brp == null || BRPlayerStatus.DEAD.equals(brp.getStatus())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		// ゲーム開始前の破壊活動は禁止
		if (this.plugin.getBrManager().getGameStatus().equals(BRGameStatus.OPENING)) {
			event.setCancelled(true);
		}
		BRPlayer brp = this.plugin.getPlayerStat().get(event.getPlayer().getName());
		if (brp == null || BRPlayerStatus.DEAD.equals(brp.getStatus())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		BRUtils.playerDeathProcess(plugin, player, event.getDeathMessage());
	}

	@EventHandler
	public void noSee(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			Player player = (Player) event.getTarget();
			BRPlayer brp = this.plugin.getPlayerStat().get(player.getName());
			if (brp == null || BRPlayerStatus.DEAD.equals(brp.getStatus())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		// ゲーム開始前の活動は禁止
		if (this.plugin.getBrManager().getGameStatus().equals(BRGameStatus.OPENING)) {
			event.setCancelled(true);
		}
		BRPlayer brp = this.plugin.getPlayerStat().get(event.getPlayer().getName());
		if (brp == null || BRPlayerStatus.DEAD.equals(brp.getStatus())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		BRPlayer brp = this.plugin.getPlayerStat().get(event.getPlayer().getName());
		if (brp == null || BRPlayerStatus.DEAD.equals(brp.getStatus())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerItemHeld(PlayerPickupItemEvent event) {
		if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		BRPlayer brp = this.plugin.getPlayerStat().get(event.getPlayer().getName());
		if (brp == null || BRPlayerStatus.DEAD.equals(brp.getStatus())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if (!BRGameStatus.OPENING.equals(this.plugin.getBrManager().getGameStatus())) {
			BRPlayer brp = this.plugin.getPlayerStat().get(event.getPlayer().getName());
			if (brp == null || BRPlayerStatus.DEAD.equals(brp.getStatus())) {
				return;
			}
			BRUtils.playerDeathProcess(plugin, event.getPlayer(), null);
		}
	}

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		BRPlayer brp = plugin.getPlayerStat().get(player.getName());
		if (brp != null && brp.getStatus().equals(BRPlayerStatus.PLAYING)) {
		} else {
			event.setMessage(ChatColor.GRAY + event.getMessage());
		}
	}

}