package com.example.demo.repository;

import com.example.demo.domain.TKBall;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TKBallRepository extends JpaRepository<TKBall, Long> {
    Optional<TKBall> getTKBallByUser(User user);
    void deleteByUser(User user);
}
