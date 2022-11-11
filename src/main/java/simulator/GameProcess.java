package simulator;

import simulator.GameStateMachine;
import simulator.game.GameSetting;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameProcess implements Runnable {

    GameSetting game;
    GameStateMachine machine;
    ExecutorService service;
    public GameProcess(GameSetting game_1,ExecutorService service_1) {
        // input game state
        game=game_1;
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
