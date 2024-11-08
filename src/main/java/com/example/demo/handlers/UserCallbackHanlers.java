package com.example.demo.handlers;

public class UserCallbackHanlers implements BasicHandlers{


    @Override
    public boolean canHandle(String callbackData) {
        return callbackData.matches(
                "(Зарегистрировать в системе|😀|😡|ALL|HORROR|ADVENTURE" +
                        "|SHOOTER|TYCOON|SURVIVAL|Оставить заяву.*)"
        );
    }

    @Override
    public void handle(Long chatId, String text) {

    }
}
