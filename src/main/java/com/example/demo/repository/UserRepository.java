package com.example.demo.repository;

import com.example.demo.domain.User;
import com.example.demo.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByChatId(Long chatId);
    List<User> getUserByGameId(Long gameId);
}
