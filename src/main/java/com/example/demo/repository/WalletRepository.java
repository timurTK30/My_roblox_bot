package com.example.demo.repository;

import com.example.demo.domain.User;
import com.example.demo.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> getWalletByUser(User user);
}
