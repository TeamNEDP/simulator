package team.nedp.valyria.simulator.websocket.message;

import team.nedp.valyria.simulator.schema.GameStartData;

public class GameStartMessage extends Message<GameStartData> {
    public GameStartMessage(GameStartData gameStartData) {
        event = "gameStart";
        data = gameStartData;
    }
}