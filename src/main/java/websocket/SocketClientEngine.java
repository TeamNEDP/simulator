package websocket;

import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketClientEngine {

	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		var url = System.getenv("SERVER_URL");

		// token 发过去
		var secret = System.getenv("SERVER_SECRET");

		WebsocketHandler client = new WebsocketHandler(new URI(url), secret);
		client.connect();

		while (!client.getReadyState().equals(ReadyState.OPEN)) {
			System.out.println("not opened");
			Thread.sleep(500);
		}

		System.out.println("connected to websocket");

		while (client.getReadyState().equals(ReadyState.OPEN)) {
			System.out.println("Sending ping message");
			client.sendPing();
			Thread.sleep(10000);
		}
	}
}
