package simulator;

import simulator.game.GameMap;
import simulator.game.GameTick;
import simulator.game.Movement;

public class GameMapState {
	GameMap gameMap;
	public GameMapState(GameMap gameMap)
	{
		this.gameMap = gameMap;
	}

	public GameTick applyMovement(String user, Movement movement) {
		// TODO

		// 1. check soldiers amount
		// 2. check map border
		// 3. check if grid can conquer

		// return result
	}

	public boolean finished() {

	}

}
