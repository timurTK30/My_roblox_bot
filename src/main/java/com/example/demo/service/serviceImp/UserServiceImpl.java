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
        return userMapper.toDto(userByChatId.get());
    }
}
