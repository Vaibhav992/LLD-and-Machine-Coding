package com.machine_coding_round.snakeladder.forInterview.dice;

import java.util.Random;

public class NormalDice implements Dice {
    private final Random random;

    public NormalDice() {
        this.random = new Random();
    }

    public NormalDice(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public int roll() {
        return random.nextInt(6) + 1;
    }
}
