package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
public class BotSender extends DefaultAbsSender {

    private final BotConfig botConfig;

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    protected BotSender(BotConfig botConfig) {
        super(new DefaultBotOptions());
        this.botConfig = botConfig;
    }
}
