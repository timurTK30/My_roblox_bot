package com.example.demo.handlers.service;

import com.example.demo.handlers.UserCallbackHanlers;
import com.example.demo.handlers.UserCommandsHandler;
import com.example.demo.handlers.UtilCommandsHandler;
import com.example.demo.service.UserService;
import com.example.demo.util.CommandData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@RequiredArgsConstructor
public class CallbackService {

    private final UserCallbackHanlers userCallback;
    private final UserService userService;
    private final UtilCommandsHandler utilHandler;

    public void handleCallback(CallbackQuery callback){
        String data = callback.getData();
        Long chatId = callback.getMessage().getChatId();
        CommandData commandData = new CommandData(data, callback.getMessage().getMessageId());
        try {
            Boolean isAdmin = userService.isUserAdmin(chatId);
            if (isAdmin){

            } else if (userCallback.canHandle(commandData)) {
                userCallback.handle(chatId, commandData);
            }
        } catch (Exception e){
            throw new RuntimeException("handleCallback- там ошибка");
        }
    }
}
