package com.example.demo.service;

import com.example.demo.domain.TKTicket;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface TKTicketService {

    TKTicket save(TKTicket tkTicket);
    Optional<TKTicket> getTKTicketByChatId(Long chatId);
    TKTicket updateByChatId(Long chatId, Long amount);
    TKTicket withdrawTKTicket(Long chatId, Long amount);
    TKTicket depositTKTicket(Long chatId, Long amount);
    void deleteByChatId(Long chatId);
    Long updateBalanceAfterMiniGame(Long chatId, Long amountOfBet, boolean isWin);
    Boolean isEnoughTickets(Long chatId, Long amountOfBet);
}
