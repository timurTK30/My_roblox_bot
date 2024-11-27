package com.example.demo.handlers;

import com.example.demo.util.CommandData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCallbackHanlers implements BasicHandlers{

    private final UserCommandsHandler userCommandsHandler;
    private final UtilCommandsHandler util;

    @Override
    public boolean canHandle(CommandData commandData) {
        System.out.println(commandData);
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
    public void handle(Long chatId, CommandData commandData) {
        String data = commandData.getData();
        Integer msgId = commandData.getMsgId();
        switch (data){
            case "Зарегистрировать":
                userCommandsHandler.wellcome(chatId);
                break;
            case "ok_reply":
                userCommandsHandler.handlePositiveFeedback(chatId);
                break;
            case "bad_reply":
                userCommandsHandler.handleNegativeFeedback(chatId);
                break;
            case "ALL":
            case "HORROR":
            case "ADVENTURE":
            case "SHOOTER":
            case "TYCOON":
            case "SURVIVAL":
                userCommandsHandler.readGames(chatId, data, msgId);
                break;
            case "Написать админу":
                userCommandsHandler.handleAdminMessage(chatId, msgId);
                break;
            case "Помошь":
                userCommandsHandler.help(chatId);
                break;
            case "Игры":
                userCommandsHandler.handleGameCommand(chatId);
                break;
            case "Купить подписки":
                userCommandsHandler.buySubscription(chatId);
                break;
            case "Профиль":
                userCommandsHandler.getProfile(chatId);
                break;
            case "Прочитать доступные игры":
                userCommandsHandler.allGames(chatId);
                break;
            case "Квесты":
                util.sendMessageToUser(chatId, "Какая будет категория?", List.of("Все квесты", "Поиск по играх"), 2);
                break;
            case "Все квесты":
                userCommandsHandler.allQuests(chatId);
                break;
            case "Поиск по играх":
                userCommandsHandler.findForGames(chatId);
                break;
            case "Отменить квест":
                break;
            case "request_buy_admin":
            case "request_buy_premium":
                break;
            default:
                log.warn("📉UserCallbackHanlers -> не найдена кнопка");
        }
    }
}