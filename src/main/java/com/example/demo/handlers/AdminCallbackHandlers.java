package com.example.demo.handlers;

import com.example.demo.util.CommandData;
import org.springframework.stereotype.Service;

@Service
public class AdminCallbackHandlers implements BasicHandlers{

    @Override
    public boolean canHandle(CommandData data) {
        String replacedData = data.getData().replaceAll("[^a-zA-Zа-яА-ЯёЁ\\s]", "").trim();
        return replacedData.matches(
                "(^Прочитать сообщение от юзера|Перезагрузить бота|Статистика использования бота" +
                        "|Доступные квесты|Квест меню|Создать квест|Удалить старие квесты" +
                        "|Прочитать доступные игры|User.*|Отправить сообщение|Редоктировать.*" +
                        "Добавить награду для квеста.*|Добавить описание для квеста.*" +
                        "|Добавить игру для квеста.*| *.Изменить на.*)"
        );
    }

    @Override
    public void handle(Long chatId, CommandData commandData) {
        String data = commandData.getData();
        Integer msgId = commandData.getMsgId();
        switch (data) {
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
                if(data.startsWith("user")){

                } else if (data.startsWith("Редоктировать")) {

                } else if (data.startsWith("Добавить награду для квеста")) {

                } else if (data.startsWith("Добавить описание для квеста")) {

                } else if (data.startsWith("Добавить игру для квеста")) {

                } else if (data.contains("Изменить на")) {

                }
        }
    }
}
