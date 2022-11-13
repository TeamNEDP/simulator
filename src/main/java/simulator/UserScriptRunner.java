package simulator;


import com.google.gson.Gson;
import simulator.game.GameStat;
import simulator.game.MoveAction;
import simulator.game.UserScript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class UserScriptRunner {
	private ScriptEngine engine = null;
	private final ScheduledExecutorService service;

	private final String color;
	private final boolean noop;


	public UserScriptRunner(String color, UserScript script, ScheduledExecutorService service) {
		this.color = color;
		this.service = service;
		if (script.type.equals("javascript")) {
			this.engine = new ScriptEngineManager().getEngineByName("nashorn");
			noop = !Boolean.TRUE.equals(executeWithTimeout(() -> {
				engine.eval(script.content);
				return true;
			}, 500, service));
		} else {
			noop = true;
		}
	}

	public MoveAction run(GameStat stat) {
		if (noop) {
			return null;
		}
		return executeWithTimeout(() -> {
					var res = engine.eval("Tick(\"" + color + "\", " + new Gson().toJson(stat) + ");");
					if (res == null) return null;
					return MoveAction.fromObject(res);
				}, 500, service
		);
	}

	private static <T> T executeWithTimeout(Callable<T> callable, @SuppressWarnings("SameParameterValue") long timeout, ExecutorService service) {

		var future = service.submit(callable);
		var start = System.currentTimeMillis();
		while (!future.isDone()) {
			if (System.currentTimeMillis() - start > timeout) {
				future.cancel(true);
				return null;
			}
			try {
				//noinspection BusyWait
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

}
