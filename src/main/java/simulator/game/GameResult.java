package simulator.game;

public class GameResult {
	public String winner;
	public UserGameStat r_stat;
	public UserGameStat b_stat;

	public GameResult() {
		r_stat = new UserGameStat();
		b_stat = new UserGameStat();
	}

	public void setTime(int time) {
		r_stat.rounds = time;
		b_stat.rounds = time;
	}

	public void updateMove(String user) {
		if (user.equals("R")) r_stat.moves++;
		else b_stat.moves++;
	}

	public void updateKill(String user, int amount) {
		if (user.equals("R")) r_stat.soldiers_killed += amount;
		else b_stat.soldiers_killed += amount;
	}

	public void updateSoldier(String user) {
		if (user.equals("R")) r_stat.soldiers_total++;
		else b_stat.soldiers_total++;
	}
}
