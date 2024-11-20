package com.example.demo.handlers;

import org.springframework.stereotype.Service;

@Service
public class AdminCallbackHandlers implements BasicHandlers{

    @Override
    public boolean canHandle(String callbackData) {
        String replacedData = callbackData.replaceAll("[^a-zA-Zа-яА-ЯёЁ\\s]", "").trim();
        return replacedData.matches(
                "(^Прочитать сообщение от юзера|Перезагрузить бота| *.Статистика использования бота" +
                        "| *.Отправить сообщение|Доступные квесты|Квест меню|Создать квест|Удалить старие квесты" +
                        "|Прочитать доступные игры|User.*|)"
        );
    }

    @Override
    public void handle(Long chatId, String text) {

    }
}
