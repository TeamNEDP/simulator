package simulator;

import simulator.game.GameStartData;
import websocket.WebsocketHandler;

import java.util.concurrent.ScheduledExecutorService;

public class GameProcess implements Runnable {

	GameStartData data;
	GameStateMachine machine;
	ScheduledExecutorService service;
	WebsocketHandler handler;
	final static int TIME_LIMIT = 50;

	public GameProcess(GameStartData data, ScheduledExecutorService service, WebsocketHandler handler) {
		// input game state
		this.data = data;
		this.service = service;
		this.handler = handler;
		machine = new GameStateMachine(data, service, handler);
	}

	@Override
	public void run() {
		for (; ; ) {
			try {
				var start = System.currentTimeMillis();
				if (machine.tick()) break;
				var took = System.currentTimeMillis() - start;
				if (took < TIME_LIMIT) {
					//noinspection BusyWait
					Thread.sleep(TIME_LIMIT - took);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
