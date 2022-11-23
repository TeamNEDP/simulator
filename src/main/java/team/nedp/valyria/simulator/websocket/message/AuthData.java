package team.nedp.valyria.simulator.websocket.message;

public class AuthData {
    int slots;
    String token;

    public AuthData(int sl, String to) {
        slots = sl;
        token = to;
    }

}
