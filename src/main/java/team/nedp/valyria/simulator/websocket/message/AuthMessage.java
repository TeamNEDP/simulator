package team.nedp.valyria.simulator.websocket.message;

public class AuthMessage extends Message<AuthData> {
    public AuthMessage(AuthData authData) {
        event = "auth";
        data = authData;
    }
}
