package com.example.demo.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class CommandData {

    private final String data;
    private final Integer msgId;
    private final Long chatId;
    private String callBackId;
    private String userName;

    public CommandData(String data, Integer msgId, Long chatId) {
        this.data = data;
        this.msgId = msgId;
        this.chatId = chatId;
    }

    public CommandData(String data, Integer msgId, Long chatId, String userName) {
        this.userName = userName;
        this.chatId = chatId;
        this.msgId = msgId;
        this.data = data;
    }


}
