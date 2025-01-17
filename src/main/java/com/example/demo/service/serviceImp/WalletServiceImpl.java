package com.example.demo.service.serviceImp;

import com.example.demo.domain.Wallet;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.WalletRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;

    public WalletServiceImpl(WalletRepository repository, UserService userService, UserMapper userMapper) {
        this.repository = repository;
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @Override
    public Wallet save(Wallet wallet) {
        return repository.save(wallet);
    }

    @Override
    public String updateByChatId(Double amountOfCoin, Long chatId) {
        Optional<Wallet> walletByChatId = getWalletByChatId(chatId);
        if (walletByChatId.isEmpty()) {
            log.warn("Wallet updateByChatId, <---!!! там ошибка");
            return null;
        }
        Wallet pulledWallet = walletByChatId.get();
        pulledWallet.setBalance(pulledWallet.getBalance() + amountOfCoin);
        save(pulledWallet);

        return "\uD83C\uDF89 Поздравляем! Вы выиграли "+ amountOfCoin +" монет! \uD83D\uDCB0 Мы уже зачислили их на ваш кошелёк. Продолжайте играть и выигрывайте ещё больше! \uD83C\uDF40";
    }

    @Override
    public Optional<Wallet> getWalletByChatId(Long chatId) {
        Optional<Wallet> walletByChatId = repository.getWalletByUser(userMapper.toEntity(userService.getUserByChatId(chatId)));
        if (walletByChatId.isEmpty()){
            log.warn("getWalletByChatId, <---!!! там ошибка");
            return Optional.empty();
        }
        return walletByChatId;
    }
}
