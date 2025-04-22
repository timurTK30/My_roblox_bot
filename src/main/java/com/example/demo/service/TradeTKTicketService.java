package com.example.demo.service;

import com.example.demo.domain.TKTicket;

public interface TradeTKTicketService {

    TKTicket buyTKTicket(Long chatId, Long amountOfBalls);
    TKTicket sellTKTicket(Long chatId, Long amountOfBalls);
}
