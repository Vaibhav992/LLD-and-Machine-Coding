package com.machine_coding_round.filesystem.mylearning.service;

import com.machine_coding_round.filesystem.mylearning.exception.AlreadyExistsException;
import com.machine_coding_round.filesystem.mylearning.exception.InvalidPathException;
import com.machine_coding_round.filesystem.mylearning.exception.PathNotFoundException;
import com.machine_coding_round.filesystem.mylearning.model.Directory;
import com.machine_coding_round.filesystem.mylearning.model.FileNode;
import com.machine_coding_round.filesystem.mylearning.model.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory FS.
 * - Tree mutations/reads go through synchronized methods (coarse lock on this).
 * - cwd is ThreadLocal so parallel clients don't stomp each other's working dir.
 */
public class FileSystem {

    private final Directory root = Directory.createRoot();
    private final ThreadLocal<Directory> cwdPerThread = new ThreadLocal<>();

    public Directory getRoot() {
        return root;
    }

    private Directory cwd() {
        Directory d = cwdPerThread.get();
        return d != null ? d : root;
    }

    public synchronized String pwd() {
        return cwd().getPath();
    }

    public synchronized void cd(String path) {
        Node node = resolve(path);
        if (!node.isDirectory()) {
            throw new InvalidPathException("Not a directory: " + path);
        }
        cwdPerThread.set((Directory) node);
    }

    public synchronized void mkdir(String path) {
        PathParts parts = splitParentAndName(path);
        Directory parent = resolveDirectory(parts.parentPath);
        if (parent.hasChild(parts.name)) {
            throw new AlreadyExistsException("Already exists: " + path);
        }
        parent.add(new Directory(parts.name));
    }

    public synchronized void mkdirp(String path) {
        List<String> segments = split(path);
        Directory current = path.startsWith("/") ? root : cwd();
        for (String segment : segments) {
            Node child = current.getChild(segment);
            if (child == null) {
                Directory dir = new Directory(segment);
                current.add(dir);
                current = dir;
            } else if (child.isDirectory()) {
                current = (Directory) child;
            } else {
                throw new InvalidPathException("Path component is a file: " + segment);
            }
        }
    }

    public synchronized void createFile(String path) {
        PathParts parts = splitParentAndName(path);
        Directory parent = resolveDirectory(parts.parentPath);
        if (parent.hasChild(parts.name)) {
            throw new AlreadyExistsException("Already exists: " + path);
        }
        parent.add(new FileNode(parts.name));
    }

    public synchronized void writeFile(String path, String content) {
        Node node = resolve(path);
        if (node.isDirectory()) {
            throw new InvalidPathException("Cannot write to directory: " + path);
        }
        ((FileNode) node).write(content);
    }

    public synchronized void appendFile(String path, String content) {
        Node node = resolve(path);
        if (node.isDirectory()) {
            throw new InvalidPathException("Cannot append to directory: " + path);
        }
        ((FileNode) node).append(content);
    }

    public synchronized String readFile(String path) {
        Node node = resolve(path);
        if (node.isDirectory()) {
            throw new InvalidPathException("Cannot read a directory: " + path);
        }
        return ((FileNode) node).read();
    }

    public synchronized List<String> ls(String path) {
        Node node = resolve(path == null || path.isBlank() ? "." : path);
        if (!node.isDirectory()) {
            return List.of(node.getName());
        }
        return ((Directory) node).listNames();
    }

    public synchronized List<String> ls() {
        return ls(".");
    }

    public synchronized void rm(String path) {
        if ("/".equals(normalize(path)) || ".".equals(path)) {
            throw new InvalidPathException("Cannot remove root or cwd this way");
        }
        PathParts parts = splitParentAndName(path);
        Directory parent = resolveDirectory(parts.parentPath);
        Node child = parent.getChild(parts.name);
        if (child == null) {
            throw new PathNotFoundException("Path not found: " + path);
        }
        if (child.isDirectory() && !((Directory) child).listNames().isEmpty()) {
            throw new InvalidPathException("Directory not empty: " + path);
        }
        parent.remove(parts.name);
        if (cwd() == child) {
            cwdPerThread.set(parent);
        }
    }

    public synchronized boolean exists(String path) {
        try {
            resolve(path);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public synchronized boolean isDirectory(String path) {
        return resolve(path).isDirectory();
    }

    /** Clear this thread's cwd (e.g. end of request). */
    public void clearThreadCwd() {
        cwdPerThread.remove();
    }

    Node resolve(String path) {
        if (path == null || path.isBlank()) {
            throw new InvalidPathException("Path is empty");
        }
        if (".".equals(path)) {
            return cwd();
        }
        if ("/".equals(path)) {
            return root;
        }

        Directory start = path.startsWith("/") ? root : cwd();
        List<String> segments = split(path);
        Node current = start;

        for (String segment : segments) {
            if (".".equals(segment)) {
                continue;
            }
            if ("..".equals(segment)) {
                if (current instanceof Directory dir && dir.getParent() != null) {
                    current = dir.getParent();
                } else if (current instanceof Directory dir && dir.isRoot()) {
                    current = root;
                } else if (current.getParent() != null) {
                    current = current.getParent();
                }
                continue;
            }
            if (!(current instanceof Directory dir)) {
                throw new PathNotFoundException("Not a directory in path: " + path);
            }
            Node next = dir.getChild(segment);
            if (next == null) {
                throw new PathNotFoundException("Path not found: " + path);
            }
            current = next;
        }
        return current;
    }

    private Directory resolveDirectory(String path) {
        if (path == null || path.isBlank() || ".".equals(path)) {
            return cwd();
        }
        if ("/".equals(path)) {
            return root;
        }
        Node node = resolve(path);
        if (!node.isDirectory()) {
            throw new InvalidPathException("Not a directory: " + path);
        }
        return (Directory) node;
    }

    private PathParts splitParentAndName(String path) {
        String normalized = normalize(path);
        if ("/".equals(normalized)) {
            throw new InvalidPathException("Invalid path for create/remove: " + path);
        }
        int slash = normalized.lastIndexOf('/');
        String parentPath;
        String name;
        if (slash <= 0) {
            parentPath = normalized.startsWith("/") ? "/" : ".";
            name = normalized.startsWith("/") ? normalized.substring(1) : normalized;
        } else {
            parentPath = normalized.substring(0, slash);
            if (parentPath.isEmpty()) parentPath = "/";
            name = normalized.substring(slash + 1);
        }
        if (name.isBlank() || name.contains("/")) {
            throw new InvalidPathException("Invalid name in path: " + path);
        }
        return new PathParts(parentPath, name);
    }

    private List<String> split(String path) {
        String p = path.startsWith("/") ? path.substring(1) : path;
        if (p.isBlank()) {
            return List.of();
        }
        return Arrays.stream(p.split("/"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String normalize(String path) {
        if (path == null || path.isBlank()) {
            throw new InvalidPathException("Path is empty");
        }
        if (path.length() > 1 && path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    private static final class PathParts {
        final String parentPath;
        final String name;

        PathParts(String parentPath, String name) {
            this.parentPath = parentPath;
            this.name = name;
        }
    }
}
