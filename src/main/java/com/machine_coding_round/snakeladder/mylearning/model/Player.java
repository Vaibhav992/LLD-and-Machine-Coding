package com.machine_coding_round.snakeladder.mylearning.model;

public class Player {
    private final String id;
    private final String name;
    private int position;

    public Player(String id, String name) {
        if (id == null || name == null) {
            throw new IllegalArgumentException("Player id and name cannot be null");
        }
        this.id = id;
        this.name = name;
        this.position = 0; // off the board / start
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return name + "@" + position;
    }
}
