package com.example.demo.domain;

import lombok.Getter;

@Getter
public enum Buttons {

    REGISTER("Зарегистрировать в системе"),
    WRITE_ADMIN("Написать админу"),
    ANSWER_GOOD("😀"),
    ANSWER_BAD("😡"),
    ALL_GAMES("ALL"),
    HORROR("HORROR"),
    ADVENTURE("ADVENTURE"),
    SHOOTER("SHOOTER"),
    TYCOON("TYCOON"),
    SURVIVAL("SURVIVAL"),
    USER("User"),
    LEAVE_REQUEST("Оставить заяву"),
    SHOW_FRIENDS("Показать друзей"),
    LEAVE("Оставить"),
    BUY("Купить");


    private final String cmd;

    Buttons(String cmd) {
        this.cmd = cmd;
    }
}
