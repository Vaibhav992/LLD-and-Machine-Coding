package com.machine_coding_round.splitwise.forInterview.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
    private final String id;
    private final String name;
    private final String email;

    @Override
    public String toString() {
        return name;
    }
}
