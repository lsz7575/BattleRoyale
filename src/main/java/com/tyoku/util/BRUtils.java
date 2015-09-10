package com.tyoku.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.potion.PotionEffect;

import com.github.kotake545.spectator.SpectatorUtil;
import com.tyoku.BattleRoyale;
import com.tyoku.dto.BRGameStatus;
import com.tyoku.dto.BRPlayer;
import com.tyoku.dto.BRPlayerStatus;
import com.tyoku.map.BrMapRender;
import com.tyoku.tasks.Ending;

public class BRUtils {

    static public Integer String2Integer(String str) {
            Integer ret = null;
        try {
                ret = Integer.parseInt(str);
            return ret;
        } catch(NumberFormatException e) {
                return null;
        }
    }

    /**
     * オンラインプレイヤー全員にメッセージを送ります。
     * @param plugin
     * @param message
     */
    static public void announce(BattleRoyale plugin, String message){
    	plugin.getServer().broadcastMessage(ChatColor.AQUA + "[放送] " + ChatColor.YELLOW + message);
    }

	/**
	 * プレイヤーがBRゲームエリア内に存在するか否かを判定する。
	 * @param plugin
	 * @param player
	 * @return
	 */
	static public boolean isGameArea(BattleRoyale plugin, Player player) {
		int g = plugin.getConfig().getInt("gamearea.glid");

		int roomX = plugin.getBrConfig().getClassRoomPosX();
		int roomZ = plugin.getBrConfig().getClassRoomPosZ();
		int rangesize = BRConst.BRMAP_GRID_FOR_BLOCK_SIZE * g + BRConst.BRMAP_GRID_FOR_BLOCK_SIZE / 2;
		int x1 = roomX - rangesize;
		int z1 = roomZ - rangesize;
		int x2 = roomX + rangesize;
		int z2 = roomZ + rangesize;
		int w = 0;
		if (x1 < x2) {
			w = x1;
			x1 = x2;
			x2 = w;
		}
		if (z1 < z2) {
			w = z1;
			z1 = z2;
			z2 = w;
		}
		int xp = player.getLocation().getBlockX();
		int zp = player.getLocation().getBlockZ();
		if (x1 >= xp && xp >= x2 && z1 >= zp && zp >= z2) {
			return true;
		}

		return false;
	}

    /**
     * プレイヤーがBRゲームエリア内に存在するか否かを判定する。
     * @param plugin
     * @param player
     * @return
     */
    static public boolean isAlertArea(BattleRoyale plugin, Player player){
    	int g = plugin.getConfig().getInt("gamearea.glid");

        int roomX = plugin.getBrConfig().getClassRoomPosX();
        int roomZ = plugin.getBrConfig().getClassRoomPosZ();
        int rangesize = BRConst.BRMAP_GRID_FOR_BLOCK_SIZE * g + BRConst.BRMAP_GRID_FOR_BLOCK_SIZE / 2;
        int x1 = roomX - rangesize;
        int z1 = roomZ - rangesize;
        int x2 = roomX + rangesize;
        int z2 = roomZ + rangesize;
            int w = 0;
            if(x1 < x2){
                    w = x1;
                    x1 = x2;
                    x2 = w;
            }
            if(z1 < z2){
                    w = z1;
                    z1 = z2;
                    z2 = w;
            }

            int bufferArea = 5;
            x1 -= bufferArea;
            z1 -= bufferArea;
            x2 += bufferArea;
            z2 += bufferArea;

            int xp = player.getLocation().getBlockX();
            int zp = player.getLocation().getBlockZ();
            if(x1 >= xp && xp >= x2 && z1 >= zp && zp >= z2){
                    return false;
            }

            return true;
    }

    /**
     * プレイヤーが禁止エリア内に居る場合にtrueを返却します。
     * @param plugin
     * @param player
     * @return
     */
	public static boolean isDeadArea(BattleRoyale plugin, Player player) {
        int roomX = plugin.getBrConfig().getClassRoomPosX();
        int roomZ = plugin.getBrConfig().getClassRoomPosZ();
        int map0X = roomX - (BRConst.BRMAP_GRID_FOR_BLOCK_SIZE * 6 + BRConst.BRMAP_GRID_FOR_BLOCK_SIZE / 2);
        int map0Z = roomZ - (BRConst.BRMAP_GRID_FOR_BLOCK_SIZE * 6 + BRConst.BRMAP_GRID_FOR_BLOCK_SIZE / 2);
        int playerX = player.getLocation().getBlockX();
        int playerZ = player.getLocation().getBlockZ();
        for(String deadArea : plugin.getDeadAreaBlocks()){
        	int[] da = BRUtils.brMapBlok2XYs(deadArea);
        	int daX = da[1]*BRConst.BRMAP_GRID_FOR_BLOCK_SIZE + map0X;
        	int daZ = da[0]*BRConst.BRMAP_GRID_FOR_BLOCK_SIZE + map0Z;
//        	player.sendMessage(String.format("MAP[a0]座標は(X:%s, Z:%s)", map0X, map0Z));
//        	player.sendMessage(String.format("禁止エリア[%s]座標は(X:%s, Z:%s)", deadArea, daX, daZ));
        	if(daX <= playerX
        			&& playerX <= daX + BRConst.BRMAP_GRID_FOR_BLOCK_SIZE
        			&& daZ <= playerZ
        			&& playerZ <= daZ + BRConst.BRMAP_GRID_FOR_BLOCK_SIZE){
        		return true;
        	}
        }
		return false;
	}

    /**
     * 指定のプレイヤーを指定カウント後に爆死させる。
     * @param player
     * @param count
     */
    static public void deadCount(Player player, int count){
                for (int i = count; i > 0; i--) {
                        try {
                                player.sendMessage(ChatColor.RED + "爆発まで・・・"+i+"秒。");
                                Thread.sleep(1000);
                        } catch (InterruptedException e) {
                                System.out.println(e);
                        }
                }
                float explosionPower = 4F;
                player.sendMessage(ChatColor.RED + "ByeBye!! BOOOOOOOOOOOM!!!");
                player.getWorld().createExplosion(player.getLocation(), explosionPower);
                player.setHealth(0);
    }

    /**
     * プレイヤーを部屋に飛ばす
     * @param plugin
     * @param player
     */
    static public void teleportRoom(BattleRoyale plugin, Player player){
            Location nLoc = getRoomLocation(plugin, player.getWorld());
        player.teleport(nLoc);
        player.sendMessage(ChatColor.GOLD + "しばらくそこでおとなしくしててください。");
    }

    /**
     * 設定した部屋のロケーションを取得する。
     * @param plugin
     * @param world
     * @return
     */
    static public Location getRoomLocation(BattleRoyale plugin, World world){
            //プリセット位置へプレイヤーを飛ばす
    	int x = plugin.getBrConfig().getClassRoomPosX();
    	int z =plugin.getBrConfig().getClassRoomPosZ();
            return new Location(world, x, 64, z);
    }

    /**
     * 指定の番号を振ったBRマップを作成する。
     * @param plugin
     * @param player
     * @param mapNo
     * @return
     */
    static public ItemStack getBRMap(BattleRoyale plugin,Player player, short id){
            ItemStack tmap = new ItemStack( Material.MAP, 1 ,(short)id);
            MapView mapview = Bukkit.getServer().getMap((short)id);
            if (mapview == null) {
            	mapview = Bukkit.createMap(Bukkit.getWorlds().get(0));
            }
            mapview.addRenderer(new BrMapRender(plugin));
            mapview.setCenterX(plugin.getBrConfig().getClassRoomPosX());
            mapview.setCenterZ(plugin.getBrConfig().getClassRoomPosZ());
            mapview.setWorld(player.getWorld());
            mapview.setScale( Scale.FAR );
        return tmap;
    }

    static public List<String> getRundumMRMapBlocks(){
        		String[] numary = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","13"};
                String[] alphaary = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n"};
                String[] pareAry = new String[numary.length*alphaary.length];
                int b = 0;
                for (int i=0; i < numary.length; i++) {
                        for (int j=0; j < alphaary.length; j++) {
                                pareAry[b++]=alphaary[j]+numary[i];
                        }
                }

                Random rgen = new Random();  // Random number generator
                //--- Shuffle by exchanging each element randomly
                for (int i=0; i < pareAry.length; i++) {
                int randomPosition = rgen.nextInt(pareAry.length);
                String temp = pareAry[i];
                pareAry[i] = pareAry[randomPosition];
                pareAry[randomPosition] = temp;
                }

                List<String> ret = new ArrayList<String>();
                for (int i=0; i < pareAry.length; i++) {
                        ret.add(pareAry[i]);
                }

                return ret;
    }

    /**
     * BR地図ブロックの場所を数値で返却。
     * @param brk
     * @return int[0]は数字の場所
     * 　　　　int[1]はアルファベットの場所
     */
    static public int[] brMapBlok2XYs(String brk){
            int[] ret = new int[2];
                String[] numary = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","13"};
                String[] alphaary = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n"};
                String alpha = brk.substring(0,1);
                String num = brk.substring(1,brk.length());
                int alphapos = 0;
                int numpos = 0;
                for (int i=0; i < numary.length; i++) {
                        if(numary[i].equals(num)){
                            numpos = i;
                                break;
                        }
                }
                for (int j=0; j < alphaary.length; j++) {
                        if(alphaary[j].equals(alpha)){
                                alphapos = j;
                                break;
                        }
                }
            ret[0]=alphapos;
            ret[1]=numpos;
            return ret;
    }


    /**
     * マイクラのロケーションをBRマップ上の座標に変換
     * @param loc
     * @return
     */
    static public String minloc2BRloc(Location loc){
    	String ret = "";
    	return ret;
    }

    /**
     * BRマップ上の指定の座標の色を指定の色で塗る。
     * @param mc
     * @param blocks
     * @param color
     */
    static public void drawBRBapBlocks(MapCanvas mc, List<String> blocks, byte color){
        int draszie = 10;
        int padding = 10;
        for (String brk : blocks) {
            int[] pos = BRUtils.brMapBlok2XYs(brk);
            for (int i = pos[1]*padding+1; i < pos[1]*padding + draszie; i++) {
                for (int j = pos[0]*padding+1; j < pos[0]*padding + draszie; j++) {
                    mc.setPixel(i, j, color);
                }
            }
        }
    }

    static public void drawBrbuild(MapCanvas mc, BattleRoyale plugin, byte color){
    	int baseX = plugin.getBrConfig().getClassRoomPosX();
    	int baseZ = plugin.getBrConfig().getClassRoomPosZ();
		int dx = 65 - (int)Math.floor(baseX/8);
		int dz = 65 - (int)Math.floor(baseZ/8);
    	for (String str : plugin.getCreatedBrBuilds()) {
    		String[] builds = str.split(",");
    		int buildX = (int)Math.floor(Integer.parseInt(builds[1]) / 8);
    		int buildZ = (int)Math.floor(Integer.parseInt(builds[3]) / 8);
    		mc.setPixel(buildX + dx, buildZ + dz, color);
		}

    }

    static public String removeSuffixint(String str){
    	Pattern p = Pattern.compile("(%d*)$");

    	Matcher m = p.matcher(str);
    	if(m.find()){
        	return m.replaceAll("");
    	}
    	return str;
    }


    static public List<ItemStack> getFirstItemStacks(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        //共通支給品
        ret.add(new ItemStack(Material.POTION, 1));
        ret.add(new ItemStack(Material.BREAD, 5));
        ret.add(new ItemStack(Material.WATER, 1));
        ret.add(new ItemStack(Material.COMPASS, 1));
        ret.add(new ItemStack(Material.WATCH, 1));
        ret.add(new ItemStack(Material.WORKBENCH, 1));
        Random rand = new Random();

        //ボーナスアイテム
        List<ItemStack> bonusItems = new ArrayList<ItemStack>();
        for(int i = 0; i < 2; i++){
            int bItem = rand.nextInt(9);
            switch (bItem) {
            case 0:
                bonusItems.addAll(BRKit.getAlchemistKit());
                break;
            case 1:
                bonusItems.addAll(BRKit.getArcherKit());
                break;
            case 2:
                bonusItems.addAll(BRKit.getBadluckKit());
                break;
            case 3:
                bonusItems.addAll(BRKit.getBomberKit());
                break;
            case 4:
                bonusItems.addAll(BRKit.getFireManKit());
                break;
            case 5:
                bonusItems.addAll(BRKit.getMinerKit());
                break;
            case 6:
                bonusItems.addAll(BRKit.getMountaineerKit());
                break;
            case 7:
                bonusItems.addAll(BRKit.getRunnerKit());
                break;
            case 8:
                bonusItems.addAll(BRKit.getSwimerKit());
                break;
            case 9:
                bonusItems.addAll(BRKit.getSwordmanKit());
                break;
            }
        }

        ret.addAll(bonusItems);

        return ret;
    }

    static public void clearPlayerStatus(Player player){
    	player.setGameMode(GameMode.SURVIVAL);
    	player.setAllowFlight(false);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setLevel(0);
        for(PotionEffect pe : player.getActivePotionEffects()){
        	player.removePotionEffect(pe.getType());
        }
    }

    /**
     * 残存プレイヤー数を返す
     * @param plugin
     * @return
     */
    static public int getPlayerBalance(BattleRoyale plugin){
    	int cnt = 0;
		Player[] ps = CommonUtil.getOnlinePlayers();
		for(int i = 0; i < ps.length; i++){
			if(!SpectatorUtil.checkSpectator(ps[i]))
				cnt++;
		}
    	return cnt;
    }


    /**
     * 参加者に音を聞かせる。
     * @param plugin
     * @param effect
     */
    static public void soundAllPlayer(BattleRoyale plugin, Effect effect){
		Player[] ps = CommonUtil.getOnlinePlayers();
		for(int i = 0; i < ps.length; i++){
			ps[i].playEffect(ps[i].getLocation(), effect, 1007);
		}
    }

    /**
     * プレイヤーが減ったときにやること。
     * @param plugin
     * @param player
     * @param kickMsg
     */
    static public void playerDeathProcess(BattleRoyale plugin, Player player, String kickMsg){
    	if(plugin.getBrManager().getGameStatus().equals(BRGameStatus.END)){
    		return;
    	}
		if(kickMsg != null){
			player.kickPlayer(kickMsg);
		}

		int pb = BRUtils.getPlayerBalance(plugin) ;
		if(pb > 1){
			String msg = String.format(
					"%sが死亡しました。【残り%d人】", player.getName(),pb);
			BRUtils.announce(plugin, msg);
			//死んだプレイヤーをさしてたコンパスを修正
			for(BRPlayer brp : plugin.getPlayerStat().values()){
				if(brp.getCompassName() != null && brp.getCompassName().equals(player.getName())){
					brp.setCompassName(BRUtils.getRandomPlayer(plugin, brp.getName()));
				}
			}
			if(pb == 2){
				//決着
				BRUtils.setLastBattle(plugin,player.getWorld());
			}

		}else if(pb == 0){
			BRUtils.announce(plugin, "ゲーム終了・・・"+ChatColor.RED+"全員死亡"+BRConst.MSG_SYS_COLOR+"の為、優勝者なし。");
			plugin.setCreateEnding(new Ending(plugin).runTask(plugin));
		}else if(pb == 1){
			String winner = "不明";
			for(BRPlayer brp : plugin.getPlayerStat().values()){
				if(BRPlayerStatus.PLAYING.equals(brp.getStatus())){
					winner = brp.getName();
				}
			}
			BRUtils.announce(plugin, String.format(
					"ゲーム終了・・・優勝者は【"+ChatColor.GOLD+"%s"+BRConst.MSG_SYS_COLOR+"】です！おめでとう！！", winner));
			plugin.setCreateEnding(new Ending(plugin).runTask(plugin));
		}
    }

    /**
     * 最終バトルステージを作成
     * @param plugin
     * @param world
     */
    static public void setLastBattle(BattleRoyale plugin, World world){
		BRUtils.announce(plugin, "残りプレイヤー2名になりましたので、ラストバトルへ移行します。");
		//BRUtils.createLastStage(plugin);
		//'plugin.setLastbattle(true);
		plugin.getDeadAreaBlocks().clear();
		plugin.getRandomMapBlocks().clear();
		plugin.getNextAreaBlocks().clear();

		int x = plugin.getBrConfig().getClassRoomPosX();
		int z = plugin.getBrConfig().getClassRoomPosZ();
		int y = plugin.getBrConfig().getClassRoomPosY();
		Location loc = new Location(world, x, y, z);


    	//プレイヤー全員中央へ飛ばす
    	Player[] ps = CommonUtil.getOnlinePlayers();
    	for(BRPlayer p : plugin.getPlayerStat().values()){
    		if(BRPlayerStatus.PLAYING.equals(p.getStatus())){
    			for(int i = 0; i < ps.length; i++){
    				if(ps[i].getName().equals(p.getName())){
    					ps[i].teleport(loc);
    					break;
    				}
    			}
    		}
    	}

		//中央をデッドエリアで囲む
		plugin.getDeadAreaBlocks().add("f5");
		plugin.getDeadAreaBlocks().add("f6");
		plugin.getDeadAreaBlocks().add("f7");
		plugin.getDeadAreaBlocks().add("g5");
		plugin.getDeadAreaBlocks().add("g7");
		plugin.getDeadAreaBlocks().add("h5");
		plugin.getDeadAreaBlocks().add("h6");
		plugin.getDeadAreaBlocks().add("h7");
		BRUtils.announce(plugin, "中央の区画の周りは禁止エリアです。殺しあってください。");

    }

    /**
     * ランダムにゲーム中のプレイヤーを取得
     * @param plugin
     * @return
     */
    static public String getRandomPlayer(BattleRoyale plugin, String ignore){
    	Collection<BRPlayer> brps = plugin.getPlayerStat().values();
    	List<String> p = new ArrayList<String>();
    	for(BRPlayer brp : brps){
    		if(BRPlayerStatus.PLAYING.equals(brp.getStatus())){
    			p.add(brp.getName());
    		}
    	}
    	if(ignore != null){
    		p.remove(ignore);
    	}
    	if(p.isEmpty()){
    		return ignore;
    	}
    	Random rand = new Random();
    	int ridx = rand.nextInt(p.size());
    	return p.get(ridx);
    }

    static public void createLastStage(BattleRoyale plugin){
		Location battleLocation = new Location(plugin.getServer().getWorlds().get(0),
				plugin.getBrConfig().getClassRoomPosX(),
				plugin.getBrConfig().getClassRoomPosY() + 50,
				plugin.getBrConfig().getClassRoomPosZ());
		plugin.getBrBuilding().get("lastbattle").create(plugin.getServer().getWorlds().get(0), battleLocation, true);
    }

	static public void setLastBattlePlayer(BattleRoyale plugin, Player player, int position) {
		int addNum = 0;
		if(position == 2){
			addNum = -14;
		}
		Location battleLocation = new Location(player.getWorld(),
					plugin.getBrConfig().getClassRoomPosX() + addNum,
					plugin.getBrConfig().getClassRoomPosY() + 50,
					plugin.getBrConfig().getClassRoomPosZ() + addNum);
		player.teleport(battleLocation);

	}

//	/**
//	 * プレイヤーのフィールド上での名前を変更する。
//	 * @param player
//	 * @param newName
//	 */
//	static public void setPlayerFieldName(Player player, String newName) {
//		((CraftPlayer) player).getHandle().name = newName;
//	}
}