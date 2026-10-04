package com.machine_coding_round.snakeladder.forInterview;

import com.machine_coding_round.snakeladder.forInterview.board.Board;
import com.machine_coding_round.snakeladder.forInterview.dice.NormalDice;
import com.machine_coding_round.snakeladder.forInterview.model.Player;
import com.machine_coding_round.snakeladder.forInterview.service.SnakeLadderGame;

import java.util.Arrays;

public class InterviewDemo {
    public static void main(String[] args) {
        Board board = new Board(100);
        board.addSnake(99, 10);
        board.addSnake(95, 50);
        board.addSnake(80, 42);
        board.addLadder(2, 38);
        board.addLadder(15, 45);
        board.addLadder(28, 76);

        SnakeLadderGame game = new SnakeLadderGame(
                board,
                new NormalDice(42L),
                Arrays.asList(new Player("Alice"), new Player("Bob"))
        );

        System.out.println("=== Snake & Ladder (interview) ===");
        Player winner = game.playUntilWinner(300);
        System.out.println("Winner: " + (winner != null ? winner.getName() : "none"));
    }
}
