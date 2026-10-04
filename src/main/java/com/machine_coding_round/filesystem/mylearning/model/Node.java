package com.machine_coding_round.filesystem.mylearning.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Base type for both files and directories (Composite pattern).
 */
@Getter
public abstract class Node {
    private final String name;
    private final Instant createdAt;
    private Instant modifiedAt;
    @Setter(AccessLevel.PACKAGE)
    private Directory parent;

    protected Node(String name) {
        if (name == null || name.isBlank() || name.contains("/")) {
            throw new IllegalArgumentException("Invalid node name: " + name);
        }
        this.name = name;
        this.createdAt = Instant.now();
        this.modifiedAt = this.createdAt;
    }

    protected void touch() {
        this.modifiedAt = Instant.now();
    }

    public abstract boolean isDirectory();

    public String getPath() {
        if (parent == null) {
            return "/" + name;
        }
        String parentPath = parent.isRoot() ? "" : parent.getPath();
        return parentPath + "/" + name;
    }
}
