package com.machine_coding_round.filesystem.forInterview.model;

public class FileNode extends Node {
    private String content = "";

    public FileNode(String name) { super(name); }

    @Override
    public boolean isDirectory() { return false; }

    public void write(String data) { this.content = data == null ? "" : data; }
    public String read() { return content; }
}
