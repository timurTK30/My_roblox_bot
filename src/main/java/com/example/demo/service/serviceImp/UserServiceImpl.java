package com.example.demo.service.serviceImp;

import com.example.demo.dto.UserDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto save(UserDto userDto) {
        return null;
    }

    @Override
    public List<UserDto> readAll() {
        return null;
    }

    @Override
    public UserDto updateByName(UserDto userDto, String name) {
        return null;
    }

    @Override
    public void deleteByName(UserDto userDto) {

    }
}
