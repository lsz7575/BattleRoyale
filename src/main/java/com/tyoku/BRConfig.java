package com.tyoku;

import org.bukkit.Location;
import org.bukkit.World;

public class BRConfig {

	private int gameGridSize = 0;
	private int classRoomPosX = 1000;
	private int classRoomPosY = 1000;
	private int classRoomPosZ = 1000;

	public BRConfig() {
	}

	public Location getRoomLocation(World world){
		return new Location(world, this.classRoomPosX, this.classRoomPosY, this.classRoomPosZ);
	}

	public int getGameGridSize() {
		return gameGridSize;
	}

	public void setGameGridSize(int gameGridSize) {
		this.gameGridSize = gameGridSize;
	}

	public int getClassRoomPosX() {
		return classRoomPosX;
	}

	public void setClassRoomPosX(int classRoomPosX) {
		this.classRoomPosX = classRoomPosX;
	}

	public int getClassRoomPosY() {
		return classRoomPosY;
	}

	public void setClassRoomPosY(int classRoomPosY) {
		this.classRoomPosY = classRoomPosY;
	}

	public int getClassRoomPosZ() {
		return classRoomPosZ;
	}

	public void setClassRoomPosZ(int classRoomPosZ) {
		this.classRoomPosZ = classRoomPosZ;
	}

}
