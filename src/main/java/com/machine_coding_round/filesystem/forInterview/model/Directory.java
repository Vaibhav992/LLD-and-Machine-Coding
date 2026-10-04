package com.machine_coding_round.filesystem.forInterview.model;

import com.machine_coding_round.filesystem.forInterview.exception.FsException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Directory extends Node {
    private final Map<String, Node> children = new LinkedHashMap<>();

    public Directory(String name) { super(name); }

    @Override
    public boolean isDirectory() { return true; }

    public void add(Node child) {
        if (children.containsKey(child.getName())) {
            throw new FsException("Already exists: " + child.getName());
        }
        children.put(child.getName(), child);
    }

    public Node get(String name) { return children.get(name); }

    public void remove(String name) {
        if (children.remove(name) == null) {
            throw new FsException("Not found: " + name);
        }
    }

    public List<String> list() { return new ArrayList<>(children.keySet()); }

    public boolean isEmpty() { return children.isEmpty(); }
}
