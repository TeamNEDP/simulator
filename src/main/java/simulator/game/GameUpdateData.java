package simulator.game;

public class GameUpdateData {
    public String id;
    public GameTick tick;
    public GameUpdateData(String id_1,GameTick tick_1)
    {
        id=id_1;
        tick=tick_1;
    }
}
