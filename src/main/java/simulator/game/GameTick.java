package simulator.game;

import java.util.Arrays;

import simulator.game.*;

public class GameTick {
    public MapGrid[] changes;
    public String operator;
    public MoveAction action;
    public boolean action_valid;
    
    public GameTick(String user, MoveAction movement)
    {
        operator=user;
        action=movement;
        action_valid=true;
        changes=new MapGrid[1];
    }

    public void add_change(MapGrid grid)
    {
        changes[changes.length-1]=grid;
        changes= Arrays.copyOf(changes,changes.length+1);
    }

    public void update(int time,GameMap gameMap)
    {
        if(time%2==0)
		{
			for(int i = 0; i < gameMap.height * gameMap.width; i++)
				if(gameMap.grid[i].isCrownOrCastle())
					add_change(gameMap.grid[i]);
		}
		if(time%50==0)
		{
			for(int i = 0; i < gameMap.height * gameMap.width; i++)
				if(gameMap.grid[i].isLand())
					add_change(gameMap.grid[i]);
		}
    }
}
