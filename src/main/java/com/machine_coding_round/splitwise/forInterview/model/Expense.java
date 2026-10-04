package com.machine_coding_round.splitwise.forInterview.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Expense {
    private final String id;
    private final double amount;
    private final String description;
    private final String paidByUserId;
    private final SplitType splitType;
    @Getter(lombok.AccessLevel.NONE)
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

    public List<Split> getSplits() {
        return Collections.unmodifiableList(splits);
    }
}
