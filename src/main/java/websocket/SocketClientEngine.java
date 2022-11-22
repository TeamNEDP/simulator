package websocket;

import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("BusyWait")
public class SocketClientEngine {

	public static void main(String[] args) throws URISyntaxException, InterruptedException { 
		var url = System.getenv("SERVER_URL");

		// token 发过去
		var secret = System.getenv("SERVER_SECRET");

		WebsocketHandler client;

		do {
			client = new WebsocketHandler(new URI(url), secret);
			client.connectBlocking();
			System.out.println("Cannot connect. Retrying after 10 seconds.");
			Thread.sleep(10000);
		} while (!client.getReadyState().equals(ReadyState.OPEN));

		System.out.println("Connected to websocket.");

		while (client.getReadyState().equals(ReadyState.OPEN)) {
			if (client.getReadyState().equals(ReadyState.OPEN)) {
				client.sendPing();
				Thread.sleep(10000);
			} else if (client.getReadyState().equals(ReadyState.CLOSED)) {
				do {
					client = new WebsocketHandler(new URI(url), secret);
					client.connectBlocking();
					System.out.println("Cannot connect. Retrying after 10 seconds.");
					Thread.sleep(10000);
				} while (!client.getReadyState().equals(ReadyState.OPEN));
			}
		}
	}
}
