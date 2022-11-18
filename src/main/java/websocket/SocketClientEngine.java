package websocket;

import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketClientEngine {

	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		System.loadLibrary("libquickjs-java-wrapper.so");

		var url = System.getenv("SERVER_URL");

		// token 发过去
		var secret = System.getenv("SERVER_SECRET");

		WebsocketHandler client = new WebsocketHandler(new URI(url), secret);
		client.connectBlocking();

		while (!client.getReadyState().equals(ReadyState.OPEN)) {
			System.out.println("Cannot connect. Retrying after 10 seconds.");
			Thread.sleep(10000);
			client.close();
			client.connectBlocking();
		}

		System.out.println("Connected to websocket.");

		while (client.getReadyState().equals(ReadyState.OPEN)) {
			client.sendPing();
			Thread.sleep(10000);
		}
	}
}
