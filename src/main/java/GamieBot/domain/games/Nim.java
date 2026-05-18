package GamieBot.domain.games;

import java.util.Random;

public class Nim implements IGame {
    public Nim() {};

    private int crd(int x, int y) {
        if (x < 0 || x > 9 || y < 0 || y > 5) return -1;
        return (5 - y) * 10 + x;
    }

    private void setWinner(int res) {
        winnerPlayer = res;
    }

    @Override
    public void initGame() {
        state = new int[9];
        Random rnd = new Random();
        for (int i = 0; i < 9; i++) {
            state[i] = rnd.nextInt(5) + 1;
        }
        visualState = new char[60];
        for (int i = 0; i < 6; i++) {
            visualState[crd(9, i)] = '\n';
        }
        player1 = 0;
        player2 = 1;
        winnerPlayer = -1;
        movingPlayer = player1;
        isEnd = false;
    }   

    private void makeVisual() {
        if (isEnd) return;
        for (int i = 0; i < 9; i++) {
            for (int j = 1; j <= 5; j++) {
                visualState[crd(i, j)] = ' ';
            }
            for (int j = 1; j <= state[i]; j++) {
                visualState[crd(i, j)] = '#';
            }
            visualState[crd(i, 0)] = (char)('0' + state[i]);
        }
    }

    /*
        # #  
        ###  
    #   #### 
    # ###### 
    #########
    */

    //move format: 3X3 or 3 3

    @Override
    public boolean checkMovingPlayer(int playerNum) {
        return (playerNum == movingPlayer);
    }

    private boolean isNormDig1(char ch) {
        return ('1' <= ch && ch <= '9');
    }

    private boolean isNormDig2(char ch) {
        return ('1' <= ch && ch <= '5');
    }

    private boolean isNormSep(char ch) {
        return (ch == 'X' || ch == 'x' || ch == ' ');
    }

    @Override
    public boolean checkMoveForm(int playerNum, String action) {
        if (action.length() != 3)
            return false;
        else if (!isNormDig1(action.charAt(0)) || !isNormDig2(action.charAt(2)))
            return false;
        return (isNormSep(action.charAt(1)));
    }

    @Override
    public boolean checkMove(int playerNum, String action) {
        int x = action.charAt(0) - '1';
        int y = action.charAt(2) - '0';
        return (state[x] >= y && y > 0);
    }

    private void isGameEnd() {
        for (int i = 0; i < 9; i++) {
            if (state[i] != 0) return;
        }
        if (movingPlayer == player1) {
            setWinner(1);
        } else {
            setWinner(0);
        }
        isEnd = true;
    }

    @Override
    public void makeMove(int playerNum, String action) {
        if (playerNum == movingPlayer) {
            int x = action.charAt(0) - '1';
            int y = action.charAt(2) - '0';
            state[x] -= y;
            if (movingPlayer == player1) {
                movingPlayer = player2;
            } else {
                movingPlayer = player1;
            }
        }
        isGameEnd();
    }

    @Override
    public int getMovingPlayer() {
        return movingPlayer;
    }

    @Override
    public String getInfoForPlayer(int playerNum) {
        makeVisual();
        String ans = "";
        for (int i = 0; i < visualState.length; i++) {
            ans += visualState[i];
        }
        return ans;
    }

    @Override
    public boolean isFinished() {
        return isEnd;
    }

    @Override
    public void capitulate(int playerNum) {
        if (!isEnd) {
            if (playerNum == player1) {
                setWinner(0);
            } else {
                setWinner(1);
            }
        }
        isEnd = true;
    }

    @Override
    public int getWinner() {
        return winnerPlayer;
    }
    
    private int[] state;
    private char[] visualState;
    private int player1, player2, winnerPlayer;
    private int movingPlayer;
    private boolean isEnd;
}
