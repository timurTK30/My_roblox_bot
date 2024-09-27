package com.example.demo.domain;

import lombok.Getter;

@Getter
public enum Commands {

    START("/start", "🛫Старт", false, false, false),
    MENU("/menu", "Меню возможностей", false, false , false),
    HELP("/help", "💁Помошь", false, true , false),
    GAMES("/games", "🎮Игры", false, true , false),
    READ_SUPP_MSG("/readSuppMsg", "Прочитать сообщение от юзера", true, true, false),
    GAME("/game", "🧭Определенная игра", false, false, false),
    BUY_SUBSCRIBE("/buy_subscribe", "📨Купить подписки", false, true, false),
    SET_ROLE("/set_role", "👨->🧑Изменить роль", true, false, false),
    RESTART("/restart", "Перезагрузить бота", true, true, false),
    PROFILE("/profile","ℹ️Профиль", false, true, false),
    STATISTISC("/statistics", "📊 Статистика использования бота", true, true, false),
    NOTIFY_ALL_USERS("/notifyAllUsers", "✉\uFE0F Отправить сообщение", true, true, false),
    SHOW_QUESTS("/quests", "Доступные квесты", false, true, false),
    CREATE_QUESTS("/create_quests", "Создать квест", true, true, true),
    ADD_GAME_FOR_QUEST("/addGameForQuest", "Добавить игру для квеста", true, true, true),
    ADD_DECRIPCION_FOR_QUEST("/addDescriptionForQuest", "Добавить описание для квеста", true, true, true);

    private final String cmd;
    private final String cmdName;
    private final boolean isCmdAdmin;
    private final boolean isNeedToShow;
    private final boolean isQuest;

    Commands(String cmd, String cmdName, boolean isCmdAdmin, boolean isNeedToShow, boolean isQuest) {
        this.cmd = cmd;
        this.cmdName = cmdName;
        this.isCmdAdmin = isCmdAdmin;
        this.isNeedToShow = isNeedToShow;
        this.isQuest = isQuest;
    }
}
