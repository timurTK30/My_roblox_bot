package com.example.demo.handlers.service;

import com.example.demo.handlers.UserCallbackHanlers;
import com.example.demo.handlers.UserCommandsHandler;
import com.example.demo.handlers.UtilCommandsHandler;
import com.example.demo.service.UserService;
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
        try {
            Boolean isAdmin = userService.isUserAdmin(chatId);
            if (isAdmin){

            } else if (userCallback.canHandle(data)) {
                userCallback.handle(chatId, data);
            }
            utilHandler.sendMessageToUser(chatId, data + " -> " + userCallback.canHandle(data));
        } catch (Exception e){
            throw new RuntimeException("handleCallback- там ошибка");
        }
    }
}
