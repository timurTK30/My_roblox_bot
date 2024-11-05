package com.example.demo.service;

import com.example.demo.domain.AdminStatus;
import com.example.demo.domain.User;
import com.example.demo.dto.GameDto;
import com.example.demo.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto userDto);
    List<UserDto> readAll();
    UserDto updateByChatId(UserDto userDto, Long chatId);
    void deleteByName(UserDto userDto);
    UserDto getUserByChatId(Long chatId);
    UserDto updateStatusByChatId(Long chatId, String status);
    UserDto updateAdminStatusByChatId(Long chatId, AdminStatus adminStatus, Long tempChatId);
    List<UserDto> getUserByGameId(Long gameId);
    UserDto updateRoleByChatId(Long chatId, String role);
    Boolean isUserAdmin(Long chatId);

}
