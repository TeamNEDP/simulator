package team.nedp.valyria.simulator.game;

import team.nedp.valyria.simulator.schema.GameStartData;
import team.nedp.valyria.simulator.websocket.WebsocketHandler;

import java.util.concurrent.ScheduledExecutorService;

public class GameProcess implements Runnable {

    GameStartData data;
    GameStateMachine machine;
    ScheduledExecutorService service;
    WebsocketHandler handler;
    final static int TIME_LIMIT = 100;

    public GameProcess(GameStartData data, ScheduledExecutorService service, WebsocketHandler handler) {
        // input game state
        this.data = data;
        this.service = service;
        this.handler = handler;
    }

    @Override
    public void run() {
        machine = new GameStateMachine(data, service, handler);
        for (; ; ) {
            try {
                var start = System.currentTimeMillis();
                if (machine.tick()) break;
                var took = System.currentTimeMillis() - start;
                if (took < TIME_LIMIT) {
                    //noinspection BusyWait
                    Thread.sleep(TIME_LIMIT - took);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
