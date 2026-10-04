package com.machine_coding_round.splitwise.forInterview.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Split {
    private final String userId;
    @Setter
    private double amount;
}
