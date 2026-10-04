package com.machine_coding_round.splitwise.mylearning.model;

import lombok.Getter;

@Getter
public class User {
    private final String id;
    private final String name;
    private final String email;
    private final String phone;

    public User(String id, String name, String email, String phone) {
        if (id == null || name == null) {
            throw new IllegalArgumentException("User id and name cannot be null");
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name;
    }
}
