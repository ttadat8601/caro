package com.caro.model;

import java.io.Serializable;

/**
 * Lớp đại diện cho một ô trên bàn cờ Caro.
 */
public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final char EMPTY = ' ';
    public static final char PLAYER_X = 'X';
    public static final char PLAYER_O = 'O';

    private int row;
    private int col;
    private char value;
    private boolean isLastMove;
    private boolean isWinningCell;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.value = EMPTY;
        this.isLastMove = false;
        this.isWinningCell = false;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return this.value == EMPTY;
    }

    public boolean isLastMove() {
        return isLastMove;
    }

    public void setLastMove(boolean lastMove) {
        isLastMove = lastMove;
    }

    public boolean isWinningCell() {
        return isWinningCell;
    }

    public void setWinningCell(boolean winningCell) {
        isWinningCell = winningCell;
    }

    public void reset() {
        this.value = EMPTY;
        this.isLastMove = false;
        this.isWinningCell = false;
    }
}
