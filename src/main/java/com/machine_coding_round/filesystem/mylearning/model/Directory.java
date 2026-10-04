package com.machine_coding_round.filesystem.mylearning.model;

import com.machine_coding_round.filesystem.mylearning.exception.AlreadyExistsException;
import com.machine_coding_round.filesystem.mylearning.exception.PathNotFoundException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Directory extends Node {
    @Getter(lombok.AccessLevel.NONE)
    private final Map<String, Node> children = new LinkedHashMap<>();
    private final boolean root;

    public Directory(String name) {
        this(name, false);
    }

    private Directory(String name, boolean root) {
        super(root ? "root" : name);
        this.root = root;
    }

    public static Directory createRoot() {
        return new Directory("root", true);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    public void add(Node child) {
        if (children.containsKey(child.getName())) {
            throw new AlreadyExistsException("Already exists: " + child.getName());
        }
        children.put(child.getName(), child);
        child.setParent(this);
        touch();
    }

    public void remove(String name) {
        Node removed = children.remove(name);
        if (removed == null) {
            throw new PathNotFoundException("No such child: " + name);
        }
        removed.setParent(null);
        touch();
    }

    public Node getChild(String name) {
        return children.get(name);
    }

    public boolean hasChild(String name) {
        return children.containsKey(name);
    }

    public List<String> listNames() {
        return new ArrayList<>(children.keySet());
    }

    public Map<String, Node> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    @Override
    public String getPath() {
        if (root) {
            return "/";
        }
        return super.getPath();
    }
}
