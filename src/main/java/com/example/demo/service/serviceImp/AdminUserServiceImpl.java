package com.example.demo.service.serviceImp;

import com.example.demo.domain.AdminStatus;
import com.example.demo.domain.AdminUser;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.dto.UserDto;
import com.example.demo.repository.AdminUserRepository;
import com.example.demo.service.AdminUserService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository repository;
    private final UserService userService;


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
        User user = userService.updateRoleByChatId(chatId, Role.ADMIN.name());
        AdminUser adminUser = new AdminUser();
        adminUser.setId(user.getId());
        adminUser.setAStatus(AdminStatus.DONT_WRITE);
        return repository.save(adminUser);
    }
}
