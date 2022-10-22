public interface GameTick {
    GridChange[] changes();
    Operator operator();
    GameAction action();
    boolean action_valid();
}
