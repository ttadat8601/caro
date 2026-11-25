package com.caro.model;

import java.awt.Color;
import java.io.Serializable;

/**
 * Lớp đại diện cho người chơi (Người hoặc Máy AI).
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private char symbol;
    private Color color;
    private int score;
    private boolean isAI;

    public Player(String name, char symbol, Color color, boolean isAI) {
        this.name = name;
        this.symbol = symbol;
        this.color = color;
        this.score = 0;
        this.isAI = isAI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getSymbol() {
        return symbol;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore() {
        this.score++;
    }

    public void resetScore() {
        this.score = 0;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean AI) {
        isAI = AI;
    }
}
