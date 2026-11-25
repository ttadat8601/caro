package com.caro.view;

import com.caro.model.Cell;
import com.caro.model.GameState;
import com.caro.model.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Panel hiển thị thông tin 2 người chơi, tỷ số, lượt đi hiện tại
 * và thanh đếm ngược thời gian mỗi nước đi.
 */
public class ScoreBoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final GameState gameState;

    private JLabel lblPlayer1Name;
    private JLabel lblPlayer1Score;
    private JLabel lblPlayer2Name;
    private JLabel lblPlayer2Score;
    private JLabel lblDrawCount;
    private JLabel lblTurnStatus;
    private JLabel lblTimerText;
    private JProgressBar progressBarTimer;

    private JPanel panelCardPlayer1;
    private JPanel panelCardPlayer2;

    public ScoreBoardPanel(GameState gameState) {
        this.gameState = gameState;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(270, 0));

        initComponents();
        updateDisplay();
    }

    private void initComponents() {
        // 1. Tiêu đề bảng điểm
        JLabel lblHeader = new JLabel("BẢNG ĐIỂM & LƯỢT ĐI", SwingConstants.CENTER);
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setForeground(new Color(33, 37, 41));
        add(lblHeader);
        add(Box.createVerticalStrut(10));

        // 2. Trạng thái lượt đi hiện tại
        lblTurnStatus = new JLabel("Đến lượt: Người chơi 1", SwingConstants.CENTER);
        lblTurnStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTurnStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTurnStatus.setOpaque(true);
        lblTurnStatus.setBackground(new Color(230, 242, 255));
        lblTurnStatus.setForeground(new Color(13, 110, 253));
        lblTurnStatus.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 210, 250), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        add(lblTurnStatus);
        add(Box.createVerticalStrut(12));

        // 3. Card Người chơi 1 (X)
        panelCardPlayer1 = createPlayerCard(gameState.getPlayerX(), true);
        add(panelCardPlayer1);
        add(Box.createVerticalStrut(8));

        // 4. Thông tin ván hòa ở giữa
        lblDrawCount = new JLabel("Hòa: 0 ván", SwingConstants.CENTER);
        lblDrawCount.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDrawCount.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblDrawCount.setForeground(new Color(108, 117, 125));
        add(lblDrawCount);
        add(Box.createVerticalStrut(8));

        // 5. Card Người chơi 2 / Máy (O)
        panelCardPlayer2 = createPlayerCard(gameState.getPlayerO(), false);
        add(panelCardPlayer2);
        add(Box.createVerticalStrut(16));

        // 6. Thanh đếm ngược thời gian nước đi
        JLabel lblTimerTitle = new JLabel("Thời gian nước đi:", SwingConstants.LEFT);
        lblTimerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTimerTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTimerTitle.setForeground(new Color(73, 80, 87));
        add(lblTimerTitle);
        add(Box.createVerticalStrut(4));

        lblTimerText = new JLabel("30s còn lại", SwingConstants.CENTER);
        lblTimerText.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTimerText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTimerText.setForeground(new Color(40, 167, 69));
        add(lblTimerText);
        add(Box.createVerticalStrut(4));

        progressBarTimer = new JProgressBar(0, gameState.getTurnTimeLimit());
        progressBarTimer.setValue(gameState.getRemainingTime());
        progressBarTimer.setStringPainted(false);
        progressBarTimer.setPreferredSize(new Dimension(240, 14));
        progressBarTimer.setMaximumSize(new Dimension(240, 14));
        progressBarTimer.setForeground(new Color(40, 167, 69));
        progressBarTimer.setBackground(new Color(222, 226, 230));
        add(progressBarTimer);
    }

    private JPanel createPlayerCard(Player player, boolean isPlayer1) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(222, 226, 230), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        card.setMaximumSize(new Dimension(250, 65));

        // Biểu tượng X hoặc O
        JLabel lblIcon = new JLabel(String.valueOf(player.getSymbol()), SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblIcon.setForeground(player.getColor());
        lblIcon.setPreferredSize(new Dimension(36, 36));
        card.add(lblIcon, BorderLayout.WEST);

        // Tên người chơi
        JLabel lblName = new JLabel(player.getName());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(new Color(33, 37, 41));

        // Điểm số
        JLabel lblScore = new JLabel("Thắng: " + player.getScore());
        lblScore.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblScore.setForeground(new Color(108, 117, 125));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        infoPanel.setOpaque(false);
        infoPanel.add(lblName);
        infoPanel.add(lblScore);
        card.add(infoPanel, BorderLayout.CENTER);

        if (isPlayer1) {
            lblPlayer1Name = lblName;
            lblPlayer1Score = lblScore;
        } else {
            lblPlayer2Name = lblName;
            lblPlayer2Score = lblScore;
        }

        return card;
    }

    /**
     * Cập nhật thông tin điểm số, tên người chơi và highlight lượt đi.
     */
    public void updateDisplay() {
        Player p1 = gameState.getPlayerX();
        Player p2 = gameState.getPlayerO();

        lblPlayer1Name.setText(p1.getName());
        lblPlayer1Score.setText("Thắng: " + p1.getScore() + " ván");

        lblPlayer2Name.setText(p2.getName());
        lblPlayer2Score.setText("Thắng: " + p2.getScore() + " ván");

        lblDrawCount.setText("Hòa: " + gameState.getDrawCount() + " ván");

        Player current = gameState.getCurrentPlayer();
        if (gameState.isGameOver()) {
            if (gameState.getWinner() != null) {
                lblTurnStatus.setText("★ THẮNG CUỘC: " + gameState.getWinner().getName());
                lblTurnStatus.setBackground(new Color(212, 237, 218));
                lblTurnStatus.setForeground(new Color(21, 87, 36));
            } else {
                lblTurnStatus.setText("VÁN ĐẤU HÒA!");
                lblTurnStatus.setBackground(new Color(255, 243, 205));
                lblTurnStatus.setForeground(new Color(133, 100, 4));
            }
        } else {
            lblTurnStatus.setText("Đến lượt: " + current.getName());
            if (current.getSymbol() == Cell.PLAYER_X) {
                lblTurnStatus.setBackground(new Color(255, 235, 238));
                lblTurnStatus.setForeground(new Color(194, 24, 91));
                panelCardPlayer1.setBorder(new LineBorder(new Color(220, 53, 69), 2, true));
                panelCardPlayer2.setBorder(new LineBorder(new Color(222, 226, 230), 1, true));
            } else {
                lblTurnStatus.setBackground(new Color(230, 242, 255));
                lblTurnStatus.setForeground(new Color(13, 110, 253));
                panelCardPlayer1.setBorder(new LineBorder(new Color(222, 226, 230), 1, true));
                panelCardPlayer2.setBorder(new LineBorder(new Color(13, 110, 253), 2, true));
            }
        }

        updateTimerDisplay();
    }

    /**
     * Cập nhật hiển thị đồng hồ đếm ngược.
     */
    public void updateTimerDisplay() {
        int limit = gameState.getTurnTimeLimit();
        if (limit <= 0) {
            lblTimerText.setText("Không giới hạn");
            progressBarTimer.setValue(100);
            progressBarTimer.setForeground(new Color(40, 167, 69));
            return;
        }

        int remaining = gameState.getRemainingTime();
        lblTimerText.setText(remaining + "s còn lại");
        progressBarTimer.setMaximum(limit);
        progressBarTimer.setValue(remaining);

        // Đổi màu theo độ khẩn cấp
        if (remaining <= 5) {
            progressBarTimer.setForeground(new Color(220, 53, 69)); // Đỏ
            lblTimerText.setForeground(new Color(220, 53, 69));
        } else if (remaining <= 10) {
            progressBarTimer.setForeground(new Color(255, 193, 7)); // Vàng cam
            lblTimerText.setForeground(new Color(217, 130, 0));
        } else {
            progressBarTimer.setForeground(new Color(40, 167, 69)); // Xanh lá
            lblTimerText.setForeground(new Color(40, 167, 69));
        }
    }
}
