package simulator;

import simulator.game.*;
import websocket.WebsocketHandler;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;

public class GameStateMachine {
	private final GameMapState currentGameState;
	private final WebsocketHandler handler;

	private final UserScriptRunner rRunner, bRunner;
	private int current, time;
	private final String id;

	private static final int MAX_TICK = 500;

	public GameStateMachine(GameStartData data, ScheduledExecutorService service, WebsocketHandler handler) {
		this.id = data.id;
		currentGameState = new GameMapState(data.setting.map);
		this.handler = handler;
		this.rRunner = new UserScriptRunner("R", data.setting.users.r.script, service);
		this.bRunner = new UserScriptRunner("B", data.setting.users.b.script, service);
		this.current = new Random().nextInt(2);
	}

	private void incTick(GameTick tick) {
		time++;
		if (time % 2 == 0) {
			currentGameState.incSoldierPerTick(tick);
		}

		if (time % 50 == 0) {
			currentGameState.incSoldierPerRound(tick);
		}

		if (time % 2 == 0) {
			current = new Random().nextInt(2);
		} else {
			current ^= 1;
		}
	}

	/**
	 * @return whether the game ends
	 */
	public synchronized boolean tick() {
		GameTick tick = new GameTick();
		incTick(tick);

		// invoke user script
		MoveAction action;
		if (current == 1) {
			GameStat stat = GameStat.fromGameMap("R", currentGameState);
			action = rRunner.run(stat, tick);
		} else {
			GameStat stat = GameStat.fromGameMap("B", currentGameState);
			action = bRunner.run(stat, tick);
		}

		currentGameState.applyMoveAction(current == 1 ? "R" : "B", action, tick);

		handler.sendGameUpdateData(new GameUpdateData(id, tick));

		if (currentGameState.finished()) {
			handler.sendGameEndData(new GameEndData(id, currentGameState.getResult(time)));
			return true;
		} else if (time >= MAX_TICK) {
			handler.sendGameEndData(new GameEndData(id, currentGameState.getResultTimeOut(time)));
			return true;
		} else {
			return false;
		}
	}
}