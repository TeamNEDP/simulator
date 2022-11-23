package team.nedp.valyria.simulator.schema;

import java.util.ArrayList;
import java.util.List;

public class GameTick {
    public List<GridChange> changes;
    public String operator;
    public MoveAction action;
    public boolean action_valid;

    public String action_error;

    public GameTick() {
        operator = null;
        action = null;
        action_valid = true;
        changes = new ArrayList<>();
        action_error = "";
    }

    public void addChange(MapGrid grid, int x, int y) {
        var change = new GridChange();
        change.grid = grid;
        change.x = x;
        change.y = y;
        changes.add(change);
    }
}
