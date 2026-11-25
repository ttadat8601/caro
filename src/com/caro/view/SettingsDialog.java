package com.caro.view;

import com.caro.controller.GameController;
import com.caro.model.Board;
import com.caro.model.GameState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Hộp thoại Cài đặt các thông số trò chơi: Kích thước bàn cờ,
 * thời gian nước đi, luật chặn 2 đầu, đổi tên người chơi, âm thanh.
 */
public class SettingsDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final Board board;
    private final GameState gameState;
    private final GameController controller;

    private JComboBox<String> cbBoardSize;
    private JComboBox<String> cbTurnTime;
    private JCheckBox chkBlockBothEnds;
    private JCheckBox chkSound;
    private JTextField txtPlayer1;
    private JTextField txtPlayer2;

    public SettingsDialog(Frame parent, Board board, GameState gameState, GameController controller) {
        super(parent, "Cài Đặt Trò Chơi", true);
        this.board = board;
        this.gameState = gameState;
        this.controller = controller;

        setSize(420, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Kích thước bàn cờ
        JPanel pnlSize = new JPanel(new BorderLayout(10, 0));
        pnlSize.add(new JLabel("Kích thước bàn cờ:"), BorderLayout.WEST);
        cbBoardSize = new JComboBox<>(new String[]{"15 x 15 (Chuẩn)", "19 x 19 (Mở rộng)"});
        cbBoardSize.setSelectedIndex(board.getSize() == 19 ? 1 : 0);
        pnlSize.add(cbBoardSize, BorderLayout.EAST);
        contentPanel.add(pnlSize);
        contentPanel.add(Box.createVerticalStrut(12));

        // 2. Thời gian mỗi nước đi
        JPanel pnlTime = new JPanel(new BorderLayout(10, 0));
        pnlTime.add(new JLabel("Thời gian mỗi lượt đi:"), BorderLayout.WEST);
        cbTurnTime = new JComboBox<>(new String[]{"15 giây", "30 giây (Mặc định)", "60 giây", "Không giới hạn"});
        int currentTime = gameState.getTurnTimeLimit();
        if (currentTime == 15) cbTurnTime.setSelectedIndex(0);
        else if (currentTime == 30) cbTurnTime.setSelectedIndex(1);
        else if (currentTime == 60) cbTurnTime.setSelectedIndex(2);
        else cbTurnTime.setSelectedIndex(3);
        pnlTime.add(cbTurnTime, BorderLayout.EAST);
        contentPanel.add(pnlTime);
        contentPanel.add(Box.createVerticalStrut(12));

        // 3. Luật chặn 2 đầu
        chkBlockBothEnds = new JCheckBox("Áp dụng luật chặn 2 đầu (Luật Caro Việt Nam)");
        chkBlockBothEnds.setSelected(board.isBlockBothEnds());
        chkBlockBothEnds.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contentPanel.add(chkBlockBothEnds);
        contentPanel.add(Box.createVerticalStrut(10));

        // 4. Âm thanh hiệu ứng
        chkSound = new JCheckBox("Bật hiệu ứng âm thanh");
        chkSound.setSelected(gameState.isSoundEnabled());
        chkSound.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contentPanel.add(chkSound);
        contentPanel.add(Box.createVerticalStrut(14));

        // 5. Tên người chơi 1
        JPanel pnlP1 = new JPanel(new BorderLayout(10, 0));
        pnlP1.add(new JLabel("Tên Người chơi 1 (X):"), BorderLayout.WEST);
        txtPlayer1 = new JTextField(gameState.getPlayerX().getName(), 15);
        pnlP1.add(txtPlayer1, BorderLayout.EAST);
        contentPanel.add(pnlP1);
        contentPanel.add(Box.createVerticalStrut(10));

        // 6. Tên người chơi 2
        JPanel pnlP2 = new JPanel(new BorderLayout(10, 0));
        pnlP2.add(new JLabel("Tên Người chơi 2 (O):"), BorderLayout.WEST);
        txtPlayer2 = new JTextField(gameState.getPlayerO().getName(), 15);
        pnlP2.add(txtPlayer2, BorderLayout.EAST);
        contentPanel.add(pnlP2);
        contentPanel.add(Box.createVerticalStrut(20));

        // Nút Lưu và Hủy
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("Áp dụng");
        btnSave.setBackground(new Color(13, 110, 253));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> applySettings());

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnSave);
        contentPanel.add(pnlButtons);

        setContentPane(contentPanel);
    }

    private void applySettings() {
        // Áp dụng kích thước bàn cờ
        int newSize = (cbBoardSize.getSelectedIndex() == 1) ? 19 : 15;
        boolean sizeChanged = (newSize != board.getSize());

        // Áp dụng thời gian
        int timeLimit;
        switch (cbTurnTime.getSelectedIndex()) {
            case 0: timeLimit = 15; break;
            case 1: timeLimit = 30; break;
            case 2: timeLimit = 60; break;
            default: timeLimit = 0; break;
        }
        gameState.setTurnTimeLimit(timeLimit);

        // Áp dụng luật chặn 2 đầu
        board.setBlockBothEnds(chkBlockBothEnds.isSelected());

        // Áp dụng âm thanh
        gameState.setSoundEnabled(chkSound.isSelected());

        // Đổi tên người chơi
        if (!txtPlayer1.getText().trim().isEmpty()) {
            gameState.getPlayerX().setName(txtPlayer1.getText().trim());
        }
        if (!txtPlayer2.getText().trim().isEmpty()) {
            gameState.getPlayerO().setName(txtPlayer2.getText().trim());
        }

        if (sizeChanged) {
            board.setSize(newSize);
            controller.startNewGame();
        } else {
            controller.updateUI();
        }

        JOptionPane.showMessageDialog(this, "Đã lưu cài đặt thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
