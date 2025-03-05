package com.example.demo.service.serviceImp;

import com.example.demo.domain.UserStatus;
import com.example.demo.domain.UserStatusData;
import com.example.demo.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService{

    private static final Duration STATUS_TTl = Duration.ofMinutes(10);
    private static final String STATUS_KEY_PREFIX = "user_status:";
    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public void setUserStatus(Long chatId, UserStatus userStatus) {
        UserStatusData userStatusData = new UserStatusData(chatId, userStatus, LocalDateTime.now());
        String key = STATUS_KEY_PREFIX + chatId;
        redisTemplate.opsForValue().set(key, userStatusData, STATUS_TTl);
    }

    @Override
    public UserStatusData getUserStatus(Long chatId) {
        String key = STATUS_KEY_PREFIX + chatId;
        UserStatusData userStatusData = (UserStatusData) redisTemplate.opsForValue().get(key);
        return userStatusData;
    }

    @Override
    public boolean isStatusExpired(UserStatusData userStatusData) {
        return false;
    }

    @Override
    public void clearUserStatus(Long chatId) {

    }
}
