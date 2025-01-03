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
    public String save(PrizeWebAppData prizeWebAppData, Long chatId) {
        Prize prize = new Prize();
        prize.setChatId(chatId);
        prize.setPrizeName(prizeWebAppData.getName());
        prize.setTime(LocalDateTime.now());

        try {
            repository.save(prize);
            return "Поздравляем! \uD83C\uDF89 \n" +
                    "Вы выиграли " + prizeWebAppData.getName() + " \uD83C\uDFC6\n" +
                    "Проверьте свой личный кабинет, чтобы узнать детали и забрать награду. \n" +
                    "Спасибо за участие! \uD83D\uDE0A";
        } catch (Exception e){
            log.info("!!!!Попытка сохронение дубликата😧😧😧. " + e.getMessage());
        }
        return "Ошибка: Вы уже получили приз! ❌\n" +
                "К сожалению, мы не можем выдать новый приз, так как у вас уже есть активный приз в личном кабинете.\n" +
                "\n" +
                "\uD83C\uDF81 Проверьте свой текущий приз и наслаждайтесь!\n" +
                "Если у вас есть вопросы, свяжитесь с нашей поддержкой.\n" +
                "\n" +
                "Спасибо за понимание и участие! \uD83D\uDE0A";
    }
}
