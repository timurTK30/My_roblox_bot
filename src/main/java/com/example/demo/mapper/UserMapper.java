package com.example.demo.mapper;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDto dto){
        User user = new User();
        user.setChatId(dto.getChatId());
        user.setNickname(dto.getNickname());
        user.setRole(Role.USER);
        return user;
    }

    public UserDto toDto(User user){
        UserDto userDto = new UserDto();
        userDto.setNickname(user.getNickname());
        userDto.setChatId(user.getChatId());
        userDto.setRole(user.getRole().name());
        return userDto;
    }
}
