package com.example.demo.handlers;

import com.example.demo.util.CommandData;

public interface BasicHandlers {

    boolean canHandle(CommandData data);
    void handle(Long chatId, CommandData data);
}
