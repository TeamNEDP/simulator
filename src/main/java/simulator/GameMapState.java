package simulator;

import simulator.game.GameMap;
import simulator.game.GameResult;
import simulator.game.GameTick;
import simulator.game.MoveAction;

public class GameMapState {
	GameMap gameMap;

	public GameMapState(GameMap gameMap) {
		this.gameMap = gameMap;
	}

	public void incSoldier() {
		// TODO
	}

	public GameTick applyMoveAction(String user, MoveAction movement) {
		// TODO

		// 1. check soldiers amount
		// 2. check map border
		// 3. check if grid can conquer

		// return result
	}

	public boolean finished() {
		// TODO
	}

	public GameResult getResult() {
		if (!finished()) throw new IllegalStateException();
		// TODO
	}

}
