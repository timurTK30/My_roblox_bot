package com.example.demo.domain;

import lombok.Getter;

@Getter
public enum QuestCommands {

    ADD_GAME_FOR_QUEST("Добавить игру для квеста"),
    ADD_DECRIPCION_FOR_QUEST("Добавить описание для квеста"),
    ADD_REWARD_FOR_QUEST("Добавить награду для квеста");

    private final String cmdName;

    QuestCommands(String cmdName) {
        this.cmdName = cmdName;
    }
}
