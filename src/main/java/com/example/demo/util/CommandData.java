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
}
