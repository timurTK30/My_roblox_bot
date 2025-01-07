package com.example.demo.repository;

import com.example.demo.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> getWalletByChatId(Long chatId);
}
