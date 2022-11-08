package simulator;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebsocketHandler extends WebSocketClient {
	ExecutorService service = Executors.newFixedThreadPool(100);

	WebsocketHandler(URI uri) {
		super(uri);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		// NO-OP
		// TODO send auth message
	}

	@Override
	public void onMessage(String message) {
		var gameSetting = new Gson().fromJson(message, GameSetting.class);
		service.submit(new GameProcess(gameSetting, service));
	}

	public synchronized void sendGameTick(GameTick gameTick)
	{
		System.out.println(new Gson().toJson(gameTick));
	}

	public synchronized void sendGameResult(GameResult gameResult)
	{
		System.out.println(new Gson().toJson(gameResult));
	}


	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("连接关闭：" + reason);
	}

	@Override
	public void onError(Exception ex) {
		System.out.println("错误：" + ex);
	}


	// Todo new class


}
