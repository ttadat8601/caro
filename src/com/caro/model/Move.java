package com.caro.model;

import java.io.Serializable;

/**
 * Lưu trữ thông tin một nước đi trên bàn cờ.
 */
public class Move implements Serializable {
    private static final long serialVersionUID = 1L;

    private int row;
    private int col;
    private char player;
    private long timestamp;

    public Move(int row, int col, char player) {
        this.row = row;
        this.col = col;
        this.player = player;
        this.timestamp = System.currentTimeMillis();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getPlayer() {
        return player;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%c tại [%d, %d]", player, row, col);
    }
}
