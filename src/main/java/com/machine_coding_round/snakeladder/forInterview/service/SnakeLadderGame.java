package com.machine_coding_round.snakeladder.forInterview.service;

import com.machine_coding_round.snakeladder.forInterview.board.Board;
import com.machine_coding_round.snakeladder.forInterview.dice.Dice;
import com.machine_coding_round.snakeladder.forInterview.model.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Interview version: exact win, queue turns, Strategy dice.
 * No GameStatus enum / CrookedDice / chained-jump loops — mention as extensions.
 */
public class SnakeLadderGame {

    private final Board board;
    private final Dice dice;
    private final Queue<Player> turns = new LinkedList<>();
    private Player winner;

    public SnakeLadderGame(Board board, Dice dice, List<Player> players) {
        this.board = board;
        this.dice = dice;
        this.turns.addAll(players);
    }

    public String playTurn() {
        if (winner != null) throw new IllegalStateException("Game already finished");

        Player current = turns.poll();
        int roll = dice.roll();
        int from = current.getPosition();
        int next = from + roll;

        if (next > board.getSize()) {
            turns.offer(current);
            return current.getName() + " rolled " + roll + " — stay at " + from;
        }

        int landed = board.resolve(next);
        current.setPosition(landed);

        String msg = current.getName() + " rolled " + roll + " (" + from + " -> " + next
                + (landed != next ? " jump->" + landed : "") + ")";

        if (landed == board.getSize()) {
            winner = current;
            return msg + " — WINS!";
        }

        turns.offer(current);
        return msg;
    }

    public Player playUntilWinner(int maxTurns) {
        int t = 0;
        while (winner == null && t++ < maxTurns) {
            System.out.println(playTurn());
        }
        return winner;
    }

    public Player getWinner() { return winner; }

    public List<Player> getPlayers() {
        return new ArrayList<>(turns);
    }
}
