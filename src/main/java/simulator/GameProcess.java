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
    WebsocketHandler handler_1;
    public GameProcess(GameSetting game_1,ExecutorService service_1,WebsocketHandler handler_1) {
        // input game state
        s_data=game_1;
        game=s_data;
        service=service_1;
        machine=new GameStateMachine(game);
    }

    @Override
    public void run() {
        for (; ; ) {
            if(machine.tick(service)) break;

        }
    }
}
