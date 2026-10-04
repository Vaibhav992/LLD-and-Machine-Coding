package com.machine_coding_round.snakeladder.mylearning.dice;

import java.util.Random;

public class NormalDice implements Dice {
    private final int faces;
    private final Random random;

    public NormalDice() {
        this(6);
    }

    public NormalDice(int faces) {
        if (faces < 2) {
            throw new IllegalArgumentException("Dice must have at least 2 faces");
        }
        this.faces = faces;
        this.random = new Random();
    }

    /** Deterministic dice for tests. */
    public NormalDice(int faces, long seed) {
        this.faces = faces;
        this.random = new Random(seed);
    }

    @Override
    public int roll() {
        return random.nextInt(faces) + 1;
    }
}
