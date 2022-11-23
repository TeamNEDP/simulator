package team.nedp.valyria.simulator;

import lombok.extern.log4j.Log4j2;
import org.java_websocket.enums.ReadyState;
import team.nedp.valyria.simulator.websocket.WebsocketHandler;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
@SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
public class Main {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        log.info("test");
        var url = System.getenv("SERVER_URL");

        // token 发过去
        var secret = System.getenv("SERVER_SECRET");

        WebsocketHandler client;

        client = new WebsocketHandler(new URI(url), secret);
        client.connectBlocking();

        if (client.getReadyState().equals(ReadyState.OPEN)) log.info("Connected to websocket.");

        for (; ; ) {
            if (client.getReadyState().equals(ReadyState.OPEN)) {
                client.sendPing();
                Thread.sleep(10000);
            } else if (client.getReadyState().equals(ReadyState.CLOSED)) {
                log.info("Reconnecting...");
                do {
                    client = new WebsocketHandler(new URI(url), secret);
                    client.connectBlocking();
                    if (!client.getReadyState().equals(ReadyState.OPEN)) {
                        log.info("Cannot connect. Retrying after 10 seconds.");
                    } else {
                        log.info("Connected to websocket.");
                    }
                    Thread.sleep(10000);
                } while (!client.getReadyState().equals(ReadyState.OPEN));
            }
        }
    }
}
