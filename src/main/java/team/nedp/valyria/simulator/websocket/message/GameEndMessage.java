package team.nedp.valyria.simulator.websocket.message;

import team.nedp.valyria.simulator.schema.GameEndData;

public class GameEndMessage extends Message<GameEndData> {
    public GameEndMessage(GameEndData gameEndData) {
        event = "gameEnd";
        data = gameEndData;
    }

}
