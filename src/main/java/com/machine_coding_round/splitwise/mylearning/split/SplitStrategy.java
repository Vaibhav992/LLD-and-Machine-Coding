package com.machine_coding_round.splitwise.mylearning.split;

import com.machine_coding_round.splitwise.mylearning.model.Split;

import java.util.List;

public interface SplitStrategy {
    void validate(List<Split> splits, double totalAmount);

    void calculateSplits(List<Split> splits, double totalAmount);
}
