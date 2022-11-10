package simulator;

import simulator.GameStateMachine;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameProcess implements Runnable {

    GameStateMachine state;
    ExecutorService service;
    public GameProcess(GameStateMachine state_1,ExecutorService service_1) {
        // input game state
        state=state_1;
        service=service_1;
    }

    @Override
    public void run() {
        for (; ; ) {
            if(GameStateMachine.tick(service)) break;

        }
    }
}
