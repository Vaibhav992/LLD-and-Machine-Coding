package com.machine_coding_round.filesystem.forInterview.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Node {
    private final String name;

    public abstract boolean isDirectory();
}
