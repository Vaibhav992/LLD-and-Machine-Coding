package com.machine_coding_round.filesystem.mylearning;

import com.machine_coding_round.filesystem.mylearning.service.FileSystem;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyDemo {
    public static void main(String[] args) throws InterruptedException {
        FileSystem fs = new FileSystem();
        fs.mkdir("/concurrent");
        fs.createFile("/concurrent/log.txt");
        fs.writeFile("/concurrent/log.txt", "");

        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger failures = new AtomicInteger();

        for (int t = 0; t < threads; t++) {
            final int id = t;
            pool.submit(() -> {
                try {
                    start.await();
                    fs.mkdir("/concurrent/dir-" + id);
                    fs.createFile("/concurrent/dir-" + id + "/f.txt");
                    fs.writeFile("/concurrent/dir-" + id + "/f.txt", "x");
                    fs.appendFile("/concurrent/log.txt", id + ",");
                } catch (Exception e) {
                    failures.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    fs.clearThreadCwd();
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();
        pool.shutdown();

        System.out.println("=== FileSystem Concurrency Demo ===");
        System.out.println("ls /concurrent size: " + fs.ls("/concurrent").size()
                + " (expect " + (threads + 1) + " = dirs + log.txt)");
        System.out.println("log.txt: " + fs.readFile("/concurrent/log.txt"));
        System.out.println(failures.get() == 0 && fs.ls("/concurrent").size() == threads + 1
                ? "PASS" : "FAIL");
    }
}
