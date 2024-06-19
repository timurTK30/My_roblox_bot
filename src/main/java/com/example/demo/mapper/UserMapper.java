package com.example.demo.mapper;

import com.example.demo.domain.AdminStatus;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserStatus;
import com.example.demo.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDto dto){
        User user = new User();
        user.setChatId(dto.getChatId());
        user.setNickname(dto.getNickname());
        user.setRole(Role.USER);
        user.setStatus(UserStatus.DONT_SENT);
        user.setAStatus(AdminStatus.DONT_WRITE);
        user.setTempChatIdForReply(0L);
        return user;
    }

    public UserDto toDto(User user){
        UserDto userDto = new UserDto();
        userDto.setNickname(user.getNickname());
        userDto.setChatId(user.getChatId());
        userDto.setRole(user.getRole().name());
        userDto.setStatus(user.getStatus().name());
        userDto.setAStatus(user.getAStatus().name());
        userDto.setTempChatIdForReply(user.getTempChatIdForReply());
        return userDto;
    }
}
