package com.example.demo.service;

import com.example.demo.domain.Wallet;

import java.util.Optional;

public interface WalletService {

    Wallet save(Wallet wallet);
    Wallet updateByChatId(Double amountOfCoin, Long chatId);
    Optional<Wallet> getWalletByChatId(Long chatId);
}
