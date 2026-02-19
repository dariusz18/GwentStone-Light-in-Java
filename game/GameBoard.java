package org.poo.game;

public final class GameBoard {
    public Card[][] getBoard() {
        return board;
    }

    private static final int PATRU = 4;
    private static final int CINCI = 5;

    public void setBoard(final Card[][] board) {
        this.board = board;
    }

    private Card[][] board;

    public GameBoard() {
        this.board = new Card[PATRU][CINCI];
        for (int i = 0; i < PATRU; i++) {
            for (int j = 0; j < CINCI; j++) {
                board[i][j] = null;
            }
        }
    }

    /**
     *
     * @param row
     * @return
     */

    public boolean isRowFull(final int row) {
        for (int j = 0; j < CINCI; j++) {
            if (board[row][j] == null) {
                return false;
            }
        }
        return true;
    }

    public boolean placeCard(final Card card, final int row, final int mana) {
        if (card.getMana() > mana) {
            System.out.println("Not enough mana to place card on table.");
            return false;
        }

        if (isRowFull(row)) {
            System.out.println("Cannot place card on table since row is full.");
            return false;
        }

        for (int j = 0; j < CINCI; j++) {
            if (board[row][j] == null) {
                board[row][j] = card;
                return true;
            }
        }

        return false;
    }

}