package simulator;

import simulator.game.*;

public class GameMapState {
	GameMap gameMap;

	public GameMapState(GameMap gameMap) {
		this.gameMap = gameMap;
	}

	public void incSoldierPerTick() {
		for (int i = 0; i < gameMap.height * gameMap.width; i++) {
			if (gameMap.grid[i].isCrownOrCastle()) {
				gameMap.grid[i].soldiers++;
			}
		}
	}

	public void incSoldierPerRound() {
		for (int i = 0; i < gameMap.height * gameMap.width; i++) {
			if (gameMap.grid[i].isLand()) {
				gameMap.grid[i].soldiers++;
			}
		}
	}

	public GameTick applyMoveAction(String user, MoveAction movement) {
		// TODO

		// 1. check soldiers amount
		// 2. check map border
		// 3. check if grid can conquer

		// return result
	}

	public boolean finished() {
		boolean r = false, b = false;
		for (var grid : gameMap.grid) {
			if (grid.type.equals("R")) r = true;
			if (grid.type.equals("B")) b = true;
		}
		return r && b;
	}

	public GameResult getResult() {
		if (!finished()) throw new IllegalStateException();
		// TODO
	}

}
