package websocket.message;

import simulator.game.GameUpdateData;

public class GameUpdateMessage extends Message<GameUpdateData> {
	public GameUpdateMessage(GameUpdateData gameUpdateData) {
		event = "gameUpdate";
		data = gameUpdateData;
	}
}
