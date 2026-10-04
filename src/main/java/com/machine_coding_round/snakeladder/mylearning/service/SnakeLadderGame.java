package com.machine_coding_round.snakeladder.mylearning.service;

import com.machine_coding_round.snakeladder.mylearning.board.Board;
import com.machine_coding_round.snakeladder.mylearning.dice.Dice;
import com.machine_coding_round.snakeladder.mylearning.model.GameStatus;
import com.machine_coding_round.snakeladder.mylearning.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SnakeLadderGame {

    private final Board board;
    private final Dice dice;
    private final Queue<Player> turnOrder;
    private final List<Player> players;
    private final boolean exactWinRequired;
    private GameStatus status;
    private Player winner;

    public SnakeLadderGame(Board board, Dice dice, List<Player> players) {
        this(board, dice, players, true);
    }

    public SnakeLadderGame(Board board, Dice dice, List<Player> players, boolean exactWinRequired) {
        if (board == null || dice == null) {
            throw new IllegalArgumentException("Board and dice are required");
        }
        if (players == null || players.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 players");
        }
        this.board = board;
        this.dice = dice;
        this.players = new ArrayList<>(players);
        this.turnOrder = new LinkedList<>(players);
        this.exactWinRequired = exactWinRequired;
        this.status = GameStatus.NOT_STARTED;
    }

    /**
     * Game is turn-based: one coarse lock on the game object is the right model.
     * Concurrent playTurn() without sync could poll the same player twice.
     */
    public synchronized void start() {
        if (status != GameStatus.NOT_STARTED) {
            throw new IllegalStateException("Game already started or finished");
        }
        status = GameStatus.IN_PROGRESS;
    }

    /**
     * Play one turn for the current player. Returns a human-readable turn summary.
     */
    public synchronized String playTurn() {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
        }

        Player current = turnOrder.poll();
        int roll = dice.roll();
        int from = current.getPosition();
        int tentative = from + roll;

        StringBuilder log = new StringBuilder();
        log.append(current.getName())
                .append(" rolled ")
                .append(roll)
                .append(" (")
                .append(from)
                .append(" -> ");

        if (tentative > board.getSize()) {
            if (exactWinRequired) {
                log.append(from).append(") — need exact win, stay");
                turnOrder.offer(current);
                return log.toString();
            }
            tentative = board.getSize();
        }

        int afterJump = board.resolvePosition(tentative);
        current.setPosition(afterJump);

        log.append(tentative);
        if (afterJump != tentative) {
            log.append(" jump-> ").append(afterJump);
        }
        log.append(")");

        if (afterJump == board.getSize()) {
            status = GameStatus.FINISHED;
            winner = current;
            log.append(" — WINS!");
            return log.toString();
        }

        turnOrder.offer(current);
        return log.toString();
    }

    /** Play until someone wins or maxTurns reached (safety). */
    public Player playUntilWinner(int maxTurns) {
        synchronized (this) {
            if (status == GameStatus.NOT_STARTED) {
                start();
            }
        }
        int turns = 0;
        while (true) {
            String line;
            synchronized (this) {
                if (status != GameStatus.IN_PROGRESS || turns >= maxTurns) {
                    break;
                }
                line = playTurn();
                turns++;
            }
            System.out.println(line);
        }
        synchronized (this) {
            if (winner == null) {
                throw new IllegalStateException("No winner within " + maxTurns + " turns");
            }
            return winner;
        }
    }

    public synchronized GameStatus getStatus() {
        return status;
    }

    public synchronized Player getWinner() {
        return winner;
    }

    public synchronized List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Board getBoard() {
        return board;
    }
}

