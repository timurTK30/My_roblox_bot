package com.example.demo.service.serviceImp;

import com.example.demo.domain.UserStatus;
import com.example.demo.domain.UserStatusData;
import com.example.demo.service.UserStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserStatusServiceImpl implements UserStatusService {

    private static final Duration STATUS_TTl = Duration.ofMinutes(10);
    private static final String STATUS_KEY_PREFIX = "user_status:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserStatusServiceImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void setUserStatus(Long chatId, UserStatus userStatus) {
        UserStatusData userStatusData = new UserStatusData(chatId, userStatus, LocalDateTime.now());
        String key = STATUS_KEY_PREFIX + chatId;
        redisTemplate.opsForValue().set(key, userStatusData, STATUS_TTl);
    }

    @Override
    public UserStatusData getUserStatus(Long chatId) {
        String key = STATUS_KEY_PREFIX + chatId;
        Object rawData = redisTemplate.opsForValue().get(key);

        if(!Objects.nonNull(rawData)){
            return new UserStatusData(UserStatus.DONT_SENT);
        }

        if (rawData instanceof java.util.LinkedHashMap) {
            return objectMapper.convertValue(rawData, UserStatusData.class);
        } else if (rawData instanceof UserStatusData) {
            return (UserStatusData) rawData;
        } else {
            return null;
        }
    }

    @Override
    public boolean isStatusExpired(UserStatusData userStatusData) {
        return LocalDateTime.now().isAfter(userStatusData.getCreatedAt().plus(STATUS_TTl));
    }

    @Override
    public void clearUserStatus(Long chatId) {
        String key = STATUS_KEY_PREFIX + chatId;
        redisTemplate.delete(key);
    }
}
