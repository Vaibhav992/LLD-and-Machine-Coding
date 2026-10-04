package com.machine_coding_round.snakeladder.forInterview.board;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private final int size;
    private final Map<Integer, Integer> snakes = new HashMap<>();  // head -> tail
    private final Map<Integer, Integer> ladders = new HashMap<>(); // start -> end

    public Board(int size) {
        this.size = size;
    }

    public void addSnake(int head, int tail) {
        if (head <= tail) throw new IllegalArgumentException("Snake head must be > tail");
        snakes.put(head, tail);
    }

    public void addLadder(int start, int end) {
        if (start >= end) throw new IllegalArgumentException("Ladder start must be < end");
        ladders.put(start, end);
    }

    public int getSize() { return size; }

    public int resolve(int position) {
        if (snakes.containsKey(position)) return snakes.get(position);
        if (ladders.containsKey(position)) return ladders.get(position);
        return position;
    }
}
