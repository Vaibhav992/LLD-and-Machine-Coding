package com.machine_coding_round.splitwise.forInterview.split;

import com.machine_coding_round.splitwise.forInterview.model.Split;

import java.util.List;

public interface SplitStrategy {
    void validate(List<Split> splits, double totalAmount);
    void calculateSplits(List<Split> splits, double totalAmount);
}
