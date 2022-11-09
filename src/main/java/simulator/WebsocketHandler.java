package simulator;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebsocketHandler extends WebSocketClient {
	ExecutorService service = Executors.newFixedThreadPool(100);
	String authMessage;

	WebsocketHandler(URI url, String str) {
		super(url);
		this.authMessage = str;
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		send(authMessage);
	}

	@Override
	public void onMessage(String message) {
		var gameSetting = new Gson().fromJson(message, GameSetting.class);
		service.submit(new GameProcess(gameSetting, service));
	}

	public synchronized void sendGameTick(GameTick gameTick) {
		try {
			send(new Gson().toJson(gameTick));
		} catch (Exception ex) {
			System.out.println("Error on open web socket");
		}
	}

	public synchronized void sendGameResult(GameResult gameResult) {
		try {
			send(new Gson().toJson(gameResult));
		} catch (Exception ex) {
			System.out.println("Error on open web socket");
		}
	}


	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("连接关闭：" + reason);
	}

	@Override
	public void onError(Exception ex) {
		System.out.println("错误：" + ex);
	}
}