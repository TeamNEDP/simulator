package simulator;

import org.java_websocket.WebSocket;

public class Main {

	public static void main(String[] args) {
		var url = System.getenv("SERVER_URL");

		// token 发过去
		var secret = System.getenv("SERVER_SECRET");

		WebsocketHandler client = new WebsocketHandler(url);
		client.connect();
		while (!client.getReadyState().equals(WebSocket.readyState.OPEN)) {
			System.out.println("还没有打开");
		}
		System.out.println("建立 websocket 连接");
	}
}
