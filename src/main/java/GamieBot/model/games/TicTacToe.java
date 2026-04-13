package GamieBot.model.games;

public class TicTacToe implements IGame {

    @Override
    public void initGame() {
        state = "=======\n| | | |\n=======\n| | | |\n=======\n| | | |\n=======".toCharArray();
        player1 = 0;
        player2 = 1;
        movingPlayer = player1;
        isEnd = false;
    }

    /*
     * =======
     * | | | |
     * =======
     * | | | |
     * =======
     * | | | |
     * =======
     */

    // Moves format: 3X3 or 3 3

    private boolean isNormDig(char ch) {
        return ('0' <= ch && ch <= '3');
    }

    private boolean isNormSep(char ch) {
        return (ch == 'X' || ch == 'x' || ch == ' ');
    }

    private boolean checkForm(String action) {
        if (action.length() != 3)
            return false;
        else if (!isNormDig(action.charAt(0)) || !isNormDig(action.charAt(2)))
            return false;
        return (isNormSep(action.charAt(1)));
    }

    private char crd(int x, int y) {
        return state[(x * 2 + 1) * 8 + y * 2 + 1];
    }

    private void setWinner(String res) {
        state = ("The Game is end. The winner: " + res).toCharArray();
    }

    private boolean checkGlavDiag() {
        if (crd(0, 0) == crd(1, 1) && crd(1, 1) == crd(2, 2)) {
            if (crd(0, 0) == 'X') {
                setWinner("First");
                isEnd = true;
                return true;
            }
            if (crd(0, 0) == 'O') {
                setWinner("Second");
                isEnd = true;
                return true;
            }
        }
        return false;
    }

    private boolean checkPobDiag() {
        if (crd(0, 2) == crd(1, 1) && crd(1, 1) == crd(2, 0)) {
            if (crd(1, 1) == 'X') {
                setWinner("First");
                isEnd = true;
                return true;
            }
            if (crd(1, 1) == 'O') {
                setWinner("Second");
                isEnd = true;
                return true;
            }
        }
        return false;
    }

    private boolean checkGor(int x) {
        if (crd(x, 0) == crd(x, 1) && crd(x, 1) == crd(x, 2)) {
            if (crd(x, 0) == 'X') {
                setWinner("First");
                isEnd = true;
                return true;
            }
            if (crd(x, 0) == 'O') {
                setWinner("Second");
                isEnd = true;
                return true;
            }
        }
        return false;
    }

    private boolean checkHor(int y) {
        if (crd(0, y) == crd(1, y) && crd(1, y) == crd(2, y)) {
            if (crd(0, y) == 'X') {
                setWinner("First");
                isEnd = true;
                return true;
            }
            if (crd(0, y) == 'O') {
                setWinner("Second");
                isEnd = true;
                return true;
            }
        }
        return false;
    }

    private void isGameEnd() {
        if (checkGlavDiag())
            return;
        if (checkPobDiag())
            return;
        for (int i = 0; i < 3; i++) {
            if (checkGor(i)) {
                return;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (checkHor(i)) {
                return;
            }
        }
    }

    @Override
    public boolean checkMove(int playerNum, String action) {
        if (isEnd)
            return false;
        else if (playerNum != movingPlayer) {
            return false;
        } else if (!checkForm(action)) {
            return false;
        } else {
            int x = action.charAt(0) - '0';
            int y = action.charAt(2) - '0';
            return (crd(x, y) == ' ');
        }
    }

    @Override
    public void makeMove(int playerNum, String action) {
        if (movingPlayer == playerNum) {
            if (playerNum == player1) {
                int x = action.charAt(0) - '0';
                int y = action.charAt(2) - '0';
                state[(x * 2 + 1) * 8 + y * 2 + 1] = 'X';
                movingPlayer = player2;
            } else if (playerNum == player2) {
                int x = action.charAt(0) - '0';
                int y = action.charAt(2) - '0';
                state[(x * 2 + 1) * 8 + y * 2 + 1] = 'O';
                movingPlayer = player1;
            }
        }
        isGameEnd();
    }

    @Override
    public boolean isFinished() {
        return isEnd;
    }

    @Override
    public String getInfoForPlayer(int playerNum) {
        String ans = "";
        for (int i = 0; i < state.length; i++) {
            ans += state[i];
        }
        return ans;
    }

    private char[] state;
    private int player1, player2;
    private int movingPlayer;
    private boolean isEnd;
}
