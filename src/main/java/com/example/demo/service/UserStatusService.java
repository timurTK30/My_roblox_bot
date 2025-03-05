package com.example.demo.service;

import com.example.demo.domain.UserStatus;
import com.example.demo.domain.UserStatusData;

public interface UserStatusService {

    void setUserStatus(Long chatId, UserStatus userStatus);
    UserStatusData getUserStatus(Long chatId);
    boolean isStatusExpired(UserStatusData userStatusData);
    void clearUserStatus(Long chatId);
}
