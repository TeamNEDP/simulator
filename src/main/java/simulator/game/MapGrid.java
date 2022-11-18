package simulator.game;

public class MapGrid {
	public String type;
	public int soldiers;

	public MapGrid copy() {
		var res = new MapGrid();
		res.type = type;
		res.soldiers = soldiers;
		return res;
	}


	public boolean isCrownOrCastle() {
		return type.equals("R") || type.equals("B") || type.equals("CR") || type.equals("CB");
	}

	/**
	 * @return whether it is a Land (vacancy exclusive).
	 */
	public boolean isLand() {
		return type.equals("LR") || type.equals("LB");
	}

	public boolean checkAmount(int num) {
		return 0 < num && num < soldiers;
	}

	public void kill(int num) {
		soldiers -= num;
	}

	public boolean isBelongTo(String user) {
		if (user.equals("R")) {
			return type.equals("R") || type.equals("LR") || type.equals("CR");
		} else {
			return type.equals("B") || type.equals("LB") || type.equals("CB");
		}
	}

	public String belongTo() {
		if (type.equals("R") || type.equals("LR") || type.equals("CR")) return "R";
		else return "B";
	}

	public void conquer(String user, int amount, GameResult result) {
		if (soldiers < amount) {
			soldiers = amount - soldiers;
			if (isCrownOrCastle() || type.equals("C")) type = "C" + user;
			else type = "L" + user;
			result.updateKill(user, soldiers);
		} else {
			kill(amount);
			result.updateKill(user, amount);
		}
	}

	public boolean canConquer() {
		return type.equals("V") || type.equals("C") || isLand() || isCrownOrCastle();
	}

	public void change(String user, boolean flag) {
		if (isBelongTo(user) || flag) return;
		if (isCrownOrCastle()) {
			type = "MF";
			soldiers = 0;
		} else type = "F";
	}
}
