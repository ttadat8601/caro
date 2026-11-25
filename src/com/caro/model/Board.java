package com.caro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp quản lý ma trận bàn cờ Caro và logic kiểm tra thắng thua.
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_SIZE = 15;
    private int size;
    private Cell[][] grid;
    private boolean blockBothEnds; // Bật/tắt luật chặn 2 đầu
    private List<Cell> winningCells;
    private Cell lastMoveCell;

    public Board() {
        this(DEFAULT_SIZE, false);
    }

    public Board(int size, boolean blockBothEnds) {
        this.size = size;
        this.blockBothEnds = blockBothEnds;
        this.winningCells = new ArrayList<>();
        initGrid();
    }

    private void initGrid() {
        grid = new Cell[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
        lastMoveCell = null;
        winningCells.clear();
    }

    public void reset() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c].reset();
            }
        }
        lastMoveCell = null;
        winningCells.clear();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int newSize) {
        if (newSize != this.size && newSize >= 10 && newSize <= 25) {
            this.size = newSize;
            initGrid();
        }
    }

    public boolean isBlockBothEnds() {
        return blockBothEnds;
    }

    public void setBlockBothEnds(boolean blockBothEnds) {
        this.blockBothEnds = blockBothEnds;
    }

    public Cell getCell(int r, int c) {
        if (isValidCoordinate(r, c)) {
            return grid[r][c];
        }
        return null;
    }

    public boolean isValidCoordinate(int r, int c) {
        return r >= 0 && r < size && c >= 0 && c < size;
    }

    public boolean isValidMove(int r, int c) {
        return isValidCoordinate(r, c) && grid[r][c].isEmpty();
    }

    public boolean makeMove(int r, int c, char player) {
        if (!isValidMove(r, c)) {
            return false;
        }

        if (lastMoveCell != null) {
            lastMoveCell.setLastMove(false);
        }

        grid[r][c].setValue(player);
        grid[r][c].setLastMove(true);
        lastMoveCell = grid[r][c];
        return true;
    }

    public void undoMove(int r, int c) {
        if (isValidCoordinate(r, c)) {
            grid[r][c].reset();
        }
    }

    public void setLastMoveCell(Cell cell) {
        if (lastMoveCell != null) {
            lastMoveCell.setLastMove(false);
        }
        lastMoveCell = cell;
        if (lastMoveCell != null) {
            lastMoveCell.setLastMove(true);
        }
    }

    public Cell getLastMoveCell() {
        return lastMoveCell;
    }

    public boolean isFull() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Cell> getWinningCells() {
        return winningCells;
    }

    /**
     * Kiểm tra xem nước đi tại (r, c) của player có tạo thành chuỗi thắng không.
     * Kiểm tra theo 4 hướng: Ngang, Dọc, Chéo chính, Chéo phụ.
     */
    public boolean checkWin(int r, int c, char player) {
        winningCells.clear();
        char opponent = (player == Cell.PLAYER_X) ? Cell.PLAYER_O : Cell.PLAYER_X;

        // 4 hướng: {dRow, dCol}
        int[][] directions = {
            {0, 1},   // Ngang
            {1, 0},   // Dọc
            {1, 1},   // Chéo chính
            {1, -1}   // Chéo phụ
        };

        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];

            List<Cell> currentLine = new ArrayList<>();
            currentLine.add(grid[r][c]);

            // Quét theo chiều dương
            int stepForward = 1;
            while (true) {
                int nr = r + dr * stepForward;
                int nc = c + dc * stepForward;
                if (isValidCoordinate(nr, nc) && grid[nr][nc].getValue() == player) {
                    currentLine.add(grid[nr][nc]);
                    stepForward++;
                } else {
                    break;
                }
            }
            int forwardEndR = r + dr * stepForward;
            int forwardEndC = c + dc * stepForward;

            // Quét theo chiều âm
            int stepBackward = 1;
            while (true) {
                int nr = r - dr * stepBackward;
                int nc = c - dc * stepBackward;
                if (isValidCoordinate(nr, nc) && grid[nr][nc].getValue() == player) {
                    currentLine.add(grid[nr][nc]);
                    stepBackward++;
                } else {
                    break;
                }
            }
            int backwardEndR = r - dr * stepBackward;
            int backwardEndC = c - dc * stepBackward;

            // Đủ 5 con liên tiếp trở lên
            if (currentLine.size() >= 5) {
                if (blockBothEnds) {
                    // Kiểm tra xem có bị đối thủ chặn cả 2 đầu không
                    boolean forwardBlocked = isValidCoordinate(forwardEndR, forwardEndC) 
                            && grid[forwardEndR][forwardEndC].getValue() == opponent;
                    boolean backwardBlocked = isValidCoordinate(backwardEndR, backwardEndC) 
                            && grid[backwardEndR][backwardEndC].getValue() == opponent;

                    // Nếu bị chặn cả 2 đầu thì không thắng theo luật Việt Nam
                    if (forwardBlocked && backwardBlocked) {
                        continue;
                    }
                }

                // Ghi nhận chuỗi thắng
                winningCells.addAll(currentLine);
                for (Cell cell : winningCells) {
                    cell.setWinningCell(true);
                }
                return true;
            }
        }
        return false;
    }
}
