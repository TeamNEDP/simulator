package websocket.message;

import simulator.game.GameSetting;

public class GameStartMessage extends Message<GameSetting> {
	public GameStartMessage(GameSetting gameStartData) {
		event = "gameStart";
		data = gameStartData;
	}
}