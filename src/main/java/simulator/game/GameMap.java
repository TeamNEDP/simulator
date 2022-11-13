package simulator.game;

import java.util.Arrays;

public class GameMap {
	public int width;
	public int height;
	public MapGrid[] grids;

	public GameMap copy() {

		var gameMap = new GameMap();

		gameMap.width = this.width;
		gameMap.height = this.height;

		gameMap.grids = Arrays.stream(grids).map(MapGrid::copy).toArray(MapGrid[]::new);
		return gameMap;
	}

	public boolean checkBorder(int x, int y) {
		return (width > x && height > y && x >= 0 && y >= 0);
	}

	public int get_pos(int x, int y) {
		return x * height + y;
	}

	public void change(String user) {
		for (int i = 0; i < height * width; i++) {
			int x = i % width;
			int y = i / width;
			int[] dx = {-1, -1, -1, 1, 1, 1, 0, 0, 0};
			int[] dy = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
			boolean flag = false;
			for (int j = 0; j < 9; j++) {
				if (!checkBorder(x + dx[j], y + dy[j])) continue;
				if (grids[get_pos(x + dx[j], y + dy[j])].isBelongTo(user))
					flag = true;
			}
			grids[get_pos(x, y)].change(user, flag);
		}
	}
}
