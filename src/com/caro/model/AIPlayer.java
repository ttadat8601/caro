package com.caro.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Thuật toán AI chơi cờ Caro sử dụng hàm đánh giá thế cờ Heuristic (Tấn công & Phòng thủ).
 */
public class AIPlayer {
    private final char aiSymbol;
    private final char humanSymbol;
    private String difficulty; // "EASY" hoặc "HARD"
    private final Random random;

    // Trọng số đánh giá tấn công
    private static final int ATK_FIVE = 200000000;
    private static final int ATK_OPEN_FOUR = 10000000;
    private static final int ATK_CLOSED_FOUR = 100000;
    private static final int ATK_OPEN_THREE = 80000;
    private static final int ATK_CLOSED_THREE = 5000;
    private static final int ATK_OPEN_TWO = 1000;
    private static final int ATK_CLOSED_TWO = 100;

    // Trọng số đánh giá phòng thủ (chặn nước đối thủ)
    private static final int DEF_FIVE = 150000000;
    private static final int DEF_OPEN_FOUR = 80000000;
    private static final int DEF_CLOSED_FOUR = 500000;
    private static final int DEF_OPEN_THREE = 250000;
    private static final int DEF_CLOSED_THREE = 15000;
    private static final int DEF_OPEN_TWO = 3000;
    private static final int DEF_CLOSED_TWO = 200;

    public AIPlayer(char aiSymbol, String difficulty) {
        this.aiSymbol = aiSymbol;
        this.humanSymbol = (aiSymbol == Cell.PLAYER_X) ? Cell.PLAYER_O : Cell.PLAYER_X;
        this.difficulty = (difficulty != null) ? difficulty : "HARD";
        this.random = new Random();
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Tìm nước đi tối ưu nhất cho AI trên bàn cờ.
     */
    public Move findBestMove(Board board) {
        int size = board.getSize();

        // 1. Nếu bàn cờ trống, đánh vào tâm bàn cờ
        int center = size / 2;
        if (board.getCell(center, center).isEmpty() && isBoardNearlyEmpty(board)) {
            return new Move(center, center, aiSymbol);
        }

        // 2. Thu thập các ô ứng viên (ô trống nằm trong bán kính 2 quanh các ô đã có quân)
        List<int[]> candidates = getCandidateCells(board, 2);
        if (candidates.isEmpty()) {
            // Dự phòng nếu không tìm được ứng viên
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if (board.isValidMove(r, c)) {
                        return new Move(r, c, aiSymbol);
                    }
                }
            }
            return null;
        }

        long bestScore = Long.MIN_VALUE;
        List<int[]> bestMoves = new ArrayList<>();

        for (int[] pos : candidates) {
            int r = pos[0];
            int c = pos[1];

            long attackScore = evaluatePoint(board, r, c, aiSymbol);
            long defendScore = evaluatePoint(board, r, c, humanSymbol);

            long totalScore;
            if ("EASY".equalsIgnoreCase(difficulty)) {
                // Ở chế độ dễ: phòng thủ nhẹ hơn và có độ nhiễu ngẫu nhiên
                totalScore = (long) (attackScore * 0.8 + defendScore * 0.7 + random.nextInt(5000));
            } else {
                // Chế độ khó: đánh giá chuẩn xác thế cờ
                totalScore = attackScore + defendScore;
            }

            if (totalScore > bestScore) {
                bestScore = totalScore;
                bestMoves.clear();
                bestMoves.add(pos);
            } else if (totalScore == bestScore) {
                bestMoves.add(pos);
            }
        }

        // Chọn ngẫu nhiên giữa các ô có cùng điểm cao nhất để nước đi tự nhiên
        int[] chosen = bestMoves.get(random.nextInt(bestMoves.size()));
        return new Move(chosen[0], chosen[1], aiSymbol);
    }

    private boolean isBoardNearlyEmpty(Board board) {
        int count = 0;
        int size = board.getSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (!board.getCell(r, c).isEmpty()) {
                    count++;
                    if (count > 2) return false;
                }
            }
        }
        return true;
    }

    private List<int[]> getCandidateCells(Board board, int distance) {
        int size = board.getSize();
        boolean[][] visited = new boolean[size][size];
        List<int[]> list = new ArrayList<>();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (!board.getCell(r, c).isEmpty()) {
                    for (int dr = -distance; dr <= distance; dr++) {
                        for (int dc = -distance; dc <= distance; dc++) {
                            int nr = r + dr;
                            int nc = c + dc;
                            if (board.isValidCoordinate(nr, nc) && board.getCell(nr, nc).isEmpty() && !visited[nr][nc]) {
                                visited[nr][nc] = true;
                                list.add(new int[]{nr, nc});
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * Đánh giá thế cờ khi đặt quân 'targetSymbol' vào vị trí (r, c).
     */
    private long evaluatePoint(Board board, int r, int c, char targetSymbol) {
        long score = 0;
        boolean isAttack = (targetSymbol == aiSymbol);

        // 4 hướng: Ngang, Dọc, Chéo chính, Chéo phụ
        int[][] directions = {
            {0, 1},
            {1, 0},
            {1, 1},
            {1, -1}
        };

        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];

            int consecutive = 1;
            int openEnds = 0;

            // Hướng tới
            int stepF = 1;
            while (true) {
                int nr = r + dr * stepF;
                int nc = c + dc * stepF;
                if (board.isValidCoordinate(nr, nc)) {
                    if (board.getCell(nr, nc).getValue() == targetSymbol) {
                        consecutive++;
                        stepF++;
                    } else if (board.getCell(nr, nc).isEmpty()) {
                        openEnds++;
                        break;
                    } else {
                        break; // Bị đối thủ chặn
                    }
                } else {
                    break; // Ra ngoài mép bàn cờ
                }
            }

            // Hướng lui
            int stepB = 1;
            while (true) {
                int nr = r - dr * stepB;
                int nc = c - dc * stepB;
                if (board.isValidCoordinate(nr, nc)) {
                    if (board.getCell(nr, nc).getValue() == targetSymbol) {
                        consecutive++;
                        stepB++;
                    } else if (board.getCell(nr, nc).isEmpty()) {
                        openEnds++;
                        break;
                    } else {
                        break; // Bị đối thủ chặn
                    }
                } else {
                    break; // Ra ngoài mép bàn cờ
                }
            }

            // Tính điểm cho đường này
            score += calculateLineScore(consecutive, openEnds, isAttack);
        }

        return score;
    }

    private long calculateLineScore(int consecutive, int openEnds, boolean isAttack) {
        if (openEnds == 0 && consecutive < 5) {
            return 0; // Bị chặn 2 đầu và chưa đủ 5 con -> vô nghĩa
        }

        if (isAttack) {
            if (consecutive >= 5) return ATK_FIVE;
            if (consecutive == 4) {
                return (openEnds == 2) ? ATK_OPEN_FOUR : ATK_CLOSED_FOUR;
            }
            if (consecutive == 3) {
                return (openEnds == 2) ? ATK_OPEN_THREE : ATK_CLOSED_THREE;
            }
            if (consecutive == 2) {
                return (openEnds == 2) ? ATK_OPEN_TWO : ATK_CLOSED_TWO;
            }
            return 10;
        } else {
            // Phòng thủ
            if (consecutive >= 5) return DEF_FIVE;
            if (consecutive == 4) {
                return (openEnds == 2) ? DEF_OPEN_FOUR : DEF_CLOSED_FOUR;
            }
            if (consecutive == 3) {
                return (openEnds == 2) ? DEF_OPEN_THREE : DEF_CLOSED_THREE;
            }
            if (consecutive == 2) {
                return (openEnds == 2) ? DEF_OPEN_TWO : DEF_CLOSED_TWO;
            }
            return 10;
        }
    }
}
