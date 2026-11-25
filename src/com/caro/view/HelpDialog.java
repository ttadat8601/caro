package com.caro.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Hộp thoại Hướng dẫn luật chơi cờ Caro, thao tác sử dụng và phím tắt.
 */
public class HelpDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    public HelpDialog(Frame parent) {
        super(parent, "Hướng Dẫn & Giới Thiệu Đồ Án", true);
        setSize(540, 480);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");

        String htmlContent = "<html><body style='font-family: Segoe UI, sans-serif; font-size: 11px; color: #212529;'>"
                + "<h2 style='color: #0d6efd; margin-bottom: 4px;'>ĐỒ ÁN GAME CỜ CARO (GOMOKU)</h2>"
                + "<p><b>Học phần:</b> Lập trình ứng dụng Java (CS311A1)</p>"
                + "<hr/>"
                + "<h3 style='color: #198754; margin-bottom: 4px;'>1. LUẬT CHƠI CỜ CARO</h3>"
                + "<ul>"
                + "  <li>Hai người chơi luân phiên đặt quân cờ (X hoặc O) vào các ô trống trên bàn cờ.</li>"
                + "  <li><b>Điều kiện thắng:</b> Người đầu tiên tạo được chuỗi <b>5 quân cờ liên tiếp</b> theo hàng ngang, hàng dọc hoặc đường chéo sẽ giành chiến thắng.</li>"
                + "  <li><b>Luật chặn 2 đầu (Tùy chọn):</b> Khi bật luật này, nếu chuỗi 5 quân cờ bị đối phương chặn ở cả 2 đầu thì không được tính là thắng.</li>"
                + "  <li><b>Hết thời gian:</b> Nếu người chơi không hoàn thành nước đi trước khi bộ đếm giờ về 0, người đó sẽ bị xử thua ván đấu.</li>"
                + "</ul>"
                + "<h3 style='color: #d63384; margin-bottom: 4px;'>2. CÁC TÍNH NĂNG CHÍNH</h3>"
                + "<ul>"
                + "  <li><b>Đấu 2 người (PvP):</b> 2 người so tài trên cùng máy tính.</li>"
                + "  <li><b>Đấu với Máy (PvE):</b> AI thông minh sử dụng hàm đánh giá thế cờ Heuristic đa chiều (Tấn công & Phòng thủ).</li>"
                + "  <li><b>Đi lại (Undo) & Làm lại (Redo):</b> Hỗ trợ lùi lại và đi tiếp các nước cờ.</li>"
                + "  <li><b>Lưu & Mở ván cờ (Save / Load):</b> Lưu ván đấu đang dở dang ra file để mở lại sau.</li>"
                + "  <li><b>Bảng điểm & Đồng hồ:</b> Theo dõi sát sao tỷ số và thời gian nước đi.</li>"
                + "  <li><b>Hiệu ứng âm thanh & đồ họa:</b> Hiển thị hover preview, highlight ô vừa đi và chuỗi 5 ô chiến thắng.</li>"
                + "</ul>"
                + "<h3 style='color: #fd7e14; margin-bottom: 4px;'>3. PHÍM TẮT NHANH</h3>"
                + "<ul>"
                + "  <li><b>F2:</b> Bắt đầu ván mới</li>"
                + "  <li><b>Ctrl + Z:</b> Hoàn tác nước đi (Undo)</li>"
                + "  <li><b>Ctrl + Y:</b> Làm lại nước đi (Redo)</li>"
                + "  <li><b>Ctrl + S:</b> Lưu ván cờ vào file</li>"
                + "  <li><b>Ctrl + O:</b> Mở ván cờ từ file</li>"
                + "</ul>"
                + "</body></html>";

        editorPane.setText(htmlContent);
        editorPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.add(btnClose);
        contentPanel.add(pnlBottom, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }
}
