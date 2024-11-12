package com.example.demo.handlers;

import org.springframework.stereotype.Service;

@Service
public class UserCallbackHanlers implements BasicHandlers{


    @Override
    public boolean canHandle(String callbackData) {
        String replacedData = callbackData.replaceAll("[^a-zA-Zа-яА-ЯёЁ\\s]", "").trim();
        System.out.println(replacedData);
        return replacedData.matches(
                "(^Зарегистрировать в системе|😀|😡|ALL|HORROR|ADVENTURE" +
                        "|SHOOTER|TYCOON|SURVIVAL|Оставить заяву.*|Написать админу| *.Помошь" +
                        "|Игры|Купить подписки|Профиль|Доступные квесты|Прочитать доступные игры" +
                        "|Квесты|Все квесты|Поиск по играх|Показать друзей.*|Оставить.*" +
                        "|Редактировать сообщение.*|Купить.*|ADMIN.*|USER.*|PREMIUM_USER.*" +
                        "|Принять квест.*| *._quest_.*|Отменить квест)"
        );
    }

    @Override
    public void handle(Long chatId, String data) {
        System.out.println(data);
    }
}