package com.example.demo.domain;

import lombok.Getter;

@Getter
public enum Commands {

    START("/start"),
    HELP("/help"),
    GAMES("/games"),
    READ_SUPP_MSG("/readSuppMsg"),
    GAME("/game"),
    BUY_SUBSCRIBE("/buy_subscribe"),
    SET_ROLE("/set_role"),
    PROFILE("/profile");

    private final String cmd;

    Commands(String cmd) {
        this.cmd = cmd;
    }
}
