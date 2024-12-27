package com.example.demo.handlers.service;

import com.example.demo.domain.Prize;
import com.example.demo.domain.PrizeWebAppData;
import com.example.demo.handlers.UtilCommandsHandler;
import com.example.demo.service.PrizeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientException;
import java.util.Objects;

@Service
@Slf4j
public class WebAppService {

    private final PrizeService prizeService;
    private final ObjectMapper objectMapper;
    private final UtilCommandsHandler utilCommandsHandler;

    @Autowired
    public WebAppService(PrizeService prizeService, ObjectMapper objectMapper, UtilCommandsHandler utilCommandsHandler) {
        this.prizeService = prizeService;
        this.objectMapper = objectMapper;
        this.utilCommandsHandler = utilCommandsHandler;
    }

    public void handleWebAppData(Update update) {
        Long chatId = update.getMessage().getChatId();
        String webAppData = update.getMessage().getWebAppData().getData();
        PrizeWebAppData prizeWebAppData = null;

        try {
            prizeWebAppData = objectMapper.readValue(webAppData, PrizeWebAppData.class);
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
        }
        Prize savedPrize = prizeService.save(prizeWebAppData, chatId);
        if (Objects.nonNull(savedPrize)){
            utilCommandsHandler.sendMessageToUser(chatId, "Поздравляем! \uD83C\uDF89 \n" +
                    "Вы выиграли " + prizeWebAppData.getName() + " \uD83C\uDFC6\n" +
                    "Проверьте свой личный кабинет, чтобы узнать детали и забрать награду. \n" +
                    "Спасибо за участие! \uD83D\uDE0A");
        }

    }
}
