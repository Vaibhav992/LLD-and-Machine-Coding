package com.machine_coding_round.splitwise.forInterview.split;

import com.machine_coding_round.splitwise.forInterview.exception.InvalidSplitException;
import com.machine_coding_round.splitwise.forInterview.model.Split;

import java.util.List;

public class EqualSplitStrategy implements SplitStrategy {
    @Override
    public void validate(List<Split> splits, double totalAmount) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("Need at least one participant");
        }
    }

    @Override
    public void calculateSplits(List<Split> splits, double totalAmount) {
        int n = splits.size();
        double share = Math.floor((totalAmount / n) * 100.0) / 100.0;
        double allocated = 0;
        for (int i = 0; i < n - 1; i++) {
            splits.get(i).setAmount(share);
            allocated += share;
        }
        splits.get(n - 1).setAmount(Math.round((totalAmount - allocated) * 100.0) / 100.0);
    }
}
