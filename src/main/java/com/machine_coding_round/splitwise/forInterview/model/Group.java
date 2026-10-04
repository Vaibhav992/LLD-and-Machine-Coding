package com.machine_coding_round.splitwise.forInterview.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Group {
    private final String id;
    private final String name;
    @Getter(lombok.AccessLevel.NONE)
    private final List<String> memberIds = new ArrayList<>();

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addMember(String userId) {
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
        }
    }

    public List<String> getMemberIds() {
        return Collections.unmodifiableList(memberIds);
    }
}
