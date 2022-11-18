package simulator;


import com.google.gson.Gson;
import com.whl.quickjs.wrapper.QuickJSContext;
import simulator.game.GameStat;
import simulator.game.GameTick;
import simulator.game.MoveAction;
import simulator.game.UserScript;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static simulator.GameProcess.TIME_LIMIT;

public class UserScriptRunner {
	private final ScheduledExecutorService service;

	private final String color;
	private final boolean noop;

	private final QuickJSContext context = QuickJSContext.create();


	public UserScriptRunner(String color, UserScript script, ScheduledExecutorService service) {
		this.color = color;
		this.service = service;
		if (script.type.equals("javascript")) {
			noop = !Boolean.TRUE.equals(executeWithTimeout(() -> {
				context.evaluate(script.content);
				return true;
			}, 1000, service));
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
					var res = context.evaluate("Tick(\"" + color + "\", " + new Gson().toJson(stat) + ");");
					if (res == null) {
						return null;
					}
					return MoveAction.fromObject(res);
				}, () -> tick.action_error = "time limit exceeded", TIME_LIMIT, service
		);
	}

	private static <T> T executeWithTimeout(Callable<T> callable, Runnable timeoutHandler, @SuppressWarnings("SameParameterValue") long timeout, ScheduledExecutorService service) {
		var done = new AtomicBoolean(false);
		var currentThread = Thread.currentThread();

		service.schedule(() -> {
			if (!done.get()) {
				currentThread.interrupt();
			}
		}, timeout, TimeUnit.MILLISECONDS);

		try {
			return callable.call();
		} catch (Exception e) {
			// TODO: remove me
			e.printStackTrace();
			timeoutHandler.run();
		}
		return null;
	}

	private static <T> T executeWithTimeout(Callable<T> callable, @SuppressWarnings("SameParameterValue") long timeout, ScheduledExecutorService service) {
		return executeWithTimeout(callable, () -> {
		}, timeout, service);
	}

}
