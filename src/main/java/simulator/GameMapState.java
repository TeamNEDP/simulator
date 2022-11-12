package simulator;

import simulator.game.*;

public class GameMapState {
	public GameMap gameMap;
	private final GameResult result;
	private final GameTick tick;

	public GameMapState(GameMap gameMap) {
		this.gameMap = gameMap;
		result = new GameResult(0);
		tick = new GameTick(null, null);
	}

	public void incSoldierPerTick() {
		for (int i = 0; i < gameMap.height * gameMap.width; i++) {
			if (gameMap.grid[i].isCrownOrCastle()) {
				gameMap.grid[i].soldiers++;
				result.updateSoldier(gameMap.grid[i].BelongTo());
				tick.addChange(gameMap.grid[i]);
			}
		}
	}

	public void incSoldierPerRound() {
		for (int i = 0; i < gameMap.height * gameMap.width; i++) {
			if (gameMap.grid[i].isLand()) {
				gameMap.grid[i].soldiers++;
				result.updateSoldier(gameMap.grid[i].BelongTo());
				tick.addChange(gameMap.grid[i]);
			}
		}
	}

	public boolean checkValid(String user, MoveAction movement) {

		// 1.check movement's validity
		if (!movement.checkValid()) return false;
		// 2.check the grid's owner
		if (!gameMap.grid[gameMap.get_pos(movement.x, movement.y)].isBelongTo(user)) return false;
		// 3.check map border
		if (!gameMap.checkBorder(movement.x, movement.y)) return false;
		// 4. check soldiers amount
		if (!gameMap.grid[gameMap.get_pos(movement.x, movement.y)].checkAmount(movement.amount)) return false;
		// 5. check map border
		if (!gameMap.checkBorder(movement.xAttention(), movement.yAttention())) return false;
		// 6. check if grid can conquer
		return gameMap.grid[gameMap.get_pos(movement.x, movement.y)].canConquer();
	}


	public GameTick applyMoveAction(String user, MoveAction movement) {
		tick.operator = user; tick.action = movement;

		if (!checkValid(user, movement)) {
			tick.action_valid = false;
			return tick;
		}

		result.updateMove(user);
		gameMap.grid[gameMap.get_pos(movement.x, movement.y)].kill(movement.amount);
		tick.addChange(gameMap.grid[gameMap.get_pos(movement.x, movement.y)]);
		if (gameMap.grid[gameMap.get_pos(movement.xAttention(), movement.yAttention())].isBelongTo(user)) {
			gameMap.grid[gameMap.get_pos(movement.xAttention(), movement.yAttention())].kill(-movement.amount);
		} else
			gameMap.grid[gameMap.get_pos(movement.xAttention(), movement.yAttention())].conquer(user, movement.amount, result);
		// return result
		tick.addChange(gameMap.grid[gameMap.get_pos(movement.xAttention(), movement.yAttention())]);
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
			if (gameMap.grid[i].isBelongTo("R")) {
				result.r_stat.grids_taken++;
			} else result.b_stat.grids_taken++;
		}
		return result;
	}

}