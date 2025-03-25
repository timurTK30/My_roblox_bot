package com.example.demo.service;

import com.example.demo.domain.TKBall;
import org.springframework.stereotype.Service;

public interface TradeTKBallService {

    TKBall buyTKBall(Long chatId, Long amountOfBalls);
    TKBall sellTKBall(Long chatId, Long amountOfBalls);
}
