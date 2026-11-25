package com.caro.view;

import com.caro.controller.GameController;
import com.caro.model.Board;
import com.caro.model.Cell;
import com.caro.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

/**
 * Panel vẽ bàn cờ Caro bằng Java Graphics2D với hiệu ứng vân gỗ,
 * bóng đổ quân cờ, hiệu ứng hover và highlight đường thắng.
 */
public class CaroBoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Board board;
    private final GameState gameState;
    private GameController controller;

    private int cellSize = 38;
    private int startX = 30;
    private int startY = 30;

    private int hoverRow = -1;
    private int hoverCol = -1;

    // Bảng màu thiết kế cao cấp
    private final Color woodColor = new Color(243, 225, 195);
    private final Color woodBorder = new Color(125, 85, 50);
    private final Color gridLineColor = new Color(90, 65, 45, 180);
    private final Color starPointColor = new Color(90, 65, 45);
    private final Color colorX = new Color(220, 53, 69);
    private final Color colorO = new Color(13, 110, 253);
    private final Color lastMoveColor = new Color(255, 193, 7);
    private final Color winHighlightColor = new Color(40, 167, 69, 160);

    public CaroBoardPanel(Board board, GameState gameState) {
        this.board = board;
        this.gameState = gameState;
        setBackground(new Color(230, 215, 190));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        initListeners();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller == null || gameState.isGameOver()) {
                    return;
                }

                int r = (e.getY() - startY + cellSize / 2) / cellSize;
                int c = (e.getX() - startX + cellSize / 2) / cellSize;

                if (board.isValidMove(r, c)) {
                    controller.onCellClicked(r, c);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                hoverCol = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameState.isGameOver()) {
                    hoverRow = -1;
                    hoverCol = -1;
                    return;
                }

                int r = (e.getY() - startY + cellSize / 2) / cellSize;
                int c = (e.getX() - startX + cellSize / 2) / cellSize;

                if (board.isValidMove(r, c)) {
                    if (r != hoverRow || c != hoverCol) {
                        hoverRow = r;
                        hoverCol = c;
                        repaint();
                    }
                } else if (hoverRow != -1 || hoverCol != -1) {
                    hoverRow = -1;
                    hoverCol = -1;
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Bật khử răng cưa mượt mà
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int size = board.getSize();
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Tự động căn chỉnh kích thước ô theo panel
        int availableSpace = Math.min(panelWidth, panelHeight) - 60;
        cellSize = Math.max(24, availableSpace / size);

        int boardPixelSize = (size - 1) * cellSize;
        startX = (panelWidth - boardPixelSize) / 2;
        startY = (panelHeight - boardPixelSize) / 2;

        // 1. Vẽ nền gỗ và viền bàn cờ
        int margin = 20;
        g2.setColor(woodColor);
        g2.fillRoundRect(startX - margin, startY - margin, boardPixelSize + margin * 2, boardPixelSize + margin * 2, 16, 16);

        g2.setColor(woodBorder);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(startX - margin, startY - margin, boardPixelSize + margin * 2, boardPixelSize + margin * 2, 16, 16);

        // 2. Vẽ lưới bàn cờ
        g2.setColor(gridLineColor);
        g2.setStroke(new BasicStroke(1.2f));

        for (int i = 0; i < size; i++) {
            int pos = i * cellSize;
            // Đường ngang
            g2.drawLine(startX, startY + pos, startX + boardPixelSize, startY + pos);
            // Đường dọc
            g2.drawLine(startX + pos, startY, startX + pos, startY + boardPixelSize);
        }

        // 3. Vẽ nhãn tọa độ (1..size và A..Z)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(new Color(110, 80, 50));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < size; i++) {
            // Tọa độ cột (A, B, C...)
            String colLabel = String.valueOf((char) ('A' + i));
            int colX = startX + i * cellSize - fm.stringWidth(colLabel) / 2;
            g2.drawString(colLabel, colX, startY - 7);

            // Tọa độ hàng (1, 2, 3...)
            String rowLabel = String.valueOf(i + 1);
            int rowY = startY + i * cellSize + fm.getAscent() / 2 - 2;
            g2.drawString(rowLabel, startX - margin + 4, rowY);
        }

        // 4. Vẽ các điểm sao (Star points) định vị
        drawStarPoints(g2, size);

        // 5. Vẽ highlight ô thắng cuộc nếu ván cờ kết thúc
        List<Cell> winningCells = board.getWinningCells();
        if (!winningCells.isEmpty()) {
            g2.setColor(winHighlightColor);
            for (Cell wc : winningCells) {
                int cx = startX + wc.getCol() * cellSize;
                int cy = startY + wc.getRow() * cellSize;
                g2.fillOval(cx - cellSize / 2 + 2, cy - cellSize / 2 + 2, cellSize - 4, cellSize - 4);
            }
        }

        // 6. Vẽ hiệu ứng hover mờ
        if (hoverRow >= 0 && hoverCol >= 0 && !gameState.isGameOver()) {
            int hx = startX + hoverCol * cellSize;
            int hy = startY + hoverRow * cellSize;
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillOval(hx - cellSize / 2 + 4, hy - cellSize / 2 + 4, cellSize - 8, cellSize - 8);

            char currentSymbol = gameState.getCurrentPlayer().getSymbol();
            drawGhostPiece(g2, hx, hy, currentSymbol);
        }

        // 7. Vẽ các quân cờ đã đánh
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Cell cell = board.getCell(r, c);
                if (!cell.isEmpty()) {
                    int px = startX + c * cellSize;
                    int py = startY + r * cellSize;
                    drawPiece(g2, px, py, cell.getValue(), cell.isLastMove());
                }
            }
        }

        // 8. Vẽ đường nối qua các ô chiến thắng
        if (winningCells.size() >= 5) {
            drawWinningLine(g2, winningCells);
        }
    }

    private void drawStarPoints(Graphics2D g2, int size) {
        g2.setColor(starPointColor);
        int r = 4;
        if (size == 15) {
            int[] points = {3, 7, 11};
            for (int pr : points) {
                for (int pc : points) {
                    int cx = startX + pc * cellSize;
                    int cy = startY + pr * cellSize;
                    g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                }
            }
        } else if (size == 19) {
            int[] points = {3, 9, 15};
            for (int pr : points) {
                for (int pc : points) {
                    int cx = startX + pc * cellSize;
                    int cy = startY + pr * cellSize;
                    g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                }
            }
        }
    }

    private void drawPiece(Graphics2D g2, int cx, int cy, char symbol, boolean isLastMove) {
        int radius = (int) (cellSize * 0.38);

        // Đổ bóng quân cờ
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillOval(cx - radius + 2, cy - radius + 3, radius * 2, radius * 2);

        // Vẽ quân X hoặc O
        if (symbol == Cell.PLAYER_X) {
            g2.setColor(colorX);
            g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int offset = (int) (radius * 0.72);
            g2.drawLine(cx - offset, cy - offset, cx + offset, cy + offset);
            g2.drawLine(cx + offset, cy - offset, cx - offset, cy + offset);
        } else {
            g2.setColor(colorO);
            g2.setStroke(new BasicStroke(4.0f));
            int oRad = (int) (radius * 0.82);
            g2.drawOval(cx - oRad, cy - oRad, oRad * 2, oRad * 2);
        }

        // Highlight nước đi cuối cùng
        if (isLastMove) {
            g2.setColor(lastMoveColor);
            g2.setStroke(new BasicStroke(2.2f));
            int markRad = radius + 3;
            g2.drawOval(cx - markRad, cy - markRad, markRad * 2, markRad * 2);

            // Chấm tâm nhỏ
            g2.setColor(Color.WHITE);
            g2.fillOval(cx - 3, cy - 3, 6, 6);
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(cx - 3, cy - 3, 6, 6);
        }
    }

    private void drawGhostPiece(Graphics2D g2, int cx, int cy, char symbol) {
        int radius = (int) (cellSize * 0.38);
        if (symbol == Cell.PLAYER_X) {
            g2.setColor(new Color(colorX.getRed(), colorX.getGreen(), colorX.getBlue(), 90));
            g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int offset = (int) (radius * 0.7);
            g2.drawLine(cx - offset, cy - offset, cx + offset, cy + offset);
            g2.drawLine(cx + offset, cy - offset, cx - offset, cy + offset);
        } else {
            g2.setColor(new Color(colorO.getRed(), colorO.getGreen(), colorO.getBlue(), 90));
            g2.setStroke(new BasicStroke(3.0f));
            int oRad = (int) (radius * 0.8);
            g2.drawOval(cx - oRad, cy - oRad, oRad * 2, oRad * 2);
        }
    }

    private void drawWinningLine(Graphics2D g2, List<Cell> cells) {
        Cell first = cells.get(0);
        Cell last = cells.get(cells.size() - 1);

        int x1 = startX + first.getCol() * cellSize;
        int y1 = startY + first.getRow() * cellSize;
        int x2 = startX + last.getCol() * cellSize;
        int y2 = startY + last.getRow() * cellSize;

        // Vệt sáng hào quang
        g2.setColor(new Color(255, 235, 59, 180));
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);

        // Đường chỉ neon chính giữa
        g2.setColor(new Color(40, 167, 69));
        g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);
    }
}
