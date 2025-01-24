package com.example.demo.repository;

import com.example.demo.domain.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
//    Optional<AdminUser> updateByChatId(Long chatId, AdminUser adminUser);
    void deleteByChatId(Long chatId);
    Optional<AdminUser> getAdminUserByChatId(Long chatId);
}
