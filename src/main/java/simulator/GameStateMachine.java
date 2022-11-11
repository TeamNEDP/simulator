package simulator;

import com.google.gson.Gson;
import simulator.game.*;
import websocket.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Arrays;
import java.util.Random;

public class GameStateMachine {
	private final GameMapState currentGameState;
	private final ScheduledExecutorService service;
	private final WebsocketHandler handler;

	private UserScriptRunner rRunner, bRunner;
	private int current;
	private String id;

	// private ...

	// current player

	// script

	public GameStateMachine(GameStartData data, ScheduledExecutorService service, WebsocketHandler handler) {
		this.id = data.id;
		currentGameState = new GameMapState(data.setting.map);
		this.handler = handler;
		this.service = service;
		this.rRunner = new UserScriptRunner(data.setting.r.script, service);
		this.bRunner = new UserScriptRunner(data.setting.b.script, service);
		this.current = new Random().nextInt(2);
	}

	/**
	 * @return whether the game ends
	 */
	public boolean tick() {
		// invoke user script
		Movement action = null;
		if (current == 1) {
			GameStat stat = GameStat.fromGameMap("r", currentGameState);
			action = rRunner.run(stat);
		} else {
			GameStat stat = GameStat.fromGameMap("b", currentGameState);
			action = bRunner.run(stat);
		}

		var tick = currentGameState.applyMovement(current == 1 ? "r" : "b", action);

		handler.sendGameUpdateData(new GameUpdateData(id, tick));

		current ^= 1;

		return currentGameState.finished();
	}
}
