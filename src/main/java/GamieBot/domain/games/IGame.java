package GamieBot.domain.games;

public interface IGame {
    public void initGame();

    public void makeMove(int playerNum, String action);

    public boolean checkMove(int playerNum, String action);

    public boolean checkMovingPlayer(int playerNum);

    public boolean checkMoveForm(int playerNum, String action);

    public String getInfoForPlayer(int playerNum);

    public int getMovingPlayer();

    public boolean isFinished();
}
