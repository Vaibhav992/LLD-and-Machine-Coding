package com.machine_coding_round.filesystem.forInterview.service;

import com.machine_coding_round.filesystem.forInterview.exception.FsException;
import com.machine_coding_round.filesystem.forInterview.model.Directory;
import com.machine_coding_round.filesystem.forInterview.model.FileNode;
import com.machine_coding_round.filesystem.forInterview.model.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Interview version: absolute paths only, Composite File/Directory.
 * Ops: mkdir, createFile, write, read, ls, rm
 */
public class FileSystem {

    private final Directory root = new Directory("/");

    public void mkdir(String path) {
        Parts p = parts(path);
        Directory parent = dir(p.parentSegments); // parents must already exist
        if (parent.get(p.name) != null) throw new FsException("Exists: " + path);
        parent.add(new Directory(p.name));
    }

    public void createFile(String path) {
        Parts p = parts(path);
        Directory parent = dir(p.parentSegments);
        if (parent.get(p.name) != null) throw new FsException("Exists: " + path);
        parent.add(new FileNode(p.name));
    }

    public void write(String path, String content) {
        Node n = resolve(path);
        if (n.isDirectory()) throw new FsException("Is a directory: " + path);
        ((FileNode) n).write(content);
    }

    public String read(String path) {
        Node n = resolve(path);
        if (n.isDirectory()) throw new FsException("Is a directory: " + path);
        return ((FileNode) n).read();
    }

    public List<String> ls(String path) {
        if ("/".equals(path)) return root.list();
        Node n = resolve(path);
        if (!n.isDirectory()) return List.of(n.getName());
        return ((Directory) n).list();
    }

    public void rm(String path) {
        Parts p = parts(path);
        Directory parent = dir(p.parentSegments);
        Node child = parent.get(p.name);
        if (child == null) throw new FsException("Not found: " + path);
        if (child.isDirectory() && !((Directory) child).isEmpty()) {
            throw new FsException("Directory not empty: " + path);
        }
        parent.remove(p.name);
    }

    private Node resolve(String path) {
        if ("/".equals(path)) return root;
        Parts p = parts(path);
        Directory parent = dir(p.parentSegments);
        Node node = parent.get(p.name);
        if (node == null) throw new FsException("Not found: " + path);
        return node;
    }

    private Directory dir(List<String> segments) {
        Directory current = root;
        for (String seg : segments) {
            Node next = current.get(seg);
            if (next == null) throw new FsException("Not found: " + seg);
            if (!next.isDirectory()) throw new FsException("Not a directory: " + seg);
            current = (Directory) next;
        }
        return current;
    }

    private Parts parts(String path) {
        if (path == null || !path.startsWith("/") || path.length() == 1) {
            throw new FsException("Use absolute path like /a/b: " + path);
        }
        String trimmed = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        String[] all = trimmed.substring(1).split("/");
        List<String> list = new ArrayList<>(Arrays.asList(all));
        String name = list.remove(list.size() - 1);
        if (name.isBlank()) throw new FsException("Invalid path: " + path);
        return new Parts(list, name);
    }

    private static final class Parts {
        final List<String> parentSegments;
        final String name;
        Parts(List<String> parentSegments, String name) {
            this.parentSegments = parentSegments;
            this.name = name;
        }
    }
}
