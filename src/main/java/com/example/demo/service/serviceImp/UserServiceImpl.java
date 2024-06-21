package com.example.demo.service.serviceImp;

import com.example.demo.domain.User;
import com.example.demo.dto.UserDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto save(UserDto userDto) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(userDto)));
    }

    @Override
    public List<UserDto> readAll() {
        List<User> userList = userRepository.findAll();
        return userList.stream().map(userMapper::toDto).toList();
    }

    @Override
    public UserDto updateByName(UserDto userDto, String name) {
        User user = userMapper.toEntity(userDto);
        user.setNickname(name);
        return userMapper.toDto(user);
    }

    @Override
    public void deleteByName(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        userRepository.delete(user);
    }

    @Override
    public UserDto getUserByChatId(Long chatId) {
        Optional<User> userByChatId = userRepository.getUserByChatId(chatId);
        return userByChatId.map(user -> userMapper.toDto(user)).orElse(null);
    }

    @Override
    public UserDto updateStatusByChatId(Long chatId, String status) {
        UserDto userByChatId = getUserByChatId(chatId);
        if(userByChatId == null){
            System.out.println("user is null. ChatId = " + chatId);
            return null;
        }
        userByChatId.setStatus(status);
        userRepository.save(userMapper.toEntity(userByChatId));
        return userByChatId;
    }

    @Override
    public UserDto updateAdminStatusByChatId(Long chatId, String adminStatus, Long tempChatId) {
        UserDto adminByChatId = getUserByChatId(chatId);
        System.out.println(adminByChatId);
        if (adminByChatId.getRole().equalsIgnoreCase("ADMIN")){
            adminByChatId.setAStatus(adminStatus);
            adminByChatId.setTempChatIdForReply(tempChatId);
            userRepository.save(userMapper.toEntity(adminByChatId));
            return adminByChatId;
        }
        else {
            throw new RuntimeException("предатель " + adminByChatId);
        }
    }
}
