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

	public int Attention_x()
	{
		if(movement=='L') return x-1;
		else if(movement=='R') return x+1;
		else return x;
	}

	public int Attention_y()
	{
		if(movement=='U') return y+1;
		else if(movement=='D') return y-1;
		else return y;
	}
}
