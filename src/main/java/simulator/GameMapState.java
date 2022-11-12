package simulator;

import simulator.game.*;

public class GameMapState {
	public GameMap gameMap;
	public GameResult result;

	public GameMapState(GameMap gameMap) {
		this.gameMap = gameMap;
		result = new GameResult(0);
	}

	public void incSoldierPerTick() {
		for (int i = 0; i < gameMap.height * gameMap.width; i++) {
			if (gameMap.grid[i].isCrownOrCastle()) {
				gameMap.grid[i].soldiers++;
				result.updateSoldier(gameMap.grid[i].belongto());
			}
		}
	}

	public void incSoldierPerRound() {
		for (int i = 0; i < gameMap.height * gameMap.width; i++) {
			if (gameMap.grid[i].isLand()) {
				gameMap.grid[i].soldiers++;
				result.updateSoldier(gameMap.grid[i].belongto());
			}
		}
	}

	public boolean checkValid(String user, MoveAction movement) {

		// 1.check movement's validity
		if (!movement.checkValid()) return false;
		// 2.check the grid's owner
		if (!gameMap.grid[gameMap.get_pos(movement.x, movement.y)].is_belongto(user)) return false;
		// 3.check map border
		if (!gameMap.checkBorder(movement.x, movement.y)) return false;
		// 4. check soldiers amount
		if (!gameMap.grid[gameMap.get_pos(movement.x, movement.y)].checkAmount(movement.amount)) return false;
		// 5. check map border
		if (!gameMap.checkBorder(movement.xAttention(), movement.yAttention())) return false;
		// 6. check if grid can conquer
		return gameMap.grid[gameMap.get_pos(movement.x, movement.y)].canConquer();
	}


	public GameTick applyMoveAction(int time, String user, MoveAction movement) {

		GameTick tick = new GameTick(user, movement);
		// change to Land
		tick.update(time, gameMap);

		if (!checkValid(user, movement)) {
			tick.action_valid = false;
			return tick;
		}

		result.updateMove(user);
		gameMap.grid[gameMap.get_pos(movement.x, movement.y)].kill(movement.amount);
		tick.add_change(gameMap.grid[gameMap.get_pos(movement.x, movement.y)]);
		if (gameMap.grid[gameMap.get_pos(movement.xAttention(), movement.yAttention())].is_belongto(user)) {
			gameMap.grid[gameMap.get_pos(movement.xAttention(), movement.yAttention())].kill(-movement.amount);
		} else
			gameMap.grid[gameMap.get_pos(movement.xAttention(), movement.yAttention())].conquer(user, movement.amount, result);
		// return result
		tick.add_change(gameMap.grid[gameMap.get_pos(movement.xAttention(), movement.yAttention())]);
		return tick;
	}

	public boolean finished() {
		boolean r = false, b = false;
		for (var grid : gameMap.grid) {
			if (grid.type.equals("R")) r = true;
			if (grid.type.equals("B")) b = true;
		}
		return !(r && b);
	}

	public GameResult getResult(int time) {
		if (!finished()) throw new IllegalStateException();
		result.setTime(time);
		for (var grid : gameMap.grid) {
			if (grid.type.equals("R")) result.winner = "R";
			if (grid.type.equals("B")) result.winner = "B";
		}
		for (int i = 0; i < gameMap.height * gameMap.width; i++) {
			if (gameMap.grid[i].is_belongto("R")) {
				result.r_stat.grids_taken++;
			} else result.b_stat.grids_taken++;
		}
		return result;
	}

}