package com.example.demo.service.serviceImp;

import com.example.demo.domain.Prize;
import com.example.demo.domain.PrizeWebAppData;
import com.example.demo.repository.PrizeRepository;
import com.example.demo.service.PrizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        repository.save(prize);
        System.out.println(prize);
        return prize;
    }
}
