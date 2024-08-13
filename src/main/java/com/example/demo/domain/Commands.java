package com.example.demo.domain;

import lombok.Getter;

@Getter
public enum Commands {

    START("/start"),
    HELP("/help"),
    GAMES("/games"),
    READ_SUPP_MSG("/readSuppMsg");

    private final String cmd;

    Commands(String cmd) {
        this.cmd = cmd;
    }
}
