package team.nedp.valyria.simulator.websocket.message;

import team.nedp.valyria.simulator.schema.GameUpdateData;

public class GameUpdateMessage extends Message<GameUpdateData> {
    public GameUpdateMessage(GameUpdateData gameUpdateData) {
        event = "gameUpdate";
        data = gameUpdateData;
    }
}
