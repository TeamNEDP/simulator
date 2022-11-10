package websocket.message;

public class GameEndMessage extends Message<GameEndData> {
	public GameEndMessage(GameEndData gameEndData) {
		event = "gameEnd";
		data = gameEndData;
	}

}
