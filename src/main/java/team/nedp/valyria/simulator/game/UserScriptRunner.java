package team.nedp.valyria.simulator.game;


import com.eclipsesource.v8.V8;
import com.google.gson.Gson;
import team.nedp.valyria.simulator.schema.GameStat;
import team.nedp.valyria.simulator.schema.GameTick;
import team.nedp.valyria.simulator.schema.MoveAction;
import team.nedp.valyria.simulator.schema.UserScript;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static team.nedp.valyria.simulator.game.GameProcess.TIME_LIMIT;

public class UserScriptRunner {
    private final ScheduledExecutorService service;

    private final String color;
    private final boolean noop;

    private final V8 runtime = V8.createV8Runtime();


    public UserScriptRunner(String color, UserScript script, ScheduledExecutorService service) {
        this.color = color;
        this.service = service;
        if (script.type.equals("javascript")) {
            noop = !Boolean.TRUE.equals(executeWithTimeout(() -> {
//				context.evaluate(script.content);
                runtime.executeVoidScript(script.content);
                return true;
            }, runtime::terminateExecution, 1000, service));
        } else {
            noop = true;
        }
    }

    public MoveAction run(GameStat stat, GameTick tick) {
        if (noop) {
            tick.action_error = "noop";
            return null;
        }

        return executeWithTimeout(() -> {
            var res = runtime.executeStringScript("JSON.stringify(Tick(\"" + color + "\", " + new Gson().toJson(stat) + "))");
            if (res == null) {
                return null;
            }
            return MoveAction.fromJson(res);
        }, () -> {
            runtime.terminateExecution();
            tick.action_error = "time limit exceeded";
        }, TIME_LIMIT, service);
    }

    public void release() {
        runtime.release();
    }

    private static <T> T executeWithTimeout(Callable<T> callable, Runnable timeoutHandler, @SuppressWarnings("SameParameterValue") long timeout, ScheduledExecutorService service) {
        var done = new AtomicBoolean(false);

        service.schedule(() -> {
            if (!done.get()) {
                timeoutHandler.run();
            }
        }, timeout, TimeUnit.MILLISECONDS);

        try {
            var res = callable.call();
            done.set(true);
            return res;
        } catch (Exception e) {
            done.set(true);
        }
        return null;
    }

}
