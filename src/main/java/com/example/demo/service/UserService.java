package com.example.demo.service;

import com.example.demo.dto.GameDto;
import com.example.demo.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto userDto);
    List<UserDto> readAll();
    UserDto updateByName(UserDto userDto, String name);
    void deleteByName(UserDto userDto);
    UserDto getUserByChatId(Long chatId);
}
