package simulator.game;

import simulator.game.*;

public class GameTick {
    public MapGrid[] changes;
    public char operator;
    public GameAction action;
    public boolean action_valid;
    public void judge_operator(int num)
    {
        if(num%2==0) this.operator='R';
        else this.operator='B';
    }
}
