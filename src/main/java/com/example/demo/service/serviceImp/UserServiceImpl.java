package com.example.demo.service.serviceImp;

import com.example.demo.domain.*;
import com.example.demo.dto.UserDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.AdminUserRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdminUserRepository adminUserRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, AdminUserRepository adminUserRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.adminUserRepository = adminUserRepository;
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
    public User updateByChatId(UserDto userDto, Long chatId) {
        User userByChatId = userRepository.getUserByChatId(chatId).get();
        Game game = userMapper.toEntity(userDto).getGame();
        if (game != null) {
            userByChatId.setGame(userDto.getGame());
        }
        userByChatId.setNickname(userDto.getNickname());
        userByChatId.setId(userDto.getId());
        userByChatId.setStatus(UserStatus.valueOf(userDto.getStatus()));
        userByChatId.setChatId(userDto.getChatId());
        userByChatId.setRole(Role.valueOf(userDto.getRole()));
        userByChatId.setExecutiveQuest(userDto.getExecutiveQuest());
        if (userByChatId instanceof AdminUser adminUser){
            adminUser.setTempChatIdForReply(userDto.getTempChatIdForReply());
            adminUser.setAStatus(AdminStatus.valueOf(userDto.getAStatus()));
            return adminUserRepository.save(adminUser);
        }
        userRepository.save(userByChatId);
        return userByChatId;
    }

    @Override
    public void deleteByName(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        userRepository.delete(user);
    }

    @Override
    public User getUserByChatId(Long chatId) {
        Optional<User> userByChatId = userRepository.getUserByChatId(chatId);
        return userByChatId.orElse(null);
    }

    @Override
    public User updateStatusByChatId(Long chatId, String status) {
        User userByChatId = userRepository.getUserByChatId(chatId).get();
        if (userByChatId instanceof AdminUser adminUser){
            adminUser.setStatus(UserStatus.valueOf(status));
            return adminUserRepository.save(adminUser);
        }
        userByChatId.setStatus(UserStatus.valueOf(status));
        userRepository.save(userByChatId);
        return userByChatId;
    }

    @Override
    public List<UserDto> getUserByGameId(Long gameId) {
        List<User> usersByGameId = userRepository.getUserByGameId(gameId);
        return usersByGameId.stream().map(userMapper::toDto).toList();
    }

    @Override
    public User updateRoleByChatId(Long chatId, String role) {
        User userByChatId = userRepository.getUserByChatId(chatId).get();
        userByChatId.setRole(Role.valueOf(role));
        if (userByChatId instanceof AdminUser adminUser){
            return adminUserRepository.save(adminUser);
        }
        return userRepository.save(userByChatId);
    }

    @Override
    public Boolean isUserAdmin(Long chatId) {
        User userByChatId = getUserByChatId(chatId);
        if(Objects.nonNull(userByChatId)){
            return userByChatId.getRole().name().equalsIgnoreCase(Role.ADMIN.name());
        }
        return false;
    }

    @Override
    public Boolean isUserAdmin(UserDto userDto) {
        if(Objects.nonNull(userDto)){
            return userDto.getRole().equalsIgnoreCase(Role.ADMIN.name());
        }
        return false;
    }

    @Override
    public void deleteGameRequestFromUser(Long chatId) {
        User userByChatId = getUserByChatId(chatId);
        userByChatId.setGame(null);
        userRepository.save(userByChatId);
    }
}
