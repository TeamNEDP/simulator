package websocket.message;

public class GameUpdateMessage extends Message<GameUpdateData> {
	public GameUpdateMessage(GameUpdateData gameUpdateData) {
		event = "gameUpdate";
		data = gameUpdateData;
	}
}
