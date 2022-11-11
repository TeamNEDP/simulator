package simulator;

import simulator.GameStateMachine;
import simulator.game.GameSetting;
import simulator.game.GameStartData;
import websocket.*;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameProcess implements Runnable {

    GameStartData s_data;
    GameSetting game;
    GameStateMachine machine;
    ExecutorService service;
    WebsocketHandler handler;
    public GameProcess(GameStartData game_1,ExecutorService service_1,WebsocketHandler handler_1) {
        // input game state
        s_data=game_1;
        game=s_data.setting;
        service=service_1;
        machine=new GameStateMachine(s_data,handler);
        handler=handler_1;
    }

    @Override
    public void run() {
        for (; ; ) {
            var start = System.currentTimeMillis();
            if(machine.tick(service)) break;
            var took = System.currentTimeMillis() - start;
			Thread.sleep(500 - took);
        }
    }
}
