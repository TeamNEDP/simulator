package simulator;

import simulator.game.*;

public class GameMapState {
	public GameMap gameMap;

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

	public GameTick applyMoveAction(int time,String user, MoveAction movement) {
		// TODO
		GameTick tick=new GameTick(user,movement);
		//change to Land
		tick.update(time,gameMap);
		//-1.check the grid's owner
		if(!gameMap.grid[gameMap.get_pos(movement.x, movement.y)].is_belongto(user))
			tick.action_valid=false;
		//0.check map border
		if(!gameMap.check_border(movement.x,movement.y))
			tick.action_valid=false;
		// 1. check soldiers amount
		if(!gameMap.grid[gameMap.get_pos(movement.x, movement.y)].checkAmount(movement.amount))
			tick.action_valid=false;
		// 2. check map border
		if(!gameMap.check_border(movement.Attention_x(),movement.Attention_y()))
			tick.action_valid=false;
		// 3. check if grid can conquer
		if(!gameMap.grid[gameMap.get_pos(movement.x, movement.y)].canConquer())
			tick.action_valid=false;
		if(!tick.action_valid) return tick;
		gameMap.grid[gameMap.get_pos(movement.x, movement.y)].kill(movement.amount);
		tick.add_change(gameMap.grid[gameMap.get_pos(movement.x, movement.y)]);
		if(gameMap.grid[gameMap.get_pos(movement.Attention_x(), movement.Attention_y())].is_belongto(user))
		{
			gameMap.grid[gameMap.get_pos(movement.Attention_x(), movement.Attention_y())].kill(-movement.amount);
		}
		else gameMap.grid[gameMap.get_pos(movement.Attention_x(), movement.Attention_y())].conquer(user,movement.amount);
		// return result
		tick.add_change(gameMap.grid[gameMap.get_pos(movement.Attention_x(), movement.Attention_y())]);
		return tick;
	}

	public boolean finished() {
		boolean r = false, b = false;
		for (var grid : gameMap.grid) {
			if (grid.type.equals("R")) r = true;
			if (grid.type.equals("B")) b = true;
		}
		return r && b;
	}

	public GameResult getResult(int time) {
		if (!finished()) throw new IllegalStateException();
		// TODO
		GameResult result=new GameResult(time);
		for (var grid : gameMap.grid) {
			if (grid.type.equals("R")) result.winner="R";
			if (grid.type.equals("B")) result.winner="B";
		}
		
		for (int i = 0; i < gameMap.height * gameMap.width; i++) {
			if (gameMap.grid[i].is_belongto("R")) {
				result.r_stat.grids_taken++;
			}
			else result.b_stat.grids_taken++;
		}
		return result;
	}

}
