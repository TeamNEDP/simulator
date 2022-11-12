package simulator.game;

public class MapGrid {
	public String type;
	public int soldiers;


	public boolean isCrownOrCastle() {
		return type.equals("R") || type.equals("B") || type.equals("CR") || type.equals("CB");
	}

	public boolean isLand() {
		return type.equals("LR") || type.equals("LB");
	}

	public boolean checkAmount(int num) {
		return soldiers > num;
	}

	public void kill(int num) {
		soldiers -= num;
	}

	public boolean isBelongTo(String user) {
		if (user.equals("R")) {
			return type.equals("R") || type.equals("LR") || type.equals("CR");
		} else {
			return type.equals("B") | type.equals("LB") || type.equals("CB");
		}
	}

	public String BelongTo() {
		if (type.equals("R") || type.equals("LR") || type.equals("CR")) return "R";
		else return "B";
	}

	public void conquer(String user, int amount, GameResult result) {
		if (soldiers < amount) {
			soldiers = amount - soldiers;
			if (isLand()) type = "L" + user;
			else type = "C" + user;
			result.updateKill(user, soldiers);
		} else {
			kill(amount);
			result.updateKill(user, amount);
		}
	}

	public boolean canConquer() {
		return isLand() || isCrownOrCastle();
	}
}
