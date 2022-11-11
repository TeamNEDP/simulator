package websocket.message;

import simulator.game.GameEndData;

public class GameEndMessage extends Message<GameEndData> {
	public GameEndMessage(GameEndData gameEndData) {
		event = "gameEnd";
		data = gameEndData;
	}

}
