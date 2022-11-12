package simulator.game;

import java.util.Arrays;

public class GameTick {
	public MapGrid[] changes;
	public String operator;
	public MoveAction action;
	public boolean action_valid;

	public GameTick(String user, MoveAction movement) {
		operator = user;
		action = movement;
		action_valid = true;
		changes = new MapGrid[1];
	}

	public void addChange(MapGrid grid) {
		changes[changes.length - 1] = grid;
		changes = Arrays.copyOf(changes, changes.length + 1);
	}
}
