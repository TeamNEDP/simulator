package team.nedp.valyria.simulator.schema;

import java.util.Arrays;

public class GameStat {
    public GameMap map;
    public int enemy_soldiers;
    public int enemy_lands;

    public GameStat(GameMap map) {
        this.map = map;
        enemy_soldiers = 0;
        enemy_lands = 0;
    }

    public static GameStat fromGameMap(String user, GameMap gameMap) {
        final var opponent = user.equals("R") ? "B" : "R";

        GameStat gamestat = new GameStat(gameMap.copy());

        Arrays.stream(gamestat.map.grids).filter(g -> g.isBelongTo(opponent)).forEach(g -> {
            gamestat.enemy_lands++;
            gamestat.enemy_soldiers += g.soldiers;
        });

        gamestat.map.addFogOfWar(user);
        return gamestat;
    }
}