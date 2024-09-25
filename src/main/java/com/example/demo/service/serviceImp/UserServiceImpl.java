package com.example.demo.service.serviceImp;

import com.example.demo.domain.AdminStatus;
import com.example.demo.domain.Game;
import com.example.demo.domain.User;
import com.example.demo.dto.UserDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

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
    public UserDto updateByChatId(UserDto userDto, Long chatId) {
        UserDto userByChatId = getUserByChatId(chatId);
        Game game = userMapper.toEntity(userDto).getGame();
        if (game != null) {
            userByChatId.setGame(userDto.getGame());
        }
        userByChatId.setNickname(userDto.getNickname());
        userByChatId.setId(userDto.getId());
        userByChatId.setStatus(userDto.getStatus());
        userByChatId.setChatId(userDto.getChatId());
        userByChatId.setAStatus(userDto.getAStatus());
        userByChatId.setTempChatIdForReply(userDto.getTempChatIdForReply());
        userByChatId.setRole(userDto.getRole());
        userRepository.save(userMapper.toEntity(userByChatId));
        return userByChatId;
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
        if (userByChatId == null) {
            System.out.println("user is null. ChatId = " + chatId);
            return null;
        }
        userByChatId.setStatus(status);
        userRepository.save(userMapper.toEntity(userByChatId));
        return userByChatId;
    }

    @Override
    public UserDto updateAdminStatusByChatId(Long chatId, AdminStatus adminStatus, Long tempChatId) {
        UserDto userByChatId = getUserByChatId(chatId);
        userByChatId.setAStatus(adminStatus.name());
        userByChatId.setTempChatIdForReply(tempChatId);
        userRepository.save(userMapper.toEntity(userByChatId));
        return userByChatId;
    }

    @Override
    public List<UserDto> getUserByGameId(Long gameId) {
        List<User> usersByGameId = userRepository.getUserByGameId(gameId);
        return usersByGameId.stream().map(userMapper::toDto).toList();
    }

    @Override
    public UserDto updateRoleByChatId(Long chatId, String role) {
        UserDto userByChatId = getUserByChatId(chatId);
        userByChatId.setRole(role);
        return save(userByChatId);
    }
}
