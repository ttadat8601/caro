package com.caro.controller;

import com.caro.model.*;
import com.caro.sound.SoundEffect;
import com.caro.view.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.List;

/**
 * Controller điều phối toàn bộ luồng hoạt động của game Caro:
 * Nhận tương tác người dùng, xử lý AI, đếm giờ, lưu và tải game.
 */
public class GameController {
    private final Board board;
    private final GameState gameState;
    private final AIPlayer aiPlayer;

    private MainFrame mainFrame;
    private CaroBoardPanel boardPanel;
    private ScoreBoardPanel scoreBoardPanel;
    private ControlPanel controlPanel;

    private Timer turnTimer;

    public GameController(Board board, GameState gameState) {
        this.board = board;
        this.gameState = gameState;
        this.aiPlayer = new AIPlayer(Cell.PLAYER_O, gameState.getDifficulty());

        initTurnTimer();
    }

    public void setViews(MainFrame mainFrame, CaroBoardPanel boardPanel, 
                         ScoreBoardPanel scoreBoardPanel, ControlPanel controlPanel) {
        this.mainFrame = mainFrame;
        this.boardPanel = boardPanel;
        this.scoreBoardPanel = scoreBoardPanel;
        this.controlPanel = controlPanel;
    }

    private void initTurnTimer() {
        turnTimer = new Timer(1000, e -> {
            if (gameState.isGameOver() || gameState.getTurnTimeLimit() <= 0) {
                return;
            }

            gameState.decrementRemainingTime();
            int remaining = gameState.getRemainingTime();

            if (scoreBoardPanel != null) {
                scoreBoardPanel.updateTimerDisplay();
            }

            if (remaining <= 5 && remaining > 0 && gameState.isSoundEnabled()) {
                SoundEffect.playAlertSound();
            }

            if (remaining <= 0) {
                // Hết giờ! Người chơi hiện tại bị xử thua
                turnTimer.stop();
                Player loser = gameState.getCurrentPlayer();
                Player winner = (loser == gameState.getPlayerX()) ? gameState.getPlayerO() : gameState.getPlayerX();

                gameState.setGameOver(true);
                gameState.setWinner(winner);
                winner.addScore();

                if (gameState.isSoundEnabled()) {
                    SoundEffect.playWinSound();
                }

                updateUI();
                JOptionPane.showMessageDialog(mainFrame,
                        "Hết thời gian nước đi! " + loser.getName() + " bị xử thua.\n" +
                                "Người chiến thắng: " + winner.getName() + "!",
                        "Hết Giờ", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public void startTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
        if (gameState.getTurnTimeLimit() > 0 && !gameState.isGameOver()) {
            gameState.setRemainingTime(gameState.getTurnTimeLimit());
            if (scoreBoardPanel != null) {
                scoreBoardPanel.updateTimerDisplay();
            }
            turnTimer.start();
        }
    }

    public void stopTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
    }

    public void startNewGame() {
        stopTurnTimer();
        board.reset();
        gameState.startNewGame();
        aiPlayer.setDifficulty(gameState.getDifficulty());
        updateUI();
        startTurnTimer();
    }

    public void onCellClicked(int r, int c) {
        if (gameState.isGameOver()) {
            return;
        }

        // Nếu đang là lượt của AI trong chế độ PvE thì bỏ qua click của người chơi
        if (gameState.getGameMode() == GameMode.PVE && gameState.getCurrentPlayer().isAI()) {
            return;
        }

        Player current = gameState.getCurrentPlayer();
        if (board.makeMove(r, c, current.getSymbol())) {
            Move move = new Move(r, c, current.getSymbol());
            gameState.recordMove(move);

            if (gameState.isSoundEnabled()) {
                SoundEffect.playMoveSound();
            }

            // Kiểm tra thắng
            if (board.checkWin(r, c, current.getSymbol())) {
                handleGameOver(current);
                return;
            }

            // Kiểm tra hòa
            if (board.isFull()) {
                handleGameDraw();
                return;
            }

            // Đổi lượt
            gameState.switchTurn();
            updateUI();
            startTurnTimer();

            // Nếu đến lượt AI
            if (gameState.getGameMode() == GameMode.PVE && gameState.getCurrentPlayer().isAI()) {
                triggerAIMove();
            }
        }
    }

    private void triggerAIMove() {
        // Cho AI suy nghĩ ngầm với độ trễ 350ms tạo cảm giác tự nhiên
        Timer aiDelayTimer = new Timer(350, evt -> {
            ((Timer) evt.getSource()).stop();
            if (gameState.isGameOver()) return;

            Move bestMove = aiPlayer.findBestMove(board);
            if (bestMove != null) {
                int r = bestMove.getRow();
                int c = bestMove.getCol();
                Player ai = gameState.getPlayerO();

                if (board.makeMove(r, c, ai.getSymbol())) {
                    gameState.recordMove(bestMove);

                    if (gameState.isSoundEnabled()) {
                        SoundEffect.playMoveSound();
                    }

                    if (board.checkWin(r, c, ai.getSymbol())) {
                        handleGameOver(ai);
                        return;
                    }

                    if (board.isFull()) {
                        handleGameDraw();
                        return;
                    }

                    gameState.switchTurn();
                    updateUI();
                    startTurnTimer();
                }
            }
        });
        aiDelayTimer.setRepeats(false);
        aiDelayTimer.start();
    }

    private void handleGameOver(Player winner) {
        stopTurnTimer();
        gameState.setGameOver(true);
        gameState.setWinner(winner);
        winner.addScore();

        if (gameState.isSoundEnabled()) {
            SoundEffect.playWinSound();
        }

        updateUI();

        int choice = JOptionPane.showConfirmDialog(mainFrame,
                "Chúc mừng " + winner.getName() + " đã giành CHIẾN THẮNG!\nBạn có muốn bắt đầu ván mới không?",
                "Kết Thúc Ván Đấu", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        }
    }

    private void handleGameDraw() {
        stopTurnTimer();
        gameState.setGameOver(true);
        gameState.setWinner(null);
        gameState.incrementDrawCount();

        updateUI();

        int choice = JOptionPane.showConfirmDialog(mainFrame,
                "Bàn cờ đã đầy! Ván đấu kết thúc với kết quả HÒA!\nBạn có muốn chơi ván mới không?",
                "Hòa Cờ", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        }
    }

    public void undoMove() {
        if (!gameState.canUndo()) return;

        stopTurnTimer();

        if (gameState.getGameMode() == GameMode.PVE) {
            // Lùi 2 nước (nước của AI và nước của người chơi)
            Move aiMove = gameState.undoLastMove();
            if (aiMove != null) {
                board.undoMove(aiMove.getRow(), aiMove.getCol());
            }

            Move humanMove = gameState.undoLastMove();
            if (humanMove != null) {
                board.undoMove(humanMove.getRow(), humanMove.getCol());
            }

            // Đặt lại lượt cho người chơi
            gameState.setCurrentPlayer(gameState.getPlayerX());
        } else {
            // Chế độ PvP: Lùi 1 nước và đảo lại lượt
            Move last = gameState.undoLastMove();
            if (last != null) {
                board.undoMove(last.getRow(), last.getCol());
                gameState.switchTurn();
            }
        }

        // Khôi phục lại highlight ô nước đi cuối
        List<Move> history = gameState.getMoveHistory();
        if (!history.isEmpty()) {
            Move prev = history.get(history.size() - 1);
            board.setLastMoveCell(board.getCell(prev.getRow(), prev.getCol()));
        } else {
            board.setLastMoveCell(null);
        }

        if (gameState.isSoundEnabled()) {
            SoundEffect.playUndoSound();
        }

        updateUI();
        startTurnTimer();
    }

    public void redoMove() {
        if (!gameState.canRedo()) return;

        stopTurnTimer();

        if (gameState.getGameMode() == GameMode.PVE) {
            // Đi lại 2 nước
            Move humanMove = gameState.redoMove();
            if (humanMove != null) {
                board.makeMove(humanMove.getRow(), humanMove.getCol(), humanMove.getPlayer());
            }

            Move aiMove = gameState.redoMove();
            if (aiMove != null) {
                board.makeMove(aiMove.getRow(), aiMove.getCol(), aiMove.getPlayer());
            }

            gameState.setCurrentPlayer(gameState.getPlayerX());
        } else {
            Move move = gameState.redoMove();
            if (move != null) {
                board.makeMove(move.getRow(), move.getCol(), move.getPlayer());
                gameState.switchTurn();
            }
        }

        if (gameState.isSoundEnabled()) {
            SoundEffect.playMoveSound();
        }

        updateUI();
        startTurnTimer();
    }

    public void resign() {
        if (gameState.isGameOver()) return;

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                gameState.getCurrentPlayer().getName() + " có chắc chắn muốn ĐẦU HÀNG?",
                "Xác Nhận Đầu Hàng", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Player loser = gameState.getCurrentPlayer();
            Player winner = (loser == gameState.getPlayerX()) ? gameState.getPlayerO() : gameState.getPlayerX();
            handleGameOver(winner);
        }
    }

    public void offerDraw() {
        if (gameState.isGameOver()) return;

        if (gameState.getGameMode() == GameMode.PVP) {
            Player other = (gameState.getCurrentPlayer() == gameState.getPlayerX())
                    ? gameState.getPlayerO() : gameState.getPlayerX();

            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    gameState.getCurrentPlayer().getName() + " đề nghị XIN HÒA!\n" +
                            other.getName() + " có chấp nhận kết quả hòa không?",
                    "Cầu Hòa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                handleGameDraw();
            }
        } else {
            // Chế độ chơi với AI: AI chỉ đồng ý hòa khi đã đi hơn 30 nước
            if (gameState.getMoveHistory().size() >= 30) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Máy AI nhận thấy thế cờ cân bằng và ĐỒNG Ý HÒA!",
                        "Cầu Hòa Thành Công", JOptionPane.INFORMATION_MESSAGE);
                handleGameDraw();
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "Máy AI nhận định thế cờ đang có lợi và TỪ CHỐI hòa!",
                        "Cầu Hòa Thất Bại", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void changeGameMode(GameMode mode) {
        if (mode != gameState.getGameMode()) {
            gameState.setGameMode(mode);
            startNewGame();
        }
    }

    public void changeDifficulty(String difficulty) {
        gameState.setDifficulty(difficulty);
        aiPlayer.setDifficulty(difficulty);
    }

    public void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Ván Cờ Caro");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Caro Save File (*.caro)", "caro"));

        int userSelection = fileChooser.showSaveDialog(mainFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".caro")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".caro");
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                oos.writeObject(board);
                oos.writeObject(gameState);
                JOptionPane.showMessageDialog(mainFrame, "Đã lưu ván cờ thành công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Lỗi khi lưu file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Mở Ván Cờ Caro Đã Lưu");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Caro Save File (*.caro)", "caro"));

        int userSelection = fileChooser.showOpenDialog(mainFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                Board loadedBoard = (Board) ois.readObject();
                GameState loadedState = (GameState) ois.readObject();

                // Cập nhật lại board và gameState
                board.setSize(loadedBoard.getSize());
                board.setBlockBothEnds(loadedBoard.isBlockBothEnds());
                for (int r = 0; r < loadedBoard.getSize(); r++) {
                    for (int c = 0; c < loadedBoard.getSize(); c++) {
                        Cell cOrig = loadedBoard.getCell(r, c);
                        Cell cTarget = board.getCell(r, c);
                        cTarget.setValue(cOrig.getValue());
                        cTarget.setLastMove(cOrig.isLastMove());
                        cTarget.setWinningCell(cOrig.isWinningCell());
                    }
                }
                board.setLastMoveCell(loadedBoard.getLastMoveCell());

                // Cập nhật GameState
                gameState.setPlayerX(loadedState.getPlayerX());
                gameState.setPlayerO(loadedState.getPlayerO());
                gameState.setCurrentPlayer(loadedState.getCurrentPlayer());
                gameState.setGameMode(loadedState.getGameMode());
                gameState.setGameOver(loadedState.isGameOver());
                gameState.setWinner(loadedState.getWinner());
                gameState.setTurnTimeLimit(loadedState.getTurnTimeLimit());
                gameState.setRemainingTime(loadedState.getRemainingTime());
                gameState.setSoundEnabled(loadedState.isSoundEnabled());
                gameState.setDifficulty(loadedState.getDifficulty());

                gameState.getMoveHistory().clear();
                gameState.getMoveHistory().addAll(loadedState.getMoveHistory());

                if (controlPanel != null) {
                    controlPanel.syncModeUI(gameState.getGameMode());
                }

                updateUI();
                startTurnTimer();

                JOptionPane.showMessageDialog(mainFrame, "Đã mở ván cờ thành công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Không thể đọc file ván cờ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void openSettings() {
        SettingsDialog dialog = new SettingsDialog(mainFrame, board, gameState, this);
        dialog.setVisible(true);
    }

    public void openHelp() {
        HelpDialog dialog = new HelpDialog(mainFrame);
        dialog.setVisible(true);
    }

    public void updateUI() {
        if (boardPanel != null) {
            boardPanel.repaint();
        }
        if (scoreBoardPanel != null) {
            scoreBoardPanel.updateDisplay();
        }
        if (controlPanel != null) {
            controlPanel.updateButtonsState();
        }
    }
}
