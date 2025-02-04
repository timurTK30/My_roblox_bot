package com.example.demo.service;

import com.example.demo.domain.AdminStatus;
import com.example.demo.domain.AdminUser;

import java.util.List;

public interface AdminUserService {

    AdminUser save(AdminUser adminUser);
    List<AdminUser> readAll();
    AdminUser updateByChatId(Long chatId, AdminStatus status, Long tempChatId);
    void deleteByChatId(Long chatId);
    AdminUser getAdminUserByChatId(Long chatId);
    AdminUser updateTempQuestId(Long chatId, Long questId);

}
