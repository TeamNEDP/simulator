package team.nedp.valyria.simulator.schema;

public class GameSetting {

    public GameMap map;

    public Users users;

    public static class Users {
        public GameUser r, b;
    }

}
