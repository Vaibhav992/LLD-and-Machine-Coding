package com.machine_coding_round.filesystem.mylearning;

import com.machine_coding_round.filesystem.mylearning.service.FileSystem;

public class FileSystemDemo {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();

        System.out.println("=== In-Memory File System (learning) ===");
        System.out.println("pwd: " + fs.pwd());

        fs.mkdir("/home");
        fs.mkdir("/home/alice");
        fs.mkdirp("/home/alice/docs/work");
        fs.createFile("/home/alice/docs/readme.txt");
        fs.writeFile("/home/alice/docs/readme.txt", "Hello FileSystem\n");
        fs.appendFile("/home/alice/docs/readme.txt", "Line 2\n");

        fs.createFile("/home/alice/docs/work/todo.txt");
        fs.writeFile("/home/alice/docs/work/todo.txt", "- study LLD\n- code filesystem\n");

        System.out.println("ls /home: " + fs.ls("/home"));
        System.out.println("ls /home/alice/docs: " + fs.ls("/home/alice/docs"));
        System.out.println("read readme.txt:\n" + fs.readFile("/home/alice/docs/readme.txt"));

        fs.cd("/home/alice/docs");
        System.out.println("cd docs, pwd: " + fs.pwd());
        System.out.println("ls .: " + fs.ls());
        System.out.println("ls work: " + fs.ls("work"));
        System.out.println("read relative: " + fs.readFile("work/todo.txt").trim());

        fs.cd("..");
        System.out.println("cd .., pwd: " + fs.pwd());

        fs.rm("/home/alice/docs/work/todo.txt");
        System.out.println("after rm todo, ls work: " + fs.ls("/home/alice/docs/work"));
        System.out.println("exists todo? " + fs.exists("/home/alice/docs/work/todo.txt"));
    }
}
