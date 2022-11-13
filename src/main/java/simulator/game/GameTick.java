package simulator.game;

import java.util.ArrayList;
import java.util.List;

public class GameTick {
	public List<GridChange> changes;
	public String operator;
	public MoveAction action;
	public boolean action_valid;

	public GameTick(String user, MoveAction movement) {
		operator = user;
		action = movement;
		action_valid = true;
		changes = new ArrayList<>();
	}

	public void addChange(MapGrid grid, int x, int y) {
		var change = new GridChange();
		change.grid = grid;
		change.x = x;
		change.y = y;
		changes.add(change);
	}
}
