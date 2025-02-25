package com.example.demo.service.serviceImp;

import com.example.demo.domain.CoinGameResult;
import com.example.demo.domain.User;
import com.example.demo.domain.Wallet;
import com.example.demo.repository.WalletRepository;
import com.example.demo.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;

    public WalletServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }


    @Override
    public Wallet save(Wallet wallet) {
        return repository.save(wallet);
    }

    @Override
    public String updateByUser(Double amountOfCoin, User user) {
        Optional<Wallet> walletByChatId = getWalletByUser(user);
        if (walletByChatId.isEmpty()) {
            log.warn("Wallet updateByChatId, <---!!! там ошибка");
            return null;
        }
        Wallet pulledWallet = walletByChatId.get();
        pulledWallet.setBalance(pulledWallet.getBalance() + amountOfCoin);
        save(pulledWallet);

        return "\uD83C\uDF89 Поздравляем! Вы выиграли " + amountOfCoin + " монет! \uD83D\uDCB0 Мы уже зачислили их на ваш кошелёк. Продолжайте играть и выигрывайте ещё больше! \uD83C\uDF40";
    }

    @Override
    public Optional<Wallet> getWalletByUser(User user) {
        Optional<Wallet> walletByChatId = repository.getWalletByUser(user);
        if (walletByChatId.isEmpty()) {
            log.warn("getWalletByChatId, <---!!! там ошибка");
            return Optional.empty();
        }
        return walletByChatId;
    }

    @Override
    public void deleteByUser(User user) {
        Optional<Wallet> walletByChatId = getWalletByUser(user);
        walletByChatId.ifPresent(repository::delete);
    }

    @Override
    public Wallet updateByUser(CoinGameResult gameResult, User user) {
        Optional<Wallet> walletByUser = getWalletByUser(user);
        if (walletByUser.isEmpty()) {
            throw new RuntimeException("Юзера с " + gameResult.getChatId() + " не найдено");
        }

        Wallet wallet = walletByUser.get();
        double amountOfRate = gameResult.getAmountOfRate();
        double balance = gameResult.getBalance();

        if (gameResult.isWon()) {
            wallet.setBalance(balance + amountOfRate);
        } else {
            wallet.setBalance(balance - amountOfRate);
        }

//        if (wallet.getBalance() == gameResult.getBalance()) {
//            save(wallet);
//        } else {
//            throw new RuntimeException("Баланс нашего кошелька != пришетшему балансу. " + wallet.getBalance() + " != " + gameResult.getBalance());
//        }
        return wallet;
    }


}
