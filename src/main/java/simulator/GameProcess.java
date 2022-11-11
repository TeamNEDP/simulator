package simulator;

import simulator.game.GameSetting;
import simulator.game.GameStartData;
import websocket.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class GameProcess implements Runnable {

    GameStartData data;
    GameStateMachine machine;
    ScheduledExecutorService service;
    WebsocketHandler handler;
    public GameProcess(GameStartData data,ScheduledExecutorService service,WebsocketHandler handler) {
        // input game state
        this.data =data;
        this.service=service;
        this.handler =handler;
        machine=new GameStateMachine(data, service, handler);
    }

    @Override
    public void run() {
        for (; ; ) {
            var start = System.currentTimeMillis();
            if(machine.tick()) break;
            var took = System.currentTimeMillis() - start;
			Thread.sleep(500 - took);
        }
    }
}
