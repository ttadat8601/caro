package com.caro.model;

/**
 * Định nghĩa các chế độ chơi của game Caro.
 */
public enum GameMode {
    PVP("Người đấu Người (2 người chơi)"),
    PVE("Người đấu Máy (Chơi với AI)");

    private final String description;

    GameMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
