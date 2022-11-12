package simulator.game;

import simulator.GameMapState;

public class GameStat {
	public GameMap map;
	public int enemy_soldiers;
	public int enemy_lands;

	public GameStat(GameMap map)
	{
		this.map=map;
		enemy_soldiers=0;
		enemy_lands=0;
	}

	public  GameStat fromGameMap(String user, GameMapState map) {
		if(user.equals("R"))user="B";
		else user="R";
		GameStat gamestat=new GameStat(map.gameMap);
		gamestat.map.change(user);
		for(int i = 0; i < this.map.height * this.map.width; i++ )
		{
			if(this.map.grid[i].isBelongTo(user))
			{
				gamestat.enemy_lands++;
				gamestat.enemy_soldiers+=this.map.grid[i].soldiers;
			}
		}
		return gamestat;
	}
}