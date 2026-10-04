package com.machine_coding_round.snakeladder.mylearning;

import com.machine_coding_round.snakeladder.mylearning.board.Board;
import com.machine_coding_round.snakeladder.mylearning.dice.NormalDice;
import com.machine_coding_round.snakeladder.mylearning.model.Ladder;
import com.machine_coding_round.snakeladder.mylearning.model.Player;
import com.machine_coding_round.snakeladder.mylearning.model.Snake;
import com.machine_coding_round.snakeladder.mylearning.service.SnakeLadderGame;

import java.util.Arrays;
import java.util.List;

public class SnakeLadderDemo {
    public static void main(String[] args) {
        List<Snake> snakes = Arrays.asList(
                new Snake(99, 10),
                new Snake(95, 50),
                new Snake(80, 42),
                new Snake(62, 22),
                new Snake(36, 6)
        );

        List<Ladder> ladders = Arrays.asList(
                new Ladder(2, 38),
                new Ladder(7, 14),
                new Ladder(15, 45),
                new Ladder(28, 76),
                new Ladder(51, 87)
        );

        Board board = new Board(100, snakes, ladders);

        List<Player> players = Arrays.asList(
                new Player("p1", "Alice"),
                new Player("p2", "Bob"),
                new Player("p3", "Charlie")
        );

        // seeded dice so the demo is reproducible
        SnakeLadderGame game = new SnakeLadderGame(board, new NormalDice(6, 42L), players, true);

        System.out.println("=== Snake & Ladder (learning) ===");
        System.out.println("Board size: " + board.getSize());
        System.out.println("Jumps: " + board.getJumps());
        System.out.println();

        Player winner = game.playUntilWinner(500);
        System.out.println();
        System.out.println("Winner: " + winner.getName() + " at " + winner.getPosition());
        System.out.println("Final positions:");
        for (Player p : game.getPlayers()) {
            System.out.println("  " + p.getName() + " -> " + p.getPosition());
        }
    }
}
