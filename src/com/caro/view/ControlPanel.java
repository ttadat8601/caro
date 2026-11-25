package com.caro.view;

import com.caro.controller.GameController;
import com.caro.model.GameMode;
import com.caro.model.GameState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel chứa các nút bấm điều khiển trò chơi: Ván mới, Đi lại, Làm lại,
 * Đầu hàng, Xin hòa, Lưu game, Mở game, Cài đặt, Hướng dẫn.
 */
public class ControlPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final GameState gameState;
    private GameController controller;

    private JButton btnNewGame;
    private JButton btnUndo;
    private JButton btnRedo;
    private JButton btnResign;
    private JButton btnOfferDraw;
    private JButton btnSaveGame;
    private JButton btnLoadGame;
    private JButton btnSettings;
    private JButton btnHelp;

    private JComboBox<String> cbMode;
    private JComboBox<String> cbDifficulty;

    public ControlPanel(GameState gameState) {
        this.gameState = gameState;
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
        setBackground(new Color(241, 243, 245));
        setBorder(new EmptyBorder(6, 10, 6, 10));

        initComponents();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    private void initComponents() {
        // Nút Ván mới
        btnNewGame = createStyledButton("Ván mới", new Color(40, 167, 69), Color.WHITE);
        btnNewGame.addActionListener(e -> {
            if (controller != null) controller.startNewGame();
        });
        add(btnNewGame);

        // Chế độ chơi
        JLabel lblMode = new JLabel("Chế độ:");
        lblMode.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(lblMode);

        cbMode = new JComboBox<>(new String[]{"2 Người (PvP)", "Đấu với Máy (PvE)"});
        cbMode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbMode.addActionListener(e -> {
            if (controller != null) {
                int idx = cbMode.getSelectedIndex();
                controller.changeGameMode(idx == 0 ? GameMode.PVP : GameMode.PVE);
                cbDifficulty.setEnabled(idx == 1);
            }
        });
        add(cbMode);

        // Độ khó AI
        JLabel lblDiff = new JLabel("Độ khó:");
        lblDiff.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(lblDiff);

        cbDifficulty = new JComboBox<>(new String[]{"Dễ", "Khó"});
        cbDifficulty.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbDifficulty.setSelectedIndex(1); // Mặc định là Khó
        cbDifficulty.setEnabled(false); // Ban đầu là PvP nên disable
        cbDifficulty.addActionListener(e -> {
            if (controller != null) {
                String diff = cbDifficulty.getSelectedIndex() == 0 ? "EASY" : "HARD";
                controller.changeDifficulty(diff);
            }
        });
        add(cbDifficulty);

        // Nút Undo
        btnUndo = createStyledButton("Đi lại (Undo)", new Color(255, 193, 7), Color.BLACK);
        btnUndo.addActionListener(e -> {
            if (controller != null) controller.undoMove();
        });
        add(btnUndo);

        // Nút Redo
        btnRedo = createStyledButton("Làm lại (Redo)", new Color(108, 117, 125), Color.WHITE);
        btnRedo.addActionListener(e -> {
            if (controller != null) controller.redoMove();
        });
        add(btnRedo);

        // Nút Đầu hàng
        btnResign = createStyledButton("Đầu hàng", new Color(220, 53, 69), Color.WHITE);
        btnResign.addActionListener(e -> {
            if (controller != null) controller.resign();
        });
        add(btnResign);

        // Nút Xin hòa
        btnOfferDraw = createStyledButton("Xin hòa", new Color(23, 162, 184), Color.WHITE);
        btnOfferDraw.addActionListener(e -> {
            if (controller != null) controller.offerDraw();
        });
        add(btnOfferDraw);

        // Nút Lưu Game
        btnSaveGame = createStyledButton("Lưu ván cờ", new Color(102, 16, 242), Color.WHITE);
        btnSaveGame.addActionListener(e -> {
            if (controller != null) controller.saveGame();
        });
        add(btnSaveGame);

        // Nút Mở Game
        btnLoadGame = createStyledButton("Mở ván cờ", new Color(111, 66, 193), Color.WHITE);
        btnLoadGame.addActionListener(e -> {
            if (controller != null) controller.loadGame();
        });
        add(btnLoadGame);

        // Nút Cài đặt
        btnSettings = createStyledButton("Cài đặt", new Color(52, 58, 64), Color.WHITE);
        btnSettings.addActionListener(e -> {
            if (controller != null) controller.openSettings();
        });
        add(btnSettings);

        // Nút Hướng dẫn
        btnHelp = createStyledButton("Hướng dẫn", new Color(0, 123, 255), Color.WHITE);
        btnHelp.addActionListener(e -> {
            if (controller != null) controller.openHelp();
        });
        add(btnHelp);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return btn;
    }

    public void updateButtonsState() {
        btnUndo.setEnabled(gameState.canUndo());
        btnRedo.setEnabled(gameState.canRedo());
        btnResign.setEnabled(!gameState.isGameOver());
        btnOfferDraw.setEnabled(!gameState.isGameOver());
    }

    public void syncModeUI(GameMode mode) {
        cbMode.setSelectedIndex(mode == GameMode.PVP ? 0 : 1);
        cbDifficulty.setEnabled(mode == GameMode.PVE);
    }
}
