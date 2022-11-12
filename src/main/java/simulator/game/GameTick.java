package simulator.game;

import java.util.Arrays;

public class GameTick {
	public GridChange[] changes;
	public String operator;
	public MoveAction action;
	public boolean action_valid;

	public GameTick(String user, MoveAction movement) {
		operator = user;
		action = movement;
		action_valid = true;
		changes = new GridChange[1];
	}

	public void addChange(MapGrid grid,int x,int y) {
		changes[changes.length - 1].grid=grid;
		changes[changes.length - 1].x=x;
		changes[changes.length - 1].y=y;
		changes = Arrays.copyOf(changes, changes.length + 1);
	}
}
