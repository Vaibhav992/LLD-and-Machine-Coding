package com.machine_coding_round.splitwise.forInterview.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Expense {
    private final String id;
    private final double amount;
    private final String description;
    private final String paidByUserId;
    private final SplitType splitType;
    private final List<Split> splits;

    public Expense(String id, double amount, String description, String paidByUserId,
                   SplitType splitType, List<Split> splits) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.paidByUserId = paidByUserId;
        this.splitType = splitType;
        this.splits = new ArrayList<>(splits);
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getPaidByUserId() { return paidByUserId; }
    public SplitType getSplitType() { return splitType; }
    public List<Split> getSplits() { return Collections.unmodifiableList(splits); }
}
