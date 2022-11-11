package simulator.game;

import com.google.gson.Gson;

public class MoveAction {
	public int x;
	public int y;
	public int amount;
	public char movement;

	public static MoveAction fromObject(Object obj) {
		return new Gson().fromJson(new Gson().toJson(obj), MoveAction.class);
	}


}
