package com.caro.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Quản lý toàn bộ trạng thái ván cờ, người chơi, lịch sử nước đi và thiết lập.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_TURN_TIME = 30; // 30 giây mỗi lượt

    private Player playerX;
    private Player playerO;
    private Player currentPlayer;
    private GameMode gameMode;
    private boolean isGameOver;
    private Player winner;
    private int drawCount;

    private List<Move> moveHistory;
    private Stack<Move> redoStack;

    private int turnTimeLimit; // Thời gian tối đa mỗi lượt (giây), 0 là không giới hạn
    private int remainingTime; // Thời gian còn lại của lượt hiện tại
    private boolean soundEnabled;
    private String difficulty; // "EASY" hoặc "HARD"

    public GameState() {
        this.playerX = new Player("Người chơi 1 (X)", Cell.PLAYER_X, new Color(220, 53, 69), false);
        this.playerO = new Player("Người chơi 2 (O)", Cell.PLAYER_O, new Color(13, 110, 253), false);
        this.currentPlayer = playerX;
        this.gameMode = GameMode.PVP;
        this.isGameOver = false;
        this.winner = null;
        this.drawCount = 0;

        this.moveHistory = new ArrayList<>();
        this.redoStack = new Stack<>();

        this.turnTimeLimit = DEFAULT_TURN_TIME;
        this.remainingTime = DEFAULT_TURN_TIME;
        this.soundEnabled = true;
        this.difficulty = "HARD";
    }

    public void startNewGame() {
        this.currentPlayer = playerX;
        this.isGameOver = false;
        this.winner = null;
        this.moveHistory.clear();
        this.redoStack.clear();
        this.remainingTime = turnTimeLimit;

        // Cập nhật tên nếu chế độ PvE
        if (gameMode == GameMode.PVE) {
            playerO.setName("Máy AI (O)");
            playerO.setAI(true);
        } else {
            playerO.setName("Người chơi 2 (O)");
            playerO.setAI(false);
        }
    }

    public void switchTurn() {
        currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
        remainingTime = turnTimeLimit;
    }

    public void recordMove(Move move) {
        moveHistory.add(move);
        redoStack.clear(); // Nước đi mới sẽ xóa stack redo
    }

    public Move undoLastMove() {
        if (!moveHistory.isEmpty()) {
            Move last = moveHistory.remove(moveHistory.size() - 1);
            redoStack.push(last);
            return last;
        }
        return null;
    }

    public Move redoMove() {
        if (!redoStack.isEmpty()) {
            Move move = redoStack.pop();
            moveHistory.add(move);
            return move;
        }
        return null;
    }

    public boolean canUndo() {
        return !moveHistory.isEmpty() && !isGameOver;
    }

    public boolean canRedo() {
        return !redoStack.isEmpty() && !isGameOver;
    }

    // Getters and Setters
    public Player getPlayerX() {
        return playerX;
    }

    public void setPlayerX(Player playerX) {
        this.playerX = playerX;
    }

    public Player getPlayerO() {
        return playerO;
    }

    public void setPlayerO(Player playerO) {
        this.playerO = playerO;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        if (gameMode == GameMode.PVE) {
            playerO.setName("Máy AI (O)");
            playerO.setAI(true);
        } else {
            playerO.setName("Người chơi 2 (O)");
            playerO.setAI(false);
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public void incrementDrawCount() {
        this.drawCount++;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    public int getTurnTimeLimit() {
        return turnTimeLimit;
    }

    public void setTurnTimeLimit(int turnTimeLimit) {
        this.turnTimeLimit = turnTimeLimit;
        this.remainingTime = turnTimeLimit;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void decrementRemainingTime() {
        if (remainingTime > 0) {
            remainingTime--;
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
