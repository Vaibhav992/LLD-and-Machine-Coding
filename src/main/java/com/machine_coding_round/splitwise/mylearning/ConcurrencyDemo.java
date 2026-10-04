package com.machine_coding_round.splitwise.mylearning;

import com.machine_coding_round.splitwise.mylearning.model.EqualSplit;
import com.machine_coding_round.splitwise.mylearning.model.Split;
import com.machine_coding_round.splitwise.mylearning.model.SplitType;
import com.machine_coding_round.splitwise.mylearning.model.User;
import com.machine_coding_round.splitwise.mylearning.service.SplitwiseService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates concurrent addExpense / settleUp on the same user pair.
 * With locks, final balance stays consistent (no lost updates).
 */
public class ConcurrencyDemo {
    public static void main(String[] args) throws InterruptedException {
        SplitwiseService.resetInstance();
        SplitwiseService service = SplitwiseService.getInstance();

        service.addUser(new User("u1", "Alice", "a@x.com", "1"));
        service.addUser(new User("u2", "Bob", "b@x.com", "2"));

        // seed: Bob owes Alice 0 initially
        int threads = 8;
        int expensesPerThread = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicInteger idGen = new AtomicInteger(1);

        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                for (int i = 0; i < expensesPerThread; i++) {
                    String eid = "e" + idGen.getAndIncrement();
                    List<Split> splits = Arrays.asList(new EqualSplit("u1"), new EqualSplit("u2"));
                    // Alice pays 20 → Bob owes +10 each time
                    service.addExpense(eid, 20.0, "item", "u1", SplitType.EQUAL, splits, null);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        double bobOwesAlice = service.getBalance("u2", "u1");
        double expected = threads * expensesPerThread * 10.0; // 8*50*10 = 4000

        System.out.println("=== Splitwise Concurrency Demo ===");
        System.out.println("Threads: " + threads + ", expenses/thread: " + expensesPerThread);
        System.out.println("Expected Bob→Alice: " + expected);
        System.out.println("Actual   Bob→Alice: " + bobOwesAlice);
        System.out.println(Math.abs(bobOwesAlice - expected) < 0.01 ? "PASS — no lost updates" : "FAIL — race detected");
    }
}
