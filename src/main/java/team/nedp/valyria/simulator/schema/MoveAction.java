package team.nedp.valyria.simulator.schema;

import com.google.gson.Gson;

public class MoveAction {
    public int x;
    public int y;
    public int amount;
    public char movement;

    public boolean checkValid() {
        return movement == 'U' || movement == 'D' || movement == 'L' || movement == 'R';
    }

    public static MoveAction fromJson(String json) {
        return new Gson().fromJson(json, MoveAction.class);
    }

    public int xAttention() {
        if (movement == 'L') return x - 1;
        else if (movement == 'R') return x + 1;
        else return x;
    }

    public int yAttention() {
        if (movement == 'U') return y - 1;
        else if (movement == 'D') return y + 1;
        else return y;
    }
}
