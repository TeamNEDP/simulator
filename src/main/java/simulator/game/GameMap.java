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

	public int getPos(int x, int y) {
		return x * height + y;
	}

	public void addFogOfWar(String user) {
		for (int i = 0; i < height * width; i++) {
			if (grids[i].isBelongTo(user)) {
				continue;
			}

			int x = i / height;
			int y = i % height;
			int[] dx = {-1, -1, -1, 1, 1, 1, 0, 0, 0};
			int[] dy = {-1, 0, 1, -1, 0, 1, -1, 0, 1};

			boolean isVisible = false;

			for (int j = 0; j < 9; j++) {
				if (!checkBorder(x + dx[j], y + dy[j])) continue;
				if (grids[getPos(x + dx[j], y + dy[j])].isBelongTo(user))
					isVisible = true;
			}

			if (isVisible) continue;

			grids[getPos(x, y)].addFogOfWar();
		}
	}
}
