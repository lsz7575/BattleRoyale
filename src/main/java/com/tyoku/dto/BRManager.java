package com.tyoku.dto;


public class BRManager {
	private BRGameStatus gameStatus = BRGameStatus.OPENING;

	public BRGameStatus getGameStatus() {
		return gameStatus;
	}
	public void setGameStatus(BRGameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}
}
