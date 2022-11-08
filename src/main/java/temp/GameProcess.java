package temp;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameProcess implements Runnable {

	GameState state;

	public GameProcess() {
		// input game state
	}

	@Override
	public void run() {
		for (; ; ) {
			servie
			final var thread = Thread.currentThread();
			final var done = new AtomicBoolean(false);
			executor.schedule(() -> {
				if (!done.get()) {
					thread.interrupt();
				}
			}, 500, TimeUnit.MILLISECONDS);
			var start = System.currentTimeMillis();
			try {
				if (state.tick()) {
					break;
				}
			} catch (InterruptedException ex) {
				state.sendGameTick();
			}
			done.set(true);


			// 固定间隔
			var took = System.currentTimeMillis() - start;

			Thread.sleep(500 - took);
		}
	}
}
