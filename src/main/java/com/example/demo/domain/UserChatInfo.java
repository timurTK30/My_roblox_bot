package com.example.demo.domain;

import lombok.Data;

@Data
public class UserChatInfo {

    private final String USER = "User";
    private Long chatId;

    @Override
    public String toString() {
        return USER + ": " + chatId + ".";
    }
}
