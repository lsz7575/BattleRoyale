package com.tyoku;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.tyoku.commands.BRBuildCmd;
import com.tyoku.commands.BRBuildList;
import com.tyoku.commands.BRMapChange;
import com.tyoku.commands.BrGame;
import com.tyoku.commands.GameArea;
import com.tyoku.commands.StartPosCmd;
import com.tyoku.dto.BRBuilding;
import com.tyoku.dto.BRGameStatus;
import com.tyoku.dto.BRManager;
import com.tyoku.dto.BRPlayer;
import com.tyoku.listener.BRPlayerListener;
import com.tyoku.listener.MapListener;
import com.tyoku.util.BRUtils;

public class BattleRoyale extends JavaPlugin {
	private Logger log;
	private File config;
	private BRManager brManager;
	private BRConfig brConfig;
	private Map<String, BRPlayer> playerStat;
	private Map<String, BukkitTask> playerTask;
	private List<String> randomMapBlocks;
	private List<String> deadAreaBlocks;
	private List<String> nextAreaBlocks;
	private BukkitTask createZoneTask;
	private BukkitTask createFirstInvincible;
	private BukkitTask createEnding;
	private Map<String, BRBuilding> brBuilding;
	private boolean isRoomCreated = false;
	private List<String> createdBrBuilds;
	private Map<String, String> votemap;
	private Location location1;
	private Location location2;
	private Location locationBuild;
	private boolean isLastbattle = false;

//	@SuppressWarnings("unused")
//	private DBManager dbm = new DBManager("battleroyale.sqlite3");

	@Override
	public void onEnable() {
		this.log = this.getLogger();
		this.log.info("BattleRoyale configre preparing....");
		this.config = new File(this.getDataFolder(), "config.yml");
		if (!config.exists()) {
			this.saveDefaultConfig();
		}
		this.brManager = new BRManager();
		this.brManager.setGameStatus(BRGameStatus.OPENING);
		this.brConfig = new BRConfig();

		this.randomMapBlocks = BRUtils.getRundumMRMapBlocks();
		nextAreaBlocks = new ArrayList<String>();
		deadAreaBlocks = new ArrayList<String>();
		votemap = new HashMap<String, String>();
		this.createdBrBuilds = new ArrayList<String>();

		setBrBuilding(new HashMap<String, BRBuilding>());
		this.playerStat = new HashMap<String, BRPlayer>();
		this.setPlayerTask(new HashMap<String, BukkitTask>());

		//读取大逃杀建筑物
		loadBRBuildings();

		//随机建筑物
		int buildNum = this.getConfig().getInt("brbuild.num");
		if(buildNum == 0){
			this.getConfig().set("brbuild.num", 15);
			buildNum = 15;
			this.saveConfig();
		}
		createRundomBuild(buildNum);

		//指令载入
		this.log.info("BattleRoyale commands preparing....");
		this.getCommand("setroom").setExecutor(new StartPosCmd(this));
		this.getCommand("brgame").setExecutor(new BrGame(this));
		this.getCommand("setbrarea").setExecutor(new GameArea(this));
		this.getCommand("brbuild").setExecutor(new BRBuildCmd(this));
		this.getCommand("brblist").setExecutor(new BRBuildList(this));
		this.getCommand("brvotemap").setExecutor(new BRMapChange(this));

		//监听载入
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new BRPlayerListener(this), this);
		pm.registerEvents(new MapListener(this), this);

		this.log.info("BattleRoyale Enabled.");
	}

	@Override
	public void onDisable() {
		if (this.createZoneTask != null) {
			this.createZoneTask.cancel();
			this.createZoneTask = null;
		}
		this.log.info("BattleRoyale Disabled.");
	}

	public void setConfig(){
		this.brConfig = new BRConfig();
		this.brConfig.setGameGridSize(this.getConfig().getInt("gamearea.glid"));
	}

	public void loadBRBuildings(){
		this.brBuilding = new HashMap<String, BRBuilding>();
		Pattern pattern = Pattern.compile("brbuild_([^.]*)\\.dat");
		File theDir = new File(this.getDataFolder().getPath());
		if(theDir.exists()){
			File[] files = theDir.listFiles();
			for(int i = 0; i < files.length; i++){
				if(files[i].isFile()){
				    Matcher matcher = pattern.matcher(files[i].getName());
				    if (matcher.find()) {
					      BRBuilding brb = BRBuilding.getBuilding(files[i]);
					      if(brb != null){
					    	  this.brBuilding.put(brb.getName(), brb);
					      }
				    }
				}
			}
		}
		this.log.info("LoadBrBuildings:"+this.brBuilding.size());
	}

	public void createRundomBuild(int buildNum){
		if(this.brBuilding.isEmpty()){
				return;
		}

		World w = this.getServer().getWorlds().get(0);
		int hugo = 1;
		Random rand = new Random();
		for(int i = buildNum; i > 0; i-- ){
			int x = w.getSpawnLocation().getBlockX() + ((rand.nextInt(360)+60) * ((rand.nextInt(10)%2)==0?-1:1));
			int y = 64;
			int z = w.getSpawnLocation().getBlockZ() + ((rand.nextInt(360)+60) * ((rand.nextInt(10)%2)==0?-1:1));

			//int bItem = (int)Math.floor(Math.random() * (this.brBuilding.size()-1)) ;
			int bItem = rand.nextInt(this.brBuilding.size());
			int j = 0;
			for(BRBuilding brb : this.brBuilding.values()){
				if(j++ == bItem){
					if("classroom".equals(brb.getName()) || "lastbattle".equals(brb.getName())){
						buildNum++;
						break;
					}
					y = w.getHighestBlockYAt(x,z);
					brb.create(w, new Location(w, x, y, z), true);
					System.out.println(String.format("%sを座標（X:%d Y:%d Z:%d）", brb.getName(), x, y, z));
					//this.createdBrBuilds.add(ChatColor.AQUA+brb.getName()+" - "+ChatColor.GOLD+"座標(X:"+Integer.toString(x)+" Y:"+Integer.toString(y)+" Z:"+Integer.toString(z)+")");
					this.createdBrBuilds.add(String.format("%s,%d,%d,%d",BRUtils.removeSuffixint(brb.getName()),x,y,z));
					break;
				}
			}
			hugo = hugo*-1;
		}
	}


	public BRManager getBrManager() {
		return brManager;
	}

	public void setBrManager(BRManager manager) {
		this.brManager = manager;
	}

	public Map<String, BRPlayer> getPlayerStat() {
		return playerStat;
	}

	public void setPlayerStat(Map<String, BRPlayer> playerStat) {
		this.playerStat = playerStat;
	}

	public Map<String, BukkitTask> getPlayerTask() {
		return playerTask;
	}

	public void setPlayerTask(Map<String, BukkitTask> playerTask) {
		this.playerTask = playerTask;
	}

	public List<String> getRandomMapBlocks() {
		return randomMapBlocks;
	}

	public void setRandomMapBlocks(List<String> randomMapBlocks) {
		this.randomMapBlocks = randomMapBlocks;
	}

	public List<String> getDeadAreaBlocks() {
		return deadAreaBlocks;
	}

	public void setDeadAreaBlocks(List<String> deadAreaBlocks) {
		this.deadAreaBlocks = deadAreaBlocks;
	}

	/**
	 * @return nextAreaBlocks
	 */
	public List<String> getNextAreaBlocks() {
		return nextAreaBlocks;
	}

	/**
	 * @param nextAreaBlocks set nextAreaBlocks
	 */
	public void setNextAreaBlocks(List<String> nextAreaBlocks) {
		this.nextAreaBlocks = nextAreaBlocks;
	}

	public BukkitTask getCreateZoneTask() {
		return createZoneTask;
	}

	public void setCreateZoneTask(BukkitTask createZoneTask) {
		this.createZoneTask = createZoneTask;
	}

	public BRConfig getBrConfig() {
		return brConfig;
	}

	public void setBrConfig(BRConfig brConfig) {
		this.brConfig = brConfig;
	}

	public BukkitTask getCreateFirstInvincible() {
		return createFirstInvincible;
	}

	public void setCreateFirstInvincible(BukkitTask createFirstInvincible) {
		this.createFirstInvincible = createFirstInvincible;
	}

	public BukkitTask getCreateEnding() {
		return createEnding;
	}

	public void setCreateEnding(BukkitTask createEnding) {
		this.createEnding = createEnding;
	}

	public Map<String, BRBuilding> getBrBuilding() {
		return brBuilding;
	}

	public void setBrBuilding(Map<String, BRBuilding> brBuilding) {
		this.brBuilding = brBuilding;
	}

	public Location getLocation1() {
		return location1;
	}

	public void setLocation1(Location location1) {
		this.location1 = location1;
	}

	public Location getLocation2() {
		return location2;
	}

	public void setLocation2(Location location2) {
		this.location2 = location2;
	}

	public Location getLocationBuild() {
		return locationBuild;
	}

	public void setLocationBuild(Location locationBuild) {
		this.locationBuild = locationBuild;
	}

	public boolean isRoomCreated() {
		return isRoomCreated;
	}

	public void setRoomCreated(boolean isRoomCreated) {
		this.isRoomCreated = isRoomCreated;
	}

	public List<String> getCreatedBrBuilds() {
		return createdBrBuilds;
	}

	public void setCreatedBrBuilds(List<String> createdBrBuilds) {
		this.createdBrBuilds = createdBrBuilds;
	}

	public Map<String, String> getVotemap() {
		return votemap;
	}

	public void setVotemap(Map<String, String> votemap) {
		this.votemap = votemap;
	}

	public boolean isLastbattle() {
		return isLastbattle;
	}

	public void setLastbattle(boolean isLastbattle) {
		this.isLastbattle = isLastbattle;
	}
}