package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AdminCommands {

    READ_SUPP_MSG("/readSuppMsg", "Прочитать сообщение от юзера"),
    SET_ROLE("/set_role", "\uD83D\uDC68->\uD83E\uDDD1Изменить роль"),
    RESTART("/restart", "Перезагрузить бота"),
    STATISTICS("/statistics", "\uD83D\uDCCA Статистика использования бота"),
    NOTIFY_ALL_USERS("/notifyAllUsers", "\u2709\uFE0F Отправить сообщение"),
    SHOW_QUESTS("/quests", "Доступные квесты"),
    CREATE_QUESTS("/create_quests", "Создать квест"),
    QUEST_MENU("/questMenu", "Квест меню"),
    CLEAR_ALL_DEPRECATED("/clearAllDeprecated", "Удалить старие квесты"),
    MENU("/menu", "Меню возможностей");

    private final String cmd;
    private final String text;
}
