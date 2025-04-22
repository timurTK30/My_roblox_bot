package com.example.demo.service.serviceImp;

import com.example.demo.domain.TKTicket;
import com.example.demo.domain.User;
import com.example.demo.domain.Wallet;
import com.example.demo.service.TKTicketService;
import com.example.demo.service.TradeTKTicketService;
import com.example.demo.service.UserService;
import com.example.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeTKTicketServiceImpl implements TradeTKTicketService {

    private final WalletService walletService;
    private final UserService userService;
    private final TKTicketService tkTicketService;
    private final Long PRICE_FOR_ONE_TKBALL = 5L;

    @Override
    public TKTicket buyTKTicket(Long chatId, Long amountOfBalls) {
        Long balanceToWithdraw = amountOfBalls * PRICE_FOR_ONE_TKBALL;
        User userByChatId = userService.getUserByChatId(chatId);
        Optional<Wallet> walletByUser = walletService.getWalletByUser(userByChatId);

        if (walletByUser.isPresent() && walletByUser.get().getBalance() >= balanceToWithdraw) {
            walletService.updateByUser(-balanceToWithdraw.doubleValue(), userByChatId);
            return tkTicketService.depositTKTicket(chatId, amountOfBalls);
        }

        return null;
    }

    @Override
    public TKTicket sellTKTicket(Long chatId, Long amountOfBalls) {
        Long balanceToDeposit = amountOfBalls * PRICE_FOR_ONE_TKBALL;
        User userByChatId = userService.getUserByChatId(chatId);
        Optional<Wallet> walletByUser = walletService.getWalletByUser(userByChatId);
        Optional<TKTicket> tkBallByChatId = tkTicketService.getTKTicketByChatId(chatId);

        if (walletByUser.isPresent() && tkBallByChatId.get().getAmountOfTickets() >= amountOfBalls) {
            walletService.updateByUser(balanceToDeposit.doubleValue(), userByChatId);
            return tkTicketService.withdrawTKTicket(chatId, amountOfBalls);
        }

        return null;
    }
}
