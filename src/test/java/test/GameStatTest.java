package test;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import team.nedp.valyria.simulator.schema.GameMap;
import team.nedp.valyria.simulator.schema.GameStat;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameStatTest {

	static final String js = "{\"width\":5,\"height\":5,\"grids\":[{\"type\":\"R\",\"soldiers\":10},{\"type\":\"M\"},{\"type\":\"C\",\"soldiers\":11},{\"type\":\"V\"},{\"type\":\"CR\",\"soldiers\":11},{\"type\":\"V\"},{\"type\":\"V\"},{\"type\":\"V\"},{\"type\":\"V\"},{\"type\":\"V\"},{\"type\":\"V\"},{\"type\":\"C\",\"soldiers\":33},{\"type\":\"M\"},{\"type\":\"V\"},{\"type\":\"V\"},{\"type\":\"C\",\"soldiers\":22},{\"type\":\"M\"},{\"type\":\"V\"},{\"type\":\"V\"},{\"type\":\"V\"},{\"type\":\"C\",\"soldiers\":29},{\"type\":\"C\",\"soldiers\":13},{\"type\":\"V\"},{\"type\":\"V\"},{\"type\":\"B\",\"soldiers\":0}]}";

	@Test
	public void testFromGameMap() {
		GameMap gameMap = new Gson().fromJson(js, GameMap.class);
		GameStat gameStat = GameStat.fromGameMap("R", gameMap);

		assertEquals(gameStat.map.grids[2].type, "MF");
	}

}
