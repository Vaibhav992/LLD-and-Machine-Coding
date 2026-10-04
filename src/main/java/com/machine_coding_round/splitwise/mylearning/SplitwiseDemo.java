package com.machine_coding_round.splitwise.mylearning;

import com.machine_coding_round.splitwise.mylearning.model.EqualSplit;
import com.machine_coding_round.splitwise.mylearning.model.ExactSplit;
import com.machine_coding_round.splitwise.mylearning.model.PercentageSplit;
import com.machine_coding_round.splitwise.mylearning.model.Split;
import com.machine_coding_round.splitwise.mylearning.model.SplitType;
import com.machine_coding_round.splitwise.mylearning.model.User;
import com.machine_coding_round.splitwise.mylearning.observer.EmailNotificationObserver;
import com.machine_coding_round.splitwise.mylearning.service.SplitwiseService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SplitwiseDemo {
    public static void main(String[] args) {
        SplitwiseService.resetInstance();
        SplitwiseService service = SplitwiseService.getInstance();
        service.addObserver(new EmailNotificationObserver());

        User alice = new User("u1", "Alice", "alice@email.com", "1111111111");
        User bob = new User("u2", "Bob", "bob@email.com", "2222222222");
        User charlie = new User("u3", "Charlie", "charlie@email.com", "3333333333");

        service.addUser(alice);
        service.addUser(bob);
        service.addUser(charlie);

        service.createGroup("g1", "Trip to Goa");
        service.addMemberToGroup("g1", "u1");
        service.addMemberToGroup("g1", "u2");
        service.addMemberToGroup("g1", "u3");

        System.out.println("===== SCENARIO 1: Equal Split =====");
        List<Split> equalSplits = Arrays.asList(
                new EqualSplit("u1"),
                new EqualSplit("u2"),
                new EqualSplit("u3")
        );
        service.addExpense("e1", 300.00, "Dinner", "u1",
                SplitType.EQUAL, equalSplits, "g1");

        System.out.println("Balances after dinner:");
        System.out.printf("  Bob owes Alice: $%.2f%n", service.getBalance("u2", "u1"));
        System.out.printf("  Charlie owes Alice: $%.2f%n", service.getBalance("u3", "u1"));

        System.out.println("\n===== SCENARIO 2: Exact Split =====");
        List<Split> exactSplits = Arrays.asList(
                new ExactSplit("u1", 100.00),
                new ExactSplit("u2", 200.00),
                new ExactSplit("u3", 200.00)
        );
        service.addExpense("e2", 500.00, "Hotel", "u2",
                SplitType.EXACT, exactSplits, "g1");

        System.out.println("Balances after hotel:");
        System.out.printf("  Alice -> Bob: $%.2f%n", service.getBalance("u1", "u2"));
        System.out.printf("  Charlie -> Bob: $%.2f%n", service.getBalance("u3", "u2"));

        System.out.println("\n===== SCENARIO 3: Percentage Split =====");
        List<Split> percentSplits = Arrays.asList(
                new PercentageSplit("u1", 50),
                new PercentageSplit("u2", 25),
                new PercentageSplit("u3", 25)
        );
        service.addExpense("e3", 400.00, "Activities", "u3",
                SplitType.PERCENTAGE, percentSplits, "g1");

        System.out.println("Balances after activities:");
        System.out.printf("  Alice -> Charlie: $%.2f%n", service.getBalance("u1", "u3"));
        System.out.printf("  Bob -> Charlie: $%.2f%n", service.getBalance("u2", "u3"));

        System.out.println("\n===== SCENARIO 4: Settlement =====");
        System.out.println("Alice settles $50 with Charlie...");
        service.settleUp("u1", "u3", 50.00);
        System.out.printf("  Alice owes Charlie after settlement: $%.2f%n",
                service.getBalance("u1", "u3"));

        System.out.println("\n===== FINAL BALANCES =====");
        String[] userIds = {"u1", "u2", "u3"};
        String[] names = {"Alice", "Bob", "Charlie"};
        for (int i = 0; i < userIds.length; i++) {
            Map<String, Double> balances = service.getBalancesForUser(userIds[i]);
            System.out.println(names[i] + "'s balances:");
            for (Map.Entry<String, Double> entry : balances.entrySet()) {
                String other = nameOf(entry.getKey());
                if (entry.getValue() > 0) {
                    System.out.printf("  Owes %s: $%.2f%n", other, entry.getValue());
                } else {
                    System.out.printf("  Owed by %s: $%.2f%n", other, -entry.getValue());
                }
            }
        }
    }

    private static String nameOf(String id) {
        return switch (id) {
            case "u1" -> "Alice";
            case "u2" -> "Bob";
            default -> "Charlie";
        };
    }
}
