package com.machine_coding_round.snakeladder.forInterview.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class Player {
    private final String name;
    @Setter
    private int position;
}
