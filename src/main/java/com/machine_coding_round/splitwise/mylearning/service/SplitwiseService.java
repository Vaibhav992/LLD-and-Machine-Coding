package com.machine_coding_round.splitwise.mylearning.service;

import com.machine_coding_round.splitwise.mylearning.exception.GroupNotFoundException;
import com.machine_coding_round.splitwise.mylearning.exception.InvalidSplitException;
import com.machine_coding_round.splitwise.mylearning.exception.UserNotFoundException;
import com.machine_coding_round.splitwise.mylearning.model.Expense;
import com.machine_coding_round.splitwise.mylearning.model.Group;
import com.machine_coding_round.splitwise.mylearning.model.Split;
import com.machine_coding_round.splitwise.mylearning.model.SplitType;
import com.machine_coding_round.splitwise.mylearning.model.User;
import com.machine_coding_round.splitwise.mylearning.observer.ExpenseObserver;
import com.machine_coding_round.splitwise.mylearning.split.EqualSplitStrategy;
import com.machine_coding_round.splitwise.mylearning.split.ExactSplitStrategy;
import com.machine_coding_round.splitwise.mylearning.split.PercentageSplitStrategy;
import com.machine_coding_round.splitwise.mylearning.split.SplitStrategy;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitwiseService {

    private static volatile SplitwiseService instance;

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Group> groups = new HashMap<>();
    private final Map<String, Expense> expenses = new HashMap<>();
    private final Map<SplitType, SplitStrategy> strategies = new EnumMap<>(SplitType.class);
    private final BalanceSheet balanceSheet = new BalanceSheet();
    private final List<ExpenseObserver> observers = new ArrayList<>();

    private SplitwiseService() {
        strategies.put(SplitType.EQUAL, new EqualSplitStrategy());
        strategies.put(SplitType.EXACT, new ExactSplitStrategy());
        strategies.put(SplitType.PERCENTAGE, new PercentageSplitStrategy());
    }

    public static SplitwiseService getInstance() {
        if (instance == null) {
            synchronized (SplitwiseService.class) {
                if (instance == null) {
                    instance = new SplitwiseService();
                }
            }
        }
        return instance;
    }

    /** For tests / demos that need a clean slate. */
    public static synchronized void resetInstance() {
        instance = null;
    }

    public synchronized void addObserver(ExpenseObserver observer) {
        observers.add(observer);
    }

    public synchronized void addUser(User user) {
        users.put(user.getId(), user);
    }

    public synchronized Group createGroup(String id, String name) {
        Group group = new Group(id, name);
        groups.put(id, group);
        return group;
    }

    public synchronized void addMemberToGroup(String groupId, String userId) {
        requireUser(userId);
        requireGroup(groupId).addMember(userId);
    }

    public synchronized Expense addExpense(String id, double amount, String description,
                                           String paidByUserId, SplitType splitType,
                                           List<Split> splits, String groupId) {
        requireUser(paidByUserId);
        if (amount <= 0) {
            throw new InvalidSplitException("Expense amount must be positive");
        }

        SplitStrategy strategy = strategies.get(splitType);
        if (strategy == null) {
            throw new InvalidSplitException("Unsupported split type: " + splitType);
        }

        for (Split split : splits) {
            requireUser(split.getUserId());
        }

        if (groupId != null) {
            Group group = requireGroup(groupId);
            requireUser(paidByUserId);
            if (!group.hasMember(paidByUserId)) {
                throw new InvalidSplitException("Payer is not a member of group " + groupId);
            }
            for (Split split : splits) {
                if (!group.hasMember(split.getUserId())) {
                    throw new InvalidSplitException(
                            "Participant " + split.getUserId() + " is not in group " + groupId);
                }
            }
        }

        strategy.validate(splits, amount);
        strategy.calculateSplits(splits, amount);

        Expense expense = new Expense(id, amount, description, paidByUserId, splitType, splits, groupId);
        expenses.put(id, expense);

        if (groupId != null) {
            groups.get(groupId).addExpense(id);
        }

        for (Split split : expense.getSplits()) {
            if (!split.getUserId().equals(paidByUserId)) {
                balanceSheet.updateBalance(split.getUserId(), paidByUserId, split.getAmount());
            }
        }

        for (ExpenseObserver observer : observers) {
            observer.onExpenseAdded(expense);
        }
        return expense;
    }

    public synchronized void settleUp(String fromUserId, String toUserId, double amount) {
        requireUser(fromUserId);
        requireUser(toUserId);
        if (fromUserId.equals(toUserId)) {
            throw new InvalidSplitException("Cannot settle with yourself");
        }
        if (amount <= 0) {
            throw new InvalidSplitException("Settlement amount must be positive");
        }

        double owed = balanceSheet.getBalance(fromUserId, toUserId);
        if (owed <= 0) {
            throw new InvalidSplitException(fromUserId + " does not owe " + toUserId);
        }
        if (amount - owed > 0.01) {
            throw new InvalidSplitException(
                    "Cannot settle more than owed. Owed=" + owed + ", tried=" + amount);
        }

        balanceSheet.settleUp(fromUserId, toUserId, amount);
        for (ExpenseObserver observer : observers) {
            observer.onSettlement(fromUserId, toUserId, amount);
        }
    }

    public synchronized double getBalance(String userId1, String userId2) {
        requireUser(userId1);
        requireUser(userId2);
        return balanceSheet.getBalance(userId1, userId2);
    }

    public synchronized Map<String, Double> getBalancesForUser(String userId) {
        requireUser(userId);
        return balanceSheet.getBalancesForUser(userId);
    }

    private User requireUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + userId);
        }
        return user;
    }

    private Group requireGroup(String groupId) {
        Group group = groups.get(groupId);
        if (group == null) {
            throw new GroupNotFoundException("Group not found: " + groupId);
        }
        return group;
    }
}
