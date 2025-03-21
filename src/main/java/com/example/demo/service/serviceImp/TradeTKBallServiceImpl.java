package com.example.demo.service.serviceImp;

import com.example.demo.domain.User;
import com.example.demo.domain.Wallet;
import com.example.demo.service.TKBallService;
import com.example.demo.service.TradeTKBallService;
import com.example.demo.service.UserService;
import com.example.demo.service.WalletService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class TradeTKBallServiceImpl implements TradeTKBallService {

    private final WalletService walletService;
    private final UserService userService;
    private final TKBallService tkBallService;
    private final Long PRICE_FOR_ONE_TKBALL = 5L;

    @Override
    public boolean buyTKBall(Long chatId, Long amountOfBalls) {
        Long balanceToWithdraw = amountOfBalls * PRICE_FOR_ONE_TKBALL;
        User userByChatId = userService.getUserByChatId(chatId);
        Optional<Wallet> walletByUser = walletService.getWalletByUser(userByChatId);

        if (walletByUser.isPresent() && walletByUser.get().getBalance() >= balanceToWithdraw) {
            walletService.updateByUser(-balanceToWithdraw.doubleValue(), userByChatId);
            tkBallService.depositTKBall(chatId, amountOfBalls);
            return true;
        }

        return false;
    }

    @Override
    public boolean sellTKBall(Long chatId, Long amountOfBalls) {
        Long balanceToDeposit = amountOfBalls * PRICE_FOR_ONE_TKBALL;
        User userByChatId = userService.getUserByChatId(chatId);
        Optional<Wallet> walletByUser = walletService.getWalletByUser(userByChatId);

        if (walletByUser.isPresent() && walletByUser.get().getBalance() >= balanceToDeposit) {
            walletService.updateByUser(balanceToDeposit.doubleValue(), userByChatId);
            tkBallService.withdrawTKBall(chatId, amountOfBalls);
            return true;
        }

        return false;
    }
}
