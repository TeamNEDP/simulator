package team.nedp.valyria.simulator.schema;

public class MapGrid {
    public String type;
    public int soldiers;

    public MapGrid copy() {
        var res = new MapGrid();
        res.type = type;
        res.soldiers = soldiers;
        return res;
    }


    /**
     * @return type in ("R", "B", "CR", "CB")
     */
    public boolean isOwnedCrownOrCastle() {
        return type.equals("R") || type.equals("B") || type.equals("CR") || type.equals("CB");
    }


    /**
     * @return type in ("C", "R", "B", "CR", "CB")
     */

    public boolean isCrownOrCastle() {
        return type.equals("C") || isOwnedCrownOrCastle();
    }

    /**
     * @return whether it is a Land (vacancy exclusive).
     */
    public boolean isOwnedLand() {
        return type.equals("R") || type.equals("B") || type.equals("LR") || type.equals("LB");
    }

    /**
     * @return whether it is a Land (vacancy inclusive).
     */
    public boolean isLand() {
        return type.equals("V") || isOwnedLand();
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
            if (isCrownOrCastle()) type = "C" + user;
            else type = "L" + user;
            result.updateKill(user, soldiers);
        } else {
            kill(amount);
            result.updateKill(user, amount);
        }
    }

    public boolean canConquer() {
        return isLand() || isCrownOrCastle();
    }

    public void addFogOfWar() {
        if (isCrownOrCastle() || type.equals("M")) {
            type = "MF";
        } else {
            type = "F";
        }
        soldiers = 0;
    }
}
