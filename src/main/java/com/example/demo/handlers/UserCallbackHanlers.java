package com.example.demo.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserCallbackHanlers implements BasicHandlers{


    @Override
    public boolean canHandle(String callbackData) {
        System.out.println(callbackData);
//        return callbackData.matches(
//                "(^Зарегистрировать в системе|😀|😡|ALL|HORROR|ADVENTURE" +
//                        "|SHOOTER|TYCOON|SURVIVAL|Оставить заяву.*|Написать админу| *.Помошь" +
//                        "|Игры|Купить подписки|Профиль|Доступные квесты|Прочитать доступные игры" +
//                        "|Квесты|Все квесты|Поиск по играх|Показать друзей.*|Оставить.*" +
//                        "|Редактировать сообщение.*|Купить.*|ADMIN.*|USER.*|PREMIUM_USER.*" +
//                        "|Принять квест.*| *._quest_.*|Отменить квест)"
//        );
        return true;
    }

    @Override
    public void handle(Long chatId, String data) {
        switch (data){
            case "Зарегистрировать":
                break;
            case "ok_reply":
                break;
            case "bad_reply":
                break;
            case "ALL":
                break;
            case "HORROR":
                break;
            case "ADVENTURE":
                break;
            case "SHOOTER":
                break;
            case "TYCOON":
                break;
            case "SURVIVAL":
                break;
            case "Написать админу":
                break;
            case "Помошь":
                break;
            case "Игры":
                break;
            case "Купить подписки":
                break;
            case "Профиль":
                break;
            case "Доступные квесты":
                break;
            case "Прочитать доступные игры":
                break;
            case "Квесты":
                break;
            case "Все квесты":
                break;
            case "Поиск по играх":
                break;
            case "Отменить квест":
                break;
            case "request_buy_admin":
            case "request_buy_premium":
                break;
            default:
                log.warn("UserCallbackHanlers -> не найдена кнопка");
        }
    }
}