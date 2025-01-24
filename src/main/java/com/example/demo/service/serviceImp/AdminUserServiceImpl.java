package com.example.demo.service.serviceImp;

import com.example.demo.domain.AdminUser;
import com.example.demo.repository.AdminUserRepository;
import com.example.demo.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository repository;


    @Override
    public AdminUser save(AdminUser adminUser) {
        return repository.save(adminUser);
    }

    @Override
    public List<AdminUser> readAll() {
        return repository.findAll();
    }

//    @Override
//    public AdminUser updateByChatId(AdminUser adminUser) {
//        return save(adminUser);
//    }

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
}
