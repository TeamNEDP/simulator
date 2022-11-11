package websocket.message;

import simulator.game.GameStartData;

public class GameStartMessage extends Message<GameStartData> {
	public GameStartMessage(GameStartData gameStartData) {
		event = "gameStart";
		data = gameStartData;
	}
}