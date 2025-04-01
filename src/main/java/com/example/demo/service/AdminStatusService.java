package com.example.demo.service;

import com.example.demo.domain.AdminStatus;
import com.example.demo.domain.AdminStatusData;

public interface AdminStatusService {

    boolean setAdminStatus(Long chatId, AdminStatus status);
    boolean setAdminStatus(Long chatId, AdminStatus status, Long tempChatId);
    AdminStatusData getAdminStatus(Long chatId);
}
