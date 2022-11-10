package websocket.message;

public class GameStartMessage extends Message<GameStartData> {
	public GameStartMessage(GameStartData gameStartData) {
		event = "gameStart";
		data = gameStartData;
	}
}