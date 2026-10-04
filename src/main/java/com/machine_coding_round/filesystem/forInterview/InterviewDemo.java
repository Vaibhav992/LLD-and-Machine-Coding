package com.machine_coding_round.filesystem.forInterview;

import com.machine_coding_round.filesystem.forInterview.service.FileSystem;

public class InterviewDemo {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();

        fs.mkdir("/home");
        fs.mkdir("/home/alice");
        fs.createFile("/home/alice/notes.txt");
        fs.write("/home/alice/notes.txt", "LLD practice");

        System.out.println("ls /home = " + fs.ls("/home"));
        System.out.println("ls /home/alice = " + fs.ls("/home/alice"));
        System.out.println("read = " + fs.read("/home/alice/notes.txt"));

        fs.createFile("/home/alice/todo.txt");
        fs.write("/home/alice/todo.txt", "1. filesystem");
        fs.rm("/home/alice/todo.txt");
        System.out.println("after rm, ls = " + fs.ls("/home/alice"));
    }
}
