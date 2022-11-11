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

	public GameStateMachine(GameStartData data, ScheduledExecutorService service, WebsocketHandler handler) {
		this.id = data.id;
		currentGameState = new GameMapState(data.setting.map);
		this.handler = handler;
		this.rRunner = new UserScriptRunner(data.setting.r.script, service);
		this.bRunner = new UserScriptRunner(data.setting.b.script, service);
		this.current = new Random().nextInt(2);
	}

	private void incTick() {
		time++;
		if (time % 2 == 0) {
			currentGameState.incSoldierPerTick();
		}

		if (time % 50 == 0) {
			currentGameState.incSoldierPerRound();
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
	public boolean tick() {

		incTick();

		// invoke user script
		MoveAction action;
		if (current == 1) {
			GameStat stat = GameStat.fromGameMap("r", currentGameState);
			action = rRunner.run(stat);
		} else {
			GameStat stat = GameStat.fromGameMap("b", currentGameState);
			action = bRunner.run(stat);
		}

		var tick = currentGameState.applyMoveAction(current == 1 ? "r" : "b", action);

		handler.sendGameUpdateData(new GameUpdateData(id, tick));

		if (currentGameState.finished()) {
			handler.sendGameEndData(new GameEndData(id, currentGameState.getResult()));
			return true;
		} else {
			return false;
		}
	}
}