package com.machine_coding_round.splitwise.forInterview.split;

import com.machine_coding_round.splitwise.forInterview.exception.InvalidSplitException;
import com.machine_coding_round.splitwise.forInterview.model.PercentageSplit;
import com.machine_coding_round.splitwise.forInterview.model.Split;

import java.util.List;

public class PercentageSplitStrategy implements SplitStrategy {
    @Override
    public void validate(List<Split> splits, double totalAmount) {
        double pct = 0;
        for (Split s : splits) pct += ((PercentageSplit) s).getPercentage();
        if (Math.abs(pct - 100.0) > 0.01) {
            throw new InvalidSplitException("Percentages must sum to 100, got " + pct);
        }
    }

    @Override
    public void calculateSplits(List<Split> splits, double totalAmount) {
        double allocated = 0;
        for (int i = 0; i < splits.size() - 1; i++) {
            PercentageSplit ps = (PercentageSplit) splits.get(i);
            double amt = Math.round(totalAmount * ps.getPercentage()) / 100.0;
            ps.setAmount(amt);
            allocated += amt;
        }
        splits.get(splits.size() - 1).setAmount(Math.round((totalAmount - allocated) * 100.0) / 100.0);
    }
}
