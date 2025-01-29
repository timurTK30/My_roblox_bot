package com.example.demo.mapper;

import com.example.demo.domain.AdminUser;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserStatus;
import com.example.demo.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setChatId(dto.getChatId());
        user.setNickname(dto.getNickname());
        if (dto.getRole() == null) {
            user.setRole(Role.USER);
        } else {
            user.setRole(Role.valueOf(dto.getRole()));
        }
        user.setStatus(UserStatus.valueOf(dto.getStatus()));
        user.setDateOfRegisterAcc(dto.getDateOfRegisterAcc());
        user.setGame(dto.getGame());
        user.setExecutiveQuest(dto.getExecutiveQuest());
        return user;
    }

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        if (user instanceof AdminUser) {
            AdminUser adminUser = (AdminUser) user;
            userDto.setAStatus(adminUser.getAStatus().name());
            userDto.setTempChatIdForReply(adminUser.getTempChatIdForReply());
        }

        userDto.setId(user.getId());
        userDto.setNickname(user.getNickname());
        userDto.setChatId(user.getChatId());
        userDto.setRole(user.getRole().name());
        userDto.setStatus(user.getStatus().name());
        userDto.setDateOfRegisterAcc(user.getDateOfRegisterAcc());
        userDto.setGame(user.getGame());
        userDto.setExecutiveQuest(user.getExecutiveQuest());
        return userDto;
    }
}
