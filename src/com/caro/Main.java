package com.caro;

import com.caro.controller.GameController;
import com.caro.model.Board;
import com.caro.model.GameState;
import com.caro.view.MainFrame;

import javax.swing.*;

/**
 * Điểm khởi chạy chương trình Game Caro.
 */
public class Main {
    public static void main(String[] args) {
        // Thiết lập giao diện theo hệ thống Windows/Modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Chạy trên Event Dispatch Thread (EDT) của Swing
        SwingUtilities.invokeLater(() -> {
            Board board = new Board();
            GameState gameState = new GameState();
            GameController controller = new GameController(board, gameState);

            MainFrame frame = new MainFrame(board, gameState, controller);
            frame.setVisible(true);
        });
    }
}
