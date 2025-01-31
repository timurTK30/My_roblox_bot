package com.example.demo.service;

import com.example.demo.domain.AdminStatus;
import com.example.demo.domain.User;
import com.example.demo.dto.GameDto;
import com.example.demo.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto userDto);
    List<UserDto> readAll();
    User updateByChatId(UserDto userDto, Long chatId);
    void deleteByName(UserDto userDto);
    UserDto getUserByChatId(Long chatId);
    User updateStatusByChatId(Long chatId, String status);
    List<UserDto> getUserByGameId(Long gameId);
    User updateRoleByChatId(Long chatId, String role);
    Boolean isUserAdmin(Long chatId);
    Boolean isUserAdmin(UserDto userDto);
    void deleteGameRequestFromUser(Long chatId);

}
