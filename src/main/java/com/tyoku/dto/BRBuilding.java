package com.tyoku.dto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Door;

import com.tyoku.BattleRoyale;
import com.tyoku.util.InventoryStringDeSerializer;

public class BRBuilding implements Serializable {
	private static final long serialVersionUID = 199035831519635924L;

	private String name;
	private BRBuildBlock home;
	private List<BRBuildBlock> buildblocks;

	public BRBuilding(Player player, String name, Location loc1, Location loc2, Location standLoc) {
		if(player == null || name == null || loc1 == null || loc2 == null || standLoc == null){
			return;
		}
		this.setName(name);
		//座標認識
		int homeX = standLoc.getBlockX();
		int homeY = standLoc.getBlockY();
		int homeZ = standLoc.getBlockZ();
		int x1 = loc1.getBlockX();
		int y1 = loc1.getBlockY();
		int z1 = loc1.getBlockZ();
		int x2 = loc2.getBlockX();
		int y2 = loc2.getBlockY();
		int z2 = loc2.getBlockZ();

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
		if (y1 < y2) {
			w = y1;
			y1 = y2;
			y2 = w;
		}

		//ホームロケーション設定
		home = new BRBuildBlock();
		home.setX(homeX - x1);
		home.setY(homeY - y1);
		home.setZ(homeZ - z1);
		try{
			//ブロック取得
			buildblocks = new ArrayList<BRBuildBlock>();
			for(int i = x2; i <= x1; i++){
				for(int j = y2; j <= y1; j++){
					for(int k = z2; k <= z1; k++){
						BRBuildBlock brblock = new BRBuildBlock();
						brblock.setX(x1 + i - homeX - homeX );
						brblock.setY(y1 + j - homeY - homeY );
						brblock.setZ(z1 + k - homeZ - homeZ );

						Block bl = player.getWorld().getBlockAt(i, j, k);
						brblock.setType(bl.getType());
						brblock.setBlockData(bl.getData());
						brblock.setEmpty(bl.isEmpty());

						//インベントリの保管
						if(!bl.isEmpty()){
							if(bl.getType().equals(Material.CHEST)){
							    Chest chest = (Chest)bl.getState();
							    Inventory inventory = chest.getInventory();
							    brblock.setInventoryStr(InventoryStringDeSerializer.InventoryToString(inventory));
							}
						}

						//看板の保管
						if(bl.getType().equals(Material.SIGN_POST)){
						    Sign sign = (Sign)bl.getState();
						    brblock.setSignTexts(sign.getLines());
						}

						//Faceの保管
						if(bl.getType().equals(Material.WOODEN_DOOR)){
							Door d = (Door) bl.getState().getData();
						    brblock.setTopHalf( d.isTopHalf());
						    brblock.setBlockFace(d.getFacing());
						    brblock.setOpen(d.isOpen());
						}

						buildblocks.add(brblock);
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * 指定のロケーションにBR建造物を建てる。
	 * @param world
	 * @param buildLocation
	 */
	public boolean create(World world, Location location, boolean igunoreEnderStone){
		//座標差分取得
		int xd = location.getBlockX()+home.getX();
		int yd = location.getBlockY()+home.getY();
		int zd = location.getBlockZ()+home.getZ();

		List<BRBuildBlock> afterBlock = new ArrayList<BRBuildBlock>();

		for(BRBuildBlock brb : this.buildblocks){
			int xb = brb.getX() + xd;
			int yb = brb.getY() + yd;
			int zb = brb.getZ() + zd;
			Block wb = world.getBlockAt(xb, yb, zb);

			//エンドストーンは無視
			if(igunoreEnderStone && Material.ENDER_STONE.equals(brb.getType())){
				continue;
			}

			//複ブロックや周りに影響される系はあとまわし。
			if(Material.IRON_DOOR_BLOCK.equals(brb.getType())
					|| Material.WOODEN_DOOR.equals(brb.getType())
					|| Material.TRIPWIRE_HOOK.equals(brb.getType())
					|| Material.LADDER.equals(brb.getType())
					|| Material.BED_BLOCK.equals(brb.getType())
					|| Material.TRAP_DOOR.equals(brb.getType())
					|| Material.TORCH.equals(brb.getType())
					|| Material.SIGN_POST.equals(brb.getType())
					){
				afterBlock.add(brb);

				//とりあえず空気置いとく。
				wb.setType(Material.AIR);
				continue;
			}

			wb.setData(brb.getBlockData());
			wb.setType(brb.getType());
			if(!brb.isEmpty()){
				if(wb.getType().equals(Material.CHEST)){
				    Chest chest = (Chest)wb.getState();
				    Inventory inventory = chest.getInventory();
				    inventory.setContents(InventoryStringDeSerializer.StringToInventory(brb.getInventoryStr()).getContents());
				}
			}
		}

		Collections.sort(afterBlock, new BRBuildBlockComparator());
		try{
			for(BRBuildBlock brb : afterBlock){
				int xb = brb.getX() + xd;
				int yb = brb.getY() + yd;
				int zb = brb.getZ() + zd;
				Block wb = world.getBlockAt(xb, yb, zb);

				wb.setData(brb.getBlockData());
				wb.setType(brb.getType());

				if(wb.getType().equals(Material.SIGN_POST)){
				    Sign sign = (Sign)wb.getState();
				    String[] txts =  brb.getSignTexts();
				    if(txts != null && txts.length > 0){
				    	for(int i = 0; i < txts.length; i++){
				    		sign.setLine(i, txts[i]);
				    	}
				    }
				    sign.update();
				}

				if(wb.getType().equals(Material.WOODEN_DOOR)){
					if(brb.getBlockFace() != null){
						Door d = (Door) wb.getState().getData();
						d.setTopHalf(brb.isTopHalf());
						d.setFacingDirection(brb.getBlockFace());
						d.setOpen(brb.isOpen());
					}
				}
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}

		return true;
	}

	/**
	 * このクラスオブジェクトシリアライズしてファイルとして保存する。
	 */
	public boolean save(BattleRoyale plugin) {
		try {
			FileOutputStream outFile = new FileOutputStream(plugin.getDataFolder().getPath() + "/brbuild_"+ this.name + ".dat");
			ObjectOutputStream outObject = new ObjectOutputStream(outFile);
			plugin.getBrBuilding().put(this.name, this);
			outObject.writeObject(this);
			outObject.close();
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isCreatable(){
		return buildblocks != null && buildblocks.size() > 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public int getBlockNum(){
		return buildblocks.size();
	}

	public BRBuildBlock getHome() {
		return home;
	}

	static public BRBuilding getBuilding(File file) {
		if (file.exists()) {
			FileInputStream inFile = null;
			ObjectInputStream inObject = null;
			try {
				inFile = new FileInputStream(file);
				inObject = new ObjectInputStream(inFile);

				Object obj = inObject.readObject();
				if (obj instanceof BRBuilding) {
					return (BRBuilding) obj;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			} finally {
				if(inObject != null){
					try {
						inObject.close();
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}

				if(inFile != null){
					try {
						inFile.close();
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}
			}
		} else {
			return null;
		}
		return null;
	}
}