package com.machine_coding_round.snakeladder.mylearning.dice;

import java.util.Random;

/**
 * Always returns an even number (2,4,6 for a 6-face die).
 * Common interview extension to show Strategy is justified.
 */
public class CrookedDice implements Dice {
    private final Random random;

    public CrookedDice() {
        this.random = new Random();
    }

    public CrookedDice(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public int roll() {
        int[] even = {2, 4, 6};
        return even[random.nextInt(even.length)];
    }
}
