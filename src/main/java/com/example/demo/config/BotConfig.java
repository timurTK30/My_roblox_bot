package com.example.demo.config;

import jakarta.persistence.Column;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BotConfig {

    private final String botName = "rb_inf_bot";
    private final String botToken = "7097886642:AAFtlA_v4A69FzU7zAjSWtHCw20VzGK-aok";
}
