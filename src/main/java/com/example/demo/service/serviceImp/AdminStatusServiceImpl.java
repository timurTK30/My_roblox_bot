package com.example.demo.service.serviceImp;

import com.example.demo.domain.AdminStatus;
import com.example.demo.domain.AdminStatusData;
import com.example.demo.service.AdminStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service

public class AdminStatusServiceImpl implements AdminStatusService {

    private static final Duration STATUS_TTL = Duration.ofMinutes(5);
    private static final String STATUS_KEY_PREFIX = "admin_status:";
    private final RedisTemplate<String, AdminStatusData> redisTemplate;

    @Autowired
    public AdminStatusServiceImpl(@Qualifier("adminStatusRedisTemplate") RedisTemplate<String, AdminStatusData> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean setAdminStatus(Long chatId, AdminStatus status) {
        return setAdminStatus(chatId, status, 0L);
    }

    @Override
    public boolean setAdminStatus(Long chatId, AdminStatus status, Long tempChatId) {
        AdminStatusData adminStatusData = new AdminStatusData(chatId, status, tempChatId);
        String key = STATUS_KEY_PREFIX + chatId;
        redisTemplate.opsForValue().set(key, adminStatusData, STATUS_TTL);
        return true;
    }


    @Override
    public AdminStatusData getAdminStatus(Long chatId) {
        String key = STATUS_KEY_PREFIX + chatId;
        AdminStatusData adminStatusData = redisTemplate.opsForValue().get(key);
        if(adminStatusData == null){
            return new AdminStatusData(chatId, AdminStatus.DONT_WRITE);
        }
        return adminStatusData;
    }
}
