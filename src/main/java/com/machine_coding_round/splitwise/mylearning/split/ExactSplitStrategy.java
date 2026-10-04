package com.machine_coding_round.splitwise.mylearning.split;

import com.machine_coding_round.splitwise.mylearning.exception.InvalidSplitException;
import com.machine_coding_round.splitwise.mylearning.model.Split;

import java.util.List;

public class ExactSplitStrategy implements SplitStrategy {

    private static final double EPS = 0.01;

    @Override
    public void validate(List<Split> splits, double totalAmount) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("Exact split needs at least one participant");
        }
        double sum = 0;
        for (Split split : splits) {
            if (split.getAmount() < 0) {
                throw new InvalidSplitException("Exact split amount cannot be negative");
            }
            sum += split.getAmount();
        }
        if (Math.abs(sum - totalAmount) > EPS) {
            throw new InvalidSplitException(
                    "Exact amounts must sum to total. Expected " + totalAmount + " but got " + sum);
        }
    }

    @Override
    public void calculateSplits(List<Split> splits, double totalAmount) {
        // amounts already set by caller
    }
}
