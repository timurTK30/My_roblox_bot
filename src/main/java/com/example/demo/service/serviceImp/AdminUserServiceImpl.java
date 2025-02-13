package com.example.demo.service.serviceImp;

import com.example.demo.domain.*;
import com.example.demo.dto.UserDto;
import com.example.demo.repository.AdminUserRepository;
import com.example.demo.service.AdminUserService;
import com.example.demo.service.UserService;
import com.example.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository repository;
    private final UserService userService;
    private final WalletService walletService;


    @Override
    public AdminUser save(AdminUser adminUser) {
        return repository.save(adminUser);
    }

    @Override
    public List<AdminUser> readAll() {
        return repository.findAll();
    }

    @Override
    public AdminUser updateByChatId(Long chatId, AdminStatus status, Long tempChatId) {
        AdminUser adminUser = getAdminUserByChatId(chatId);
        adminUser.setTempChatIdForReply(tempChatId);
        adminUser.setAStatus(status);
        return save(adminUser);
    }

    @Override
    public void deleteByChatId(Long chatId) {
        repository.deleteByChatId(chatId);
    }

    @Override
    public AdminUser getAdminUserByChatId(Long chatId) {
        Optional<AdminUser> adminUserByChatId = repository.getAdminUserByChatId(chatId);
        if (adminUserByChatId.isEmpty()) {
            throw new RuntimeException("Пользователь не найден с помошью chatId: " + chatId);
        }
        return adminUserByChatId.get();
    }

    @Override
    public AdminUser updateTempQuestId(Long chatId, Long questId) {
        AdminUser adminUserByChatId = getAdminUserByChatId(chatId);
        adminUserByChatId.setTempQuestId(questId);
        return save(adminUserByChatId);
    }

    @Override
    public AdminUser updateUserToAdminByChatId(Long chatId) {
       // User user = userService.updateRoleByChatId(chatId, Role.ADMIN.name());
        User user = userService.getUserByChatId(chatId);
        user.setRole(Role.ADMIN);
        userService.deleteById(user.getId());
        AdminUser adminUser = new AdminUser();
        BeanUtils.copyProperties(user, adminUser);
        adminUser.setAStatus(AdminStatus.DONT_WRITE);
        AdminUser adminSave = repository.save(adminUser);
        Wallet wallet = new Wallet();
        wallet.setUser(adminSave);
        wallet.setBalance(200.0);
        walletService.save(wallet);
        return adminSave;
    }
}
