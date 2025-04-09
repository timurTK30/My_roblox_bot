package com.example.demo.service.serviceImp;

import com.example.demo.domain.TKBall;
import com.example.demo.repository.TKBallRepository;
import com.example.demo.service.TKBallService;
import com.example.demo.service.UserService;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TKBallServiceImpl implements TKBallService {

    private final TKBallRepository repository;
    private final UserService userService;


    @Override
    public TKBall save(TKBall tkBall) {
        return repository.save(tkBall);
    }

    @Override
    public Optional<TKBall> getTKBallByChatId(Long chatId) {
        return repository.getTKBallByUser(userService.getUserByChatId(chatId));
    }

    @Override
    public TKBall updateByChatId(Long chatId, Long amount) {
        Optional<TKBall> tkBallByChatId = getTKBallByChatId(chatId);
        if (tkBallByChatId.isPresent()) {
            TKBall tkBall = tkBallByChatId.get();
            tkBall.setAmountOfBalls(amount);
            return save(tkBall);
        }
        throw new NotFoundException("Юзер не найден: " + chatId);
    }

    @Override
    public TKBall withdrawTKBall(Long chatId, Long amount) {
        Optional<TKBall> tkBallByChatId = getTKBallByChatId(chatId);
        if (tkBallByChatId.isPresent()) {
            TKBall tkBall = tkBallByChatId.get();
            tkBall.setAmountOfBalls(tkBall.getAmountOfBalls() - amount);
            return save(tkBall);
        }
        throw new NotFoundException("Юзер не найден: " + chatId);
    }

    @Override
    public TKBall depositTKBall(Long chatId, Long amount) {
        Optional<TKBall> tkBallByChatId = getTKBallByChatId(chatId);
        if (tkBallByChatId.isPresent()) {
            TKBall tkBall = tkBallByChatId.get();
            tkBall.setAmountOfBalls(tkBall.getAmountOfBalls() + amount);
            return save(tkBall);
        }
        throw new NotFoundException("Юзер не найден: " + chatId);
    }

    @Override
    public void deleteByChatId(Long chatId) {
        repository.deleteByUser(userService.getUserByChatId(chatId));
    }

    @Override
    public Long updateBalanceAfterMiniGame(Long chatId, Long amountOfBet, boolean isWin) {
        if(isWin){
            return depositTKBall(chatId, amountOfBet).getAmountOfBalls();
        } else {
            return withdrawTKBall(chatId, amountOfBet).getAmountOfBalls();
        }
    }

    @Override
    public Boolean isEnoughTickets(Long chatId, Long amountOfBet) {
        Long amountOfBalls = getTKBallByChatId(chatId).get().getAmountOfBalls();
        return amountOfBalls >= amountOfBet;
    }
}
