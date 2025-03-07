package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserStatusData {

    private Long chatId;
    private UserStatus userStatus;
    private LocalDateTime createdAt;

    public UserStatusData(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
