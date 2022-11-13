package simulator.game;

import simulator.GameMapState;

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

	public static GameStat fromGameMap(String user, GameMapState map) {
		final var opponent = user.equals("R") ? "B" : "R";

		GameStat gamestat = new GameStat(map.gameMap.copy());
		gamestat.map.change(user);

		Arrays.stream(gamestat.map.grids).filter(g -> g.isBelongTo(opponent)).forEach(g -> {
			gamestat.enemy_lands++;
			gamestat.enemy_soldiers += g.soldiers;
		});

		return gamestat;
	}
}