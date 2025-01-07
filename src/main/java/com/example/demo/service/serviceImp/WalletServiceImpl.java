package com.example.demo.service.serviceImp;

import com.example.demo.domain.Wallet;
import com.example.demo.repository.WalletRepository;
import com.example.demo.service.WalletService;

import java.util.Optional;

public class WalletServiceImpl implements WalletService{

    private final WalletRepository repository;
    private final WalletService service;

    public WalletServiceImpl(WalletRepository repository, WalletService service) {
        this.repository = repository;
        this.service = service;
    }


    @Override
    public Wallet save(Wallet wallet) {
        return repository.save(wallet);
    }

    @Override
    public Wallet updateByChatId(Wallet wallet, Long chatId) {=
        return null;
    }

    @Override
    public Optional<Wallet> getWalletByChatId(Long chatId) {
        Optional<Wallet> walletByChatId = repository.getWalletByChatId(chatId);
        return walletByChatId;
    }
}
