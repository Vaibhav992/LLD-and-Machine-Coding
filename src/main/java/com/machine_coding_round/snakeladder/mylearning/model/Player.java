package com.machine_coding_round.snakeladder.mylearning.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Player {
    private final String id;
    private final String name;
    @Setter
    private int position;

    public Player(String id, String name) {
        if (id == null || name == null) {
            throw new IllegalArgumentException("Player id and name cannot be null");
        }
        this.id = id;
        this.name = name;
        this.position = 0;
    }

    @Override
    public String toString() {
        return name + "@" + position;
    }
}
