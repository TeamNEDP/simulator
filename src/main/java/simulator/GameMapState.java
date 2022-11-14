package simulator;

import simulator.game.*;

import java.util.Arrays;

public class GameMapState {
	public GameMap gameMap;
	private final GameResult result;

	public GameMapState(GameMap gameMap) {
		this.gameMap = gameMap;
		result = new GameResult();
	}

	public void incSoldierPerTick(GameTick tick) {
		for (int i = 0; i < gameMap.height * gameMap.width; i++) {
			if (gameMap.grids[i].isCrownOrCastle()) {
				gameMap.grids[i].soldiers++;
				result.updateSoldier(gameMap.grids[i].belongTo());
				tick.addChange(gameMap.grids[i], i % gameMap.width, i / gameMap.width);
			}
		}
	}

	public void incSoldierPerRound(GameTick tick) {
		for (int i = 0; i < gameMap.height * gameMap.width; i++) {
			if (gameMap.grids[i].isLand()) {
				gameMap.grids[i].soldiers++;
				result.updateSoldier(gameMap.grids[i].belongTo());
				tick.addChange(gameMap.grids[i], i % gameMap.width, i / gameMap.width);
			}
		}
	}

	public boolean checkValid(String user, MoveAction movement) {
		if (movement == null) return false;
		// 1.check movement's validity
		if (!movement.checkValid()) return false;
		// 2.check the grid's owner
		if (!gameMap.grids[gameMap.getPos(movement.x, movement.y)].isBelongTo(user)) return false;
		// 3.check map border
		if (!gameMap.checkBorder(movement.x, movement.y)) return false;
		// 4. check soldiers amount
		if (!gameMap.grids[gameMap.getPos(movement.x, movement.y)].checkAmount(movement.amount)) return false;
		// 5. check map border
		if (!gameMap.checkBorder(movement.xAttention(), movement.yAttention())) return false;
		// 6. check if grid can conquer
		return gameMap.grids[gameMap.getPos(movement.x, movement.y)].canConquer();
	}


	public void applyMoveAction(String user, MoveAction movement, GameTick tick) {
		tick.operator = user;
		tick.action = movement;


		if (!checkValid(user, movement)) {
			tick.action_valid = false;
			return;
		}

		result.updateMove(user);
		gameMap.grids[gameMap.getPos(movement.x, movement.y)].kill(movement.amount);
		tick.addChange(gameMap.grids[gameMap.getPos(movement.x, movement.y)], movement.x, movement.y);

		if (gameMap.grids[gameMap.getPos(movement.xAttention(), movement.yAttention())].isBelongTo(user)) {
			gameMap.grids[gameMap.getPos(movement.xAttention(), movement.yAttention())].kill(-movement.amount);
		} else
			gameMap.grids[gameMap.getPos(movement.xAttention(), movement.yAttention())].conquer(user, movement.amount, result);
		// return result
		tick.addChange(gameMap.grids[gameMap.getPos(movement.xAttention(), movement.yAttention())], movement.xAttention(), movement.yAttention());
	}

	public boolean finished() {
		boolean r = false, b = false;

		for (var grid : gameMap.grids) {
			if (grid.type.equals("R")) r = true;
			if (grid.type.equals("B")) b = true;
		}

		return !(r && b);
	}


	private void initResult(int time) {
		result.setTime(time);
		result.r_stat.grids_taken = (int) Arrays.stream(gameMap.grids).filter(g -> g.isBelongTo("R")).count();
		result.b_stat.grids_taken = (int) Arrays.stream(gameMap.grids).filter(g -> g.isBelongTo("B")).count();
	}

	public GameResult getResult(int time) {
		initResult(time);

		for (var grid : gameMap.grids) {
			if (grid.type.equals("R")) result.winner = "R";
			if (grid.type.equals("B")) result.winner = "B";
		}

		return result;
	}

	public GameResult getResultTimeOut(int time) {
		initResult(time);

		if (result.r_stat.grids_taken < result.b_stat.grids_taken) {
			result.winner = "B";
		} else if (result.r_stat.grids_taken > result.b_stat.grids_taken) {
			result.winner = "R";
		} else {
			result.winner = "D";
		}

		return result;
	}

}