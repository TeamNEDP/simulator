package websocket;

import org.java_websocket.WebSocket;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketClientEngine {

	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		var url = System.getenv("SERVER_URL");

		// token 发过去
		var secret = System.getenv("SERVER_SECRET");

		WebsocketHandler client = new WebsocketHandler(new URI(url), secret);
		client.connect();
		while (!client.getReadyState().equals(WebSocket.readyState.OPEN)) {
			System.out.println("还没有打开");
			Thread.sleep(500);
		}
		System.out.println("建立 websocket 连接");
	}
}
