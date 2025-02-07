package com.example.demo.handlers.admin;

import com.example.demo.domain.Quest;
import com.example.demo.handlers.BasicHandlers;
import com.example.demo.handlers.UtilCommandsHandler;
import com.example.demo.util.CommandData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.demo.domain.QuestCommands.*;

@Service
@RequiredArgsConstructor
public class AdminCallbackHandlers implements BasicHandlers {

    private final AdminCommandsHandler adminCommandsHandler;
    private final UtilCommandsHandler util;

    @Override
    public boolean canHandle(CommandData commandData) {
        String data = commandData.getData();

        return data.matches(
                "(^Прочитать сообщение от юзера|Перезагрузить бота|Статистика использования бота" +
                        "|Доступные квесты|Квест меню|Создать квест|Удалить старие квесты" +
                        "|Прочитать доступные игры|User.*|Отправить сообщение|.*Редоктировать.*" +
                        "|Добавить награду для квеста.*|Добавить описание для квеста.*" +
                        "|Добавить игру для квеста.*|.*Изменить на.*|change_role_.*)"
        );
    }

    @Override
    public void handle(Long chatId, CommandData commandData) {
        String data = commandData.getData();
        Integer msgId = commandData.getMsgId();
        switch (data) {
            case "Прочитать сообщение от юзера":
                adminCommandsHandler.readSuppMsg(chatId);
                break;
            case "Перезагрузить бота":
                adminCommandsHandler.restart(chatId);
                break;
            case "Статистика использования бота":
                adminCommandsHandler.statistics(chatId);
                break;
            case "Доступные квесты":
                adminCommandsHandler.readAllQuestsForAdmin(chatId);
                break;
            case "Квест меню":
                adminCommandsHandler.menuForCreateQuest(chatId);
                break;
            case "Создать квест":
                adminCommandsHandler.createQuest(chatId);
                break;
            case "Удалить старие квесты":
                adminCommandsHandler.deleteDeprecatedQuest(chatId);
                break;
            case "Прочитать доступные игры":
                util.allGames(chatId);
                break;
            case "Отправить сообщение":
                adminCommandsHandler.requestToNotifyAllUsers(chatId);
                break;
            case "Меню возможностей":
                adminCommandsHandler.menuForAdmin(chatId);
                break;
            default:
                if (data.startsWith("user")) {
                    adminCommandsHandler.handleUserReplyRequest(chatId, data);
                } else if (data.contains(EDIT_QUEST.getCmdName())) {
                    Quest existQuest = adminCommandsHandler.getQuestByIdFromCallback(chatId, data);
                    adminCommandsHandler.outputQuestForAdmin(chatId, existQuest);
                } else if (data.startsWith(ADD_REWARD_FOR_QUEST.getCmdName())) {
                    adminCommandsHandler.requestToAddRewardForQuest(chatId, data);
                } else if (data.startsWith(ADD_DECRIPCION_FOR_QUEST.getCmdName())) {
                    adminCommandsHandler.requestToAddDescriptionForQuest(chatId, data);
                } else if (data.startsWith(ADD_GAME_FOR_QUEST.getCmdName())) {
                    adminCommandsHandler.requestToAddGameForQuest(chatId, data);
                } else if (data.contains("Изменить на")) {
                    adminCommandsHandler.changeQuestStatus(chatId, data);
                } else if (data.contains("change_role_")) {
                    adminCommandsHandler.updateRole(chatId, data);
                }
        }
    }
}
