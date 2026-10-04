package com.machine_coding_round.splitwise.forInterview;

import com.machine_coding_round.splitwise.forInterview.model.EqualSplit;
import com.machine_coding_round.splitwise.forInterview.model.ExactSplit;
import com.machine_coding_round.splitwise.forInterview.model.PercentageSplit;
import com.machine_coding_round.splitwise.forInterview.model.Split;
import com.machine_coding_round.splitwise.forInterview.model.SplitType;
import com.machine_coding_round.splitwise.forInterview.model.User;
import com.machine_coding_round.splitwise.forInterview.service.SplitwiseService;

import java.util.Arrays;
import java.util.List;

/** 45-min interview happy-path demo. */
public class InterviewDemo {
    public static void main(String[] args) {
        SplitwiseService service = new SplitwiseService();

        service.addUser(new User("u1", "Alice", "a@x.com"));
        service.addUser(new User("u2", "Bob", "b@x.com"));
        service.addUser(new User("u3", "Charlie", "c@x.com"));

        // Equal: Alice pays 300, 3 people => each 100
        List<Split> equal = Arrays.asList(new EqualSplit("u1"), new EqualSplit("u2"), new EqualSplit("u3"));
        service.addExpense("e1", 300, "Dinner", "u1", SplitType.EQUAL, equal);
        System.out.println("Bob owes Alice: " + service.getBalance("u2", "u1"));       // 100
        System.out.println("Charlie owes Alice: " + service.getBalance("u3", "u1"));   // 100

        // Exact: Bob pays 500 (Alice 100, Bob 200, Charlie 200)
        List<Split> exact = Arrays.asList(
                new ExactSplit("u1", 100), new ExactSplit("u2", 200), new ExactSplit("u3", 200));
        service.addExpense("e2", 500, "Hotel", "u2", SplitType.EXACT, exact);

        // Percentage: Charlie pays 400 (50/25/25)
        List<Split> pct = Arrays.asList(
                new PercentageSplit("u1", 50), new PercentageSplit("u2", 25), new PercentageSplit("u3", 25));
        service.addExpense("e3", 400, "Cab", "u3", SplitType.PERCENTAGE, pct);

        System.out.println("Alice owes Charlie before settle: " + service.getBalance("u1", "u3"));
        service.settleUp("u1", "u3", 50);
        System.out.println("Alice owes Charlie after settle: " + service.getBalance("u1", "u3"));
        System.out.println("Alice balances: " + service.getBalancesForUser("u1"));
    }
}
