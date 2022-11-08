package simulator;

import com.google.gson.annotations.SerializedName;

public class GameSetting {

    @SerializedName("id")
    String id;
    @SerializedName("map")
    GameMap map;

    @SerializedName("r")
    GameUser r;

    @SerializedName("b")
    GameUser b;

}
