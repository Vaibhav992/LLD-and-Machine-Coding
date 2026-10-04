package com.machine_coding_round.snakeladder.mylearning.board;

import com.machine_coding_round.snakeladder.mylearning.model.Ladder;
import com.machine_coding_round.snakeladder.mylearning.model.Snake;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private final int size;
    private final Map<Integer, Integer> jumps; // start -> end (snake or ladder)

    public Board(int size, List<Snake> snakes, List<Ladder> ladders) {
        if (size <= 1) {
            throw new IllegalArgumentException("Board size must be > 1");
        }
        this.size = size;
        this.jumps = new HashMap<>();
        placeSnakes(snakes);
        placeLadders(ladders);
    }

    private void placeSnakes(List<Snake> snakes) {
        if (snakes == null) return;
        for (Snake snake : snakes) {
            validateCell(snake.getHead());
            validateCell(snake.getTail());
            if (snake.getHead() == size) {
                throw new IllegalArgumentException("Snake cannot start at winning cell " + size);
            }
            if (jumps.containsKey(snake.getHead())) {
                throw new IllegalArgumentException("Jump already exists at " + snake.getHead());
            }
            jumps.put(snake.getHead(), snake.getTail());
        }
    }

    private void placeLadders(List<Ladder> ladders) {
        if (ladders == null) return;
        for (Ladder ladder : ladders) {
            validateCell(ladder.getStart());
            validateCell(ladder.getEnd());
            if (ladder.getStart() == size) {
                throw new IllegalArgumentException("Ladder cannot start at winning cell " + size);
            }
            if (jumps.containsKey(ladder.getStart())) {
                throw new IllegalArgumentException("Jump already exists at " + ladder.getStart());
            }
            jumps.put(ladder.getStart(), ladder.getEnd());
        }
    }

    private void validateCell(int cell) {
        if (cell < 1 || cell > size) {
            throw new IllegalArgumentException("Cell out of board: " + cell);
        }
    }

    public int getSize() {
        return size;
    }

    /**
     * Apply snake/ladder if present. Supports chained jumps
     * (land on ladder that ends on a snake head, etc.) with a safety limit.
     */
    public int resolvePosition(int position) {
        int guard = 0;
        while (jumps.containsKey(position)) {
            position = jumps.get(position);
            if (++guard > size) {
                throw new IllegalStateException("Infinite jump cycle detected on board");
            }
        }
        return position;
    }

    public Map<Integer, Integer> getJumps() {
        return Collections.unmodifiableMap(jumps);
    }
}
