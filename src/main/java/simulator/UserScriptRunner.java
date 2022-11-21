package simulator;


import com.eclipsesource.v8.V8;
import com.google.gson.Gson;
import simulator.game.GameStat;
import simulator.game.GameTick;
import simulator.game.MoveAction;
import simulator.game.UserScript;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static simulator.GameProcess.TIME_LIMIT;

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
				runtime.executeScript(script.content);
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
		var currentThread = Thread.currentThread();

		service.schedule(() -> {
			if (!done.get()) {
				currentThread.interrupt();
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

	private static <T> T executeWithTimeout(Callable<T> callable, @SuppressWarnings("SameParameterValue") long timeout, ScheduledExecutorService service) {
		return executeWithTimeout(callable, () -> {
		}, timeout, service);
	}

}
