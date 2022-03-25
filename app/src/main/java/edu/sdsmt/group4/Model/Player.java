package edu.sdsmt.group4.Model;

public class Player {
    private final String name;
    private final int id;
    private int score = 0;

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public int getScore() {
        return this.score;
    }

    public void incScore(int add) {
        this.score += add;
    }
}
