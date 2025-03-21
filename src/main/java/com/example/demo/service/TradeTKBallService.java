package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public interface TradeTKBallService {

    boolean buyTKBall(Long chatId, Long amountOfBalls);
    boolean sellTKBall(Long chatId, Long amountOfBalls);
}
