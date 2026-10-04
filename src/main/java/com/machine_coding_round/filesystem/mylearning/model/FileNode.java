package com.machine_coding_round.filesystem.mylearning.model;

public class FileNode extends Node {
    private final StringBuilder content = new StringBuilder();

    public FileNode(String name) {
        super(name);
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    public void write(String data) {
        content.setLength(0);
        if (data != null) {
            content.append(data);
        }
        touch();
    }

    public void append(String data) {
        if (data != null) {
            content.append(data);
            touch();
        }
    }

    public String read() {
        return content.toString();
    }

    public int size() {
        return content.length();
    }
}
