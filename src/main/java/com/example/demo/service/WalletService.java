package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.domain.Wallet;

import java.util.Optional;

public interface WalletService {

    Wallet save(Wallet wallet);
    String updateByUser(Double amountOfCoin, User user);
    Optional<Wallet> getWalletByUser(User user);
    void deleteByUser(User user);
}
