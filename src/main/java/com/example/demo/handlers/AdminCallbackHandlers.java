package com.example.demo.handlers;

import org.springframework.stereotype.Service;

@Service
public class AdminCallbackHandlers implements BasicHandlers{

    @Override
    public boolean canHandle(String callbackData) {
        String replacedData = callbackData.replaceAll("[^a-zA-Zа-яА-ЯёЁ\\s]", "").trim();
        return replacedData.matches(
                "(^Прочитать сообщение от юзера|Перезагрузить бота|Статистика использования бота" +
                        "|Доступные квесты|Квест меню|Создать квест|Удалить старие квесты" +
                        "|Прочитать доступные игры|User.*|Отправить сообщение|Редоктировать.*" +
                        "Добавить награду для квеста.*|Добавить описание для квеста.*" +
                        "|Добавить игру для квеста.*| *.Изменить на.*)"
        );
    }

    @Override
    public void handle(Long chatId, String callbackData) {
        switch (callbackData) {
            case "Прочитать сообщение от юзера":
                break;
            case "Перезагрузить бота":
                break;
            case "Статистика использования бота":
                break;
            case "Доступные квесты":
                break;
            case "Квест меню":
                break;
            case "Создать квест":
                break;
            case "Удалить старие квесты":
                break;
            case "Прочитать доступные игры":
                break;
            case "Отправить сообщение":
                break;
            default:
                if(callbackData.startsWith("User")){

                } else if (callbackData.startsWith("Редоктировать")) {

                } else if (callbackData.startsWith("Добавить награду для квеста")) {

                } else if (callbackData.startsWith("Добавить описание для квеста")) {

                } else if (callbackData.startsWith("Добавить игру для квеста")) {

                } else if (callbackData.contains("Изменить на")) {

                }
        }
    }
}
