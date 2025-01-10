package com.example.demo;

import com.example.demo.config.BotConfig;
import com.example.demo.handlers.service.CallbackService;
import com.example.demo.handlers.service.CommandService;
import com.example.demo.handlers.service.WebAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static java.util.Objects.nonNull;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CallbackService callbackService;
    private final CommandService commandService;
    private final WebAppService webAppService;

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            callbackService.handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage() && nonNull(update.getMessage().getText())) {
            commandService.handleCommand(update.getMessage());
        } else if (nonNull(update.getMessage().getWebAppData())) {
            webAppService.handleWebAppData(update);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }
}
