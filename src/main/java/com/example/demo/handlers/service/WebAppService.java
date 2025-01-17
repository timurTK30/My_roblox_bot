package com.example.demo.handlers.service;

import com.example.demo.domain.PrizeWebAppData;
import com.example.demo.handlers.UtilCommandsHandler;
import com.example.demo.service.PrizeService;
import com.example.demo.service.WalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
public class WebAppService {

    private final PrizeService prizeService;
    private final ObjectMapper objectMapper;
    private final UtilCommandsHandler utilCommandsHandler;
    private final WalletService walletService;

    @Autowired
    public WebAppService(PrizeService prizeService, ObjectMapper objectMapper, UtilCommandsHandler utilCommandsHandler, WalletService walletService) {
        this.prizeService = prizeService;
        this.objectMapper = objectMapper;
        this.utilCommandsHandler = utilCommandsHandler;
        this.walletService = walletService;
    }

    public void handleWebAppData(Update update) {
        Long chatId = update.getMessage().getChatId();
        String webAppData = update.getMessage().getWebAppData().getData();
        PrizeWebAppData prizeWebAppData = null;

        try {
            prizeWebAppData = objectMapper.readValue(webAppData, PrizeWebAppData.class);
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
            return;
        }
        if (utilCommandsHandler.checkIfPrizeCoin(prizeWebAppData)) {
            Double amountOfCoin = Double.valueOf(prizeWebAppData.getName().replace("Coin", "").trim());
            String msgCoin = walletService.updateByChatId(amountOfCoin, chatId);
            utilCommandsHandler.sendMessageToUser(chatId, msgCoin);
            return;
        }
        String msg = prizeService.save(prizeWebAppData, chatId);
        utilCommandsHandler.sendMessageToUser(chatId, msg);

    }
}
