package com.example.demo.service.serviceImp;

import com.example.demo.domain.TKTicket;
import com.example.demo.repository.TKTicketRepository;
import com.example.demo.service.TKTicketService;
import com.example.demo.service.UserService;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TKTicketServiceImpl implements TKTicketService {

    private final TKTicketRepository repository;
    private final UserService userService;


    @Override
    public TKTicket save(TKTicket tkTicket) {
        return repository.save(tkTicket);
    }

    @Override
    public Optional<TKTicket> getTKTicketByChatId(Long chatId) {
        return repository.getTKBallByUser(userService.getUserByChatId(chatId));
    }

    @Override
    public TKTicket updateByChatId(Long chatId, Long amount) {
        Optional<TKTicket> tkBallByChatId = getTKTicketByChatId(chatId);
        if (tkBallByChatId.isPresent()) {
            TKTicket tkTicket = tkBallByChatId.get();
            tkTicket.setAmountOfTickets(amount);
            return save(tkTicket);
        }
        throw new NotFoundException("Юзер не найден: " + chatId);
    }

    @Override
    public TKTicket withdrawTKTicket(Long chatId, Long amount) {
        Optional<TKTicket> tkBallByChatId = getTKTicketByChatId(chatId);
        if (tkBallByChatId.isPresent()) {
            TKTicket tkTicket = tkBallByChatId.get();
            tkTicket.setAmountOfTickets(tkTicket.getAmountOfTickets() - amount);
            return save(tkTicket);
        }
        throw new NotFoundException("Юзер не найден: " + chatId);
    }

    @Override
    public TKTicket depositTKTicket(Long chatId, Long amount) {
        Optional<TKTicket> tkBallByChatId = getTKTicketByChatId(chatId);
        if (tkBallByChatId.isPresent()) {
            TKTicket tkTicket = tkBallByChatId.get();
            tkTicket.setAmountOfTickets(tkTicket.getAmountOfTickets() + amount);
            return save(tkTicket);
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
            return depositTKTicket(chatId, amountOfBet).getAmountOfTickets();
        } else {
            return withdrawTKTicket(chatId, amountOfBet).getAmountOfTickets();
        }
    }

    @Override
    public Boolean isEnoughTickets(Long chatId, Long amountOfBet) {
        Optional<TKTicket> tkTicketByChatId = getTKTicketByChatId(chatId);
        if (tkTicketByChatId.isEmpty()){
            return false;
        }
        Long amountOfBalls = tkTicketByChatId.get().getAmountOfTickets();
        return amountOfBalls >= amountOfBet;
    }
}
