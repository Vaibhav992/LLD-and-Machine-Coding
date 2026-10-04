package com.machine_coding_round.snakeladder.mylearning.model;

public class Ladder {
    private final int start;
    private final int end;

    public Ladder(int start, int end) {
        if (start >= end) {
            throw new IllegalArgumentException(
                    "Ladder start must be less than end: " + start + " -> " + end);
        }
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
