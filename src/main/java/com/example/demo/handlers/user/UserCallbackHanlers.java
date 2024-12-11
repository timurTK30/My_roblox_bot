package com.example.demo.handlers.user;

import com.example.demo.handlers.BasicHandlers;
import com.example.demo.handlers.UtilCommandsHandler;
import com.example.demo.util.CommandData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCallbackHanlers implements BasicHandlers {

    private final UserCommandsHandler userCommandsHandler;
    private final UtilCommandsHandler util;

    @Override
    public boolean canHandle(CommandData commandData) {
        String callbackData = commandData.getData();
        return callbackData.matches(
            "(^Зарегистрировать|ok_reply|bad_reply|ALL|HORROR|ADVENTURE" +
                "|SHOOTER|TYCOON|SURVIVAL|Написать админу|Помошь|Игры|Купить подписки" +
                "|Профиль|Прочитать доступные игры|Квесты|Все квесты|Поиск по играх" +
                "|Отменить квест|request_buy_admin|request_buy_premium|leave_request_.*" +
                "|show_friends_.*|remove_gameRequest_.*)"
        );
    }

    @Override
    public void handle(Long chatId, CommandData commandData) {
        String data = commandData.getData();
        Integer msgId = commandData.getMsgId();
        String callBackId = commandData.getCallBackId();
        switch (data) {
            case "Зарегистрировать":
                userCommandsHandler.register(chatId, msgId);
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
                userCommandsHandler.cancelQuest(chatId);
                break;
            case "request_buy_admin":
            case "request_buy_premium":
                util.requestToBuySub(data, chatId);
                break;
            default:
                if (data.startsWith("leave_request_")) {
                    userCommandsHandler.handleGameApplication(chatId, data, callBackId);
                    break;
                } else if (data.startsWith("show_friends_")) {
                    userCommandsHandler.showFriends(chatId, data);
                    break;
                } else if (data.startsWith("remove_gameRequest_")) {
                    userCommandsHandler.removeGameRequest(chatId, callBackId);
                } else {
                    log.warn("UserCallbackHanlers -> не найдена кнопка -> " + data);
                }
        }
    }
}