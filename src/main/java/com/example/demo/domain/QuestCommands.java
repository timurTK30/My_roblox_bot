package com.example.demo.domain;

import lombok.Getter;

@Getter
public enum QuestCommands {

    ADD_GAME_FOR_QUEST("Добавить игру для квеста", true),
    ADD_DECRIPCION_FOR_QUEST("Добавить описание для квеста", true),
    ADD_REWARD_FOR_QUEST("Добавить награду для квеста", true),
    EDIT_QUEST("Редоктировать", false);

    private final String cmdName;
    private final boolean isCreateNewQuest;

    QuestCommands(String cmdName, boolean isCreateNewQuest) {
        this.cmdName = cmdName;
        this.isCreateNewQuest = isCreateNewQuest;
    }
}
