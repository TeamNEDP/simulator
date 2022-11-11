package simulator.game;

import simulator.game.*;

public class MapGrid {
	public String type;
	public int soldiers;


	public boolean isCrownOrCastle()
	{
		return type.equals("R") || type.equals("B") || type.equals("CR") || type.equals("CB");
	}

	public boolean isLand()
	{
		return !(type.equals("C") || type.equals("M") || type.equals("V") || type.equals("F") || type.equals("MF"));
	}

	/**
	 * description
	 *
	 * @param user     who wants to conquer
	 * @param soldiers how many soldiers that is moving
	 * @return whether given conquer can be performed
	 */
	public boolean canConquer(String user, int soldiers) {
		// TODO
	}
}
