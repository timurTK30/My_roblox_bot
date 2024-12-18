package com.example.demo.mapper;

import com.example.demo.domain.AdminStatus;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.UserStatus;
import com.example.demo.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
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
        }else {
            user.setRole(Role.valueOf(dto.getRole()));
        }
        user.setStatus(UserStatus.valueOf(dto.getStatus()));
        user.setAStatus(AdminStatus.valueOf(dto.getAStatus()));
        user.setDateOfRegisterAcc(dto.getDateOfRegisterAcc());
        user.setTempChatIdForReply(dto.getTempChatIdForReply());
        user.setGame(dto.getGame());
        user.setExecutiveQuest(dto.getExecutiveQuest());
        return user;
    }

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setNickname(user.getNickname());
        userDto.setChatId(user.getChatId());
        userDto.setRole(user.getRole().name());
        userDto.setStatus(user.getStatus().name());
        userDto.setAStatus(user.getAStatus().name());
        userDto.setDateOfRegisterAcc(user.getDateOfRegisterAcc());
        userDto.setTempChatIdForReply(user.getTempChatIdForReply());
        userDto.setGame(user.getGame());
        userDto.setExecutiveQuest(user.getExecutiveQuest());
        return userDto;
    }
}
