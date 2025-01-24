package com.example.demo.service;

import com.example.demo.domain.AdminUser;

import java.util.List;

public interface AdminUserService {

    AdminUser save(AdminUser adminUser);
    List<AdminUser> readAll();
//    AdminUser updateByChatId(AdminUser adminUser);
    void deleteByChatId(Long chatId);
    AdminUser getAdminUserByChatId(Long chatId);

}
