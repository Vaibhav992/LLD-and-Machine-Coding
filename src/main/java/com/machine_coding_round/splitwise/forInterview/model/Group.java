package com.machine_coding_round.splitwise.forInterview.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Group {
    private final String id;
    private final String name;
    private final List<String> memberIds = new ArrayList<>();

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addMember(String userId) {
        if (!memberIds.contains(userId)) memberIds.add(userId);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getMemberIds() { return Collections.unmodifiableList(memberIds); }
}
