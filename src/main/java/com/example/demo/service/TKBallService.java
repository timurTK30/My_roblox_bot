package com.example.demo.service;

import com.example.demo.domain.TKBall;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TKBallService {

    TKBall save(TKBall tkBall);
    Optional<TKBall> getTKBallByChatId(Long chatId);
    TKBall updateByChatId(Long chatId, Long amount);
    TKBall withdrawTKBall(Long chatId, Long amount);
    TKBall depositTKBall(Long chatId, Long amount);
    void deleteByChatId(Long chatId);
}
