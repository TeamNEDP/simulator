package simulator.game;

import simulator.game.*;
import simulator.game.UserGameStat;

public class GameResult {
    public String winner;
    public UserGameStat r_stat;
    public UserGameStat b_stat;
    public GameResult(int time)
    {
        r_stat=new r_stat(time);
        b_stat=new b_stat(time);
    }
}
