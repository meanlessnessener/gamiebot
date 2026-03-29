package GamieBot.model.games;

public interface IGame {
    public void initGame();

    public void makeMove(int playerNum, String action);

    public boolean checkMove(int playerNum, String action);

    public String getInfoForPlayer(int playerNum);
}
