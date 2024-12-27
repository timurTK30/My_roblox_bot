package com.example.demo.service.serviceImp;

import com.example.demo.domain.Prize;
import com.example.demo.domain.PrizeWebAppData;
import com.example.demo.repository.PrizeRepository;
import com.example.demo.service.PrizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PrizeServiceImpl implements PrizeService {

    private final PrizeRepository repository;

    @Autowired
    public PrizeServiceImpl(PrizeRepository repository) {
        this.repository = repository;
    }


    @Override
    public Prize save(PrizeWebAppData prizeWebAppData, Long chatId) {
        Prize prize = new Prize();
        prize.setChatId(chatId);
        prize.setPrizeName(prizeWebAppData.getName());
        prize.setTime(LocalDateTime.now());

        try {
            return repository.save(prize);
        } catch (Exception e){
            log.info("!!!!Попытка сохронение дубликата😧😧😧. " + e.getMessage());
        }
        return null;
    }
}
