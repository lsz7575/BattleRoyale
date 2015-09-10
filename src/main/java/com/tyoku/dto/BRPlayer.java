package com.tyoku.dto;


public class BRPlayer {
	private String name;
	private short durability;
	private BRPlayerStatus status;
	private int playCount = 0;
	private boolean isFiestChestOpend = true;
	private String compassName;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BRPlayerStatus getStatus() {
		return status;
	}
	public void setStatus(BRPlayerStatus status) {
		this.status = status;
	}
	public int getPlayCount() {
		return playCount;
	}
	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}
	public short getDurability() {
		return durability;
	}
	public void setDurability(short durability) {
		this.durability = durability;
	}
	public boolean isFiestChestOpend() {
		return isFiestChestOpend;
	}
	public void setFiestChestOpend(boolean isFiestChestOpend) {
		this.isFiestChestOpend = isFiestChestOpend;
	}
	public String getCompassName() {
		return compassName;
	}
	public void setCompassName(String compassName) {
		this.compassName = compassName;
	}
}
