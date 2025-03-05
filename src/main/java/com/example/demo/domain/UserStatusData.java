package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class UserStatusData {

    private Long chatId;
    private UserStatus userStatus;
    private LocalDateTime createdAt;

    public UserStatusData(Long chatId, UserStatus userStatus) {
        this.chatId = chatId;
        this.userStatus = userStatus;
        this.createdAt = LocalDateTime.now();
    }
}
