package com.machine_coding_round.snakeladder.mylearning.model;

import lombok.Getter;

@Getter
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
}
