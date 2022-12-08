package team.nedp.valyria.simulator;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.enums.ReadyState;
import team.nedp.valyria.simulator.websocket.WebsocketHandler;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
public class Main {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
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
                for (; ; ){
                    client = new WebsocketHandler(new URI(url), secret);
                    client.connectBlocking();
                    if (!client.getReadyState().equals(ReadyState.OPEN)) {
                        log.info("Cannot connect. Retrying after 10 seconds.");
                        Thread.sleep(10000);
                    } else {
                        log.info("Connected to websocket.");
                        break;
                    }
                }
            }
        }
    }
}