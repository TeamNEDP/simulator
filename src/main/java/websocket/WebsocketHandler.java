package websocket;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import simulator.GameProcess;
import simulator.game.GameEndData;
import simulator.game.GameUpdateData;
import websocket.message.*;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WebsocketHandler extends WebSocketClient {
	final int MAX_SLOTS = 100;
	ScheduledExecutorService service;
	AuthData authData;

	WebsocketHandler(URI url, String str) {
		super(url);
		this.authData = new AuthData(MAX_SLOTS, str);
		service = Executors.newScheduledThreadPool(MAX_SLOTS);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		sendAuthData(this.authData);
	}

	@Override
	public void onMessage(String message) {
		var gameStartData = new Gson().fromJson(message, GameStartMessage.class).data;

		service.submit(new GameProcess(gameStartData, service, this));
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("连接关闭：" + reason);
	}

	@Override
	public void onError(Exception ex) {
		System.out.println("错误：" + ex);
	}

	public synchronized void sendAuthData(AuthData authData) {
		try {
			send(new Gson().toJson(new AuthMessage(authData)));
		} catch (Exception ex) {
			System.out.println("Error on open web socket");
		}
	}

	public synchronized void sendGameUpdateData(GameUpdateData gameUpdateData) {
		try {
			send(new Gson().toJson(new GameUpdateMessage(gameUpdateData)));
		} catch (Exception ex) {
			System.out.println("Error on open web socket");
		}
	}

	public synchronized void sendGameEndData(GameEndData gameEndData) {
		try {
			send(new Gson().toJson(new GameEndMessage(gameEndData)));
		} catch (Exception ex) {
			System.out.println("Error on open web socket");
		}
	}



}