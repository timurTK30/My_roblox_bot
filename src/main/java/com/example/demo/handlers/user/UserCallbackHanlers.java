package com.example.demo.handlers.user;

import com.example.demo.handlers.BasicHandlers;
import com.example.demo.handlers.UtilCommandsHandler;
import com.example.demo.service.TKBallService;
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
    private final TKBallService tkBallService;
    @Override
    public boolean canHandle(CommandData commandData) {
        String callbackData = commandData.getData();
        return callbackData.matches(
            "(^Зарегистрировать|ok_reply|bad_reply|ALL|HORROR|ADVENTURE" +
                "|SHOOTER|TYCOON|SURVIVAL|Написать админу|Помошь|Игры|Купить подписки" +
                "|Профиль|Прочитать доступные игры|Квесты|Все квесты|Поиск по играх" +
                "|Отменить квест|request_buy_admin|request_buy_premium|leave_request_.*" +
                "|show_friends_.*|remove_gameRequest_.*|edit_msg|leave_msg|sellTkBall.*|buyTkBall.*|custom" +
                    "|customBuyTKBall|customSellTKBall|miniGame_.*|cube_.*|/miniGames|miniBasketBall_process)"
        );
    }

    @Override
    public void handle(Long chatId, CommandData commandData) {
        String data = commandData.getData();
        Integer msgId = commandData.getMsgId();
        String callBackId = commandData.getCallBackId();
        String userName = commandData.getUserName();
        switch (data) {
            case "Зарегистрировать":
                userCommandsHandler.register(chatId, msgId, userName);
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
                userCommandsHandler.handleSupportMessage(chatId, msgId);
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
                util.allGames(chatId);
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
            case "edit_msg":
                userCommandsHandler.handleEditSuppMsg(chatId, msgId);
                break;
            case "leave_msg":
                util.editMsg(chatId, msgId,"✨ Спасибо за ваше терпение! \n" +
                        "Наши администраторы делают всё возможное, чтобы ответить вам как можно скорее. Ваша поддержка и понимание для нас очень важны! \uD83D\uDE0A \n" +
                        "Пожалуйста, оставайтесь с нами — мы скоро вернёмся с ответом! \uD83D\uDE4C");
                break;
            case "custom":
                util.sendMessageToUser(chatId, "Вибирите что вам нужно: ", List.of("Продать", "Купить"), List.of("customSellTKBall", "customBuyTKBall"), 1);
                break;
            case "customBuyTKBall":
            case "customSellTKBall":
                userCommandsHandler.updateStatusCustomTrade(chatId, data);
                break;
            case "cube_even":
            case "cube_odd":
                util.processMiniGameCube(chatId, data, msgId);
                break;
            case "/miniGames":
                util.miniGamesMsg(chatId, msgId);
                break;
            case "miniBasketBall_process":
                util.processMiniGameBasketball(chatId, data, msgId);
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
                } else if (data.startsWith("buyTkBall")) {
                    util.sendTypingStatus(chatId);
                    userCommandsHandler.buyTkBalls(chatId, data, msgId);
                    break;
                } else if (data.startsWith("sellTkBall")) {
                    util.sendTypingStatus(chatId);
                    userCommandsHandler.sellTKBalls(chatId, data, msgId);
                    break;
                } else if (data.startsWith("miniGame_")) {

                    if (!tkBallService.isEnoughTickets(chatId, 2L)){
                        util.sendMessageToUser(chatId, "\uD83C\uDFAB У вас недостаточно тикетов для участия в игре!\n" +
                                "\n" +
                                "Вы можете:\n" +
                                "1\uFE0F⃣ Купить тикеты в нашем магазине (/tradeBalls)\n" +
                                "2\uFE0F⃣ Заработать тикеты в других играх нашего бота\n" +
                                "3\uFE0F⃣ Получить тикеты в качестве ежедневного приза\n" +
                                "\n" +
                                "Нажмите кнопку \"Купить тикеты\" или \"Ежедневный приз\" ниже, чтобы пополнить свой баланс!");
                        return;
                    }
                    if (data.contains("cube")){
                        util.disableButton(chatId, msgId);
                        util.sendMessageToUser(chatId,"\uD83C\uDFB2 <b>Вы выбрали игру \"Кубик\"</b>! \uD83C\uDFB2\n" +
                                "\n" +
                                "Сделайте выбор:", List.of("\uD83D\uDD35 Чет", "\uD83D\uDD34 Нечет"),
                                List.of("cube_even", "cube_odd"), 1);
                    } else if (data.contains("basket")) {
                        util.disableButton(chatId, msgId);
                        util.sendMessageToUser(chatId, "<b>Вы выбрали игру Баскетбол:</b>\n", List.of("Кинуть"), List.of("miniBasketBall_process"), 1);
                    }
                } else {
                    log.warn("UserCallbackHanlers -> не найдена кнопка -> " + data);
                }
        }
    }
}