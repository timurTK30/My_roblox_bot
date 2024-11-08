package com.example.demo.handlers;

public interface BasicHandlers {

    boolean canHandle(String text);
    void handle(Long chatId, String text);
}
