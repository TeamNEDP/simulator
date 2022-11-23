package websocket;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import simulator.GameProcess;
import simulator.game.GameEndData;
import simulator.game.GameUpdateData;
import websocket.message.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

public class WebsocketHandler extends WebSocketClient {
	final int MAX_SLOTS = 100;
	ScheduledExecutorService service;
	AuthData authData;

	Map<String, Future<?>> games = new ConcurrentHashMap<>();

	WebsocketHandler(URI url, String str) {
		super(url);
		this.authData = new AuthData(MAX_SLOTS, str);
		service = Executors.newScheduledThreadPool(MAX_SLOTS);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("Connected to websocket.");
		sendAuthData(this.authData);
	}

	@Override
	public synchronized void onMessage(String message) {
		var gameStartData = new Gson().fromJson(message, GameStartMessage.class).data;
		System.out.println("Start simulating contest: " + gameStartData.id + ".");
		Optional.ofNullable(games.get(gameStartData.id)).ifPresent(f -> f.cancel(true));
		games.put(gameStartData.id, service.submit(new GameProcess(gameStartData, service, this)));
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("Connection closed: " + reason);
	}

	@Override
	public void onError(Exception ex) {
		System.out.println("Error: ");
		ex.printStackTrace();
	}

	public synchronized void sendAuthData(AuthData authData) {
		try {
			send(new Gson().toJson(new AuthMessage(authData)));
		} catch (Exception ex) {
			System.out.println("Error on open web socket.");
		}
	}

//	static int cnt = 0;

	public synchronized void sendGameUpdateData(GameUpdateData gameUpdateData) {
		try {
//			System.out.println("++cnt = " + ++cnt);
			send(new Gson().toJson(new GameUpdateMessage(gameUpdateData)));
		} catch (Exception ex) {
			System.out.println("Error on open web socket.");
		}
	}

	public synchronized void sendGameEndData(GameEndData gameEndData) {
		try {
			send(new Gson().toJson(new GameEndMessage(gameEndData)));
		} catch (Exception ex) {
			System.out.println("Error on open web socket");
		}
		System.out.println("End simulating contest: " + gameEndData.id + ".");
	}


}