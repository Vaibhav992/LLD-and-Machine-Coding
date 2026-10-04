package com.machine_coding_round.splitwise.mylearning.split;

import com.machine_coding_round.splitwise.mylearning.exception.InvalidSplitException;
import com.machine_coding_round.splitwise.mylearning.model.PercentageSplit;
import com.machine_coding_round.splitwise.mylearning.model.Split;

import java.util.List;

public class PercentageSplitStrategy implements SplitStrategy {

    private static final double EPS = 0.01;

    @Override
    public void validate(List<Split> splits, double totalAmount) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("Percentage split needs at least one participant");
        }
        double percentSum = 0;
        for (Split split : splits) {
            if (!(split instanceof PercentageSplit)) {
                throw new InvalidSplitException("Percentage split requires PercentageSplit objects");
            }
            percentSum += ((PercentageSplit) split).getPercentage();
        }
        if (Math.abs(percentSum - 100.0) > EPS) {
            throw new InvalidSplitException(
                    "Percentages must sum to 100 but got " + percentSum);
        }
    }

    @Override
    public void calculateSplits(List<Split> splits, double totalAmount) {
        double allocated = 0;
        for (int i = 0; i < splits.size() - 1; i++) {
            PercentageSplit ps = (PercentageSplit) splits.get(i);
            double amount = Math.round(totalAmount * ps.getPercentage()) / 100.0;
            ps.setAmount(amount);
            allocated += amount;
        }
        splits.get(splits.size() - 1).setAmount(
                Math.round((totalAmount - allocated) * 100.0) / 100.0);
    }
}
