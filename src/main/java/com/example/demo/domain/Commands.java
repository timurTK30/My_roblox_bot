package com.example.demo.domain;

import lombok.Getter;

@Getter
public enum Commands {

    START("/start", "🛫Старт", false, false),
    HELP("/help", "💁Помошь", false, true),
    GAMES("/games", "🎮Игры", false, true),
    READ_SUPP_MSG("/readSuppMsg", "Прочитать сообщение от юзера", true, true),
    GAME("/game", "🧭Определенная игра", false, false),
    BUY_SUBSCRIBE("/buy_subscribe", "📨Купить подписки", false, true),
    SET_ROLE("/set_role", "👨->🧑Изменить роль", true, true),
    PROFILE("/profile","ℹ️Профиль", false, true);

    private final String cmd;
    private final String cmdName;
    private final boolean isCmdAdmin;
    private final boolean isNeedToWath;

    Commands(String cmd, String cmdName, boolean isCmdAdmin, boolean isNeedToWath) {
        this.cmd = cmd;
        this.cmdName = cmdName;
        this.isCmdAdmin = isCmdAdmin;
        this.isNeedToWath = isNeedToWath;
    }
}
