package com.machine_coding_round.splitwise.forInterview.service;

import com.machine_coding_round.splitwise.forInterview.exception.InvalidSplitException;
import com.machine_coding_round.splitwise.forInterview.exception.UserNotFoundException;
import com.machine_coding_round.splitwise.forInterview.model.Expense;
import com.machine_coding_round.splitwise.forInterview.model.Group;
import com.machine_coding_round.splitwise.forInterview.model.Split;
import com.machine_coding_round.splitwise.forInterview.model.SplitType;
import com.machine_coding_round.splitwise.forInterview.model.User;
import com.machine_coding_round.splitwise.forInterview.split.EqualSplitStrategy;
import com.machine_coding_round.splitwise.forInterview.split.ExactSplitStrategy;
import com.machine_coding_round.splitwise.forInterview.split.PercentageSplitStrategy;
import com.machine_coding_round.splitwise.forInterview.split.SplitStrategy;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interview version: no Singleton, no Observer, no concurrency.
 * Still has Strategy for splits + pairwise BalanceSheet.
 */
public class SplitwiseService {

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Group> groups = new HashMap<>();
    private final List<Expense> expenses = new ArrayList<>();
    private final Map<SplitType, SplitStrategy> strategies = new EnumMap<>(SplitType.class);
    private final BalanceSheet balanceSheet = new BalanceSheet();

    public SplitwiseService() {
        strategies.put(SplitType.EQUAL, new EqualSplitStrategy());
        strategies.put(SplitType.EXACT, new ExactSplitStrategy());
        strategies.put(SplitType.PERCENTAGE, new PercentageSplitStrategy());
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public Group createGroup(String id, String name) {
        Group g = new Group(id, name);
        groups.put(id, g);
        return g;
    }

    public void addMemberToGroup(String groupId, String userId) {
        requireUser(userId);
        groups.get(groupId).addMember(userId);
    }

    public Expense addExpense(String id, double amount, String description,
                              String paidByUserId, SplitType type, List<Split> splits) {
        requireUser(paidByUserId);
        for (Split s : splits) requireUser(s.getUserId());

        SplitStrategy strategy = strategies.get(type);
        strategy.validate(splits, amount);
        strategy.calculateSplits(splits, amount);

        Expense expense = new Expense(id, amount, description, paidByUserId, type, splits);
        expenses.add(expense);

        for (Split s : expense.getSplits()) {
            if (!s.getUserId().equals(paidByUserId)) {
                balanceSheet.updateBalance(s.getUserId(), paidByUserId, s.getAmount());
            }
        }
        return expense;
    }

    public void settleUp(String from, String to, double amount) {
        requireUser(from);
        requireUser(to);
        if (amount <= 0) throw new InvalidSplitException("Settlement must be positive");
        double owed = balanceSheet.getBalance(from, to);
        if (amount - owed > 0.01) {
            throw new InvalidSplitException("Cannot settle more than owed: " + owed);
        }
        balanceSheet.settleUp(from, to, amount);
    }

    public double getBalance(String a, String b) {
        return balanceSheet.getBalance(a, b);
    }

    public Map<String, Double> getBalancesForUser(String userId) {
        return balanceSheet.getBalancesForUser(userId);
    }

    private void requireUser(String id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("User not found: " + id);
        }
    }
}
