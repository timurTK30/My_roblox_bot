package com.example.demo.handlers.service;

import com.example.demo.handlers.UtilCommandsHandler;
import com.example.demo.handlers.user.UserCallbackHanlers;
import com.example.demo.service.UserService;
import com.example.demo.util.CommandData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@RequiredArgsConstructor
public class CallbackService {

    private static final Logger log = LoggerFactory.getLogger(CallbackService.class);
    private final UserCallbackHanlers userCallback;
    private final UserService userService;
    private final UtilCommandsHandler utilHandler;

    public void handleCallback(CallbackQuery callback) {
        String data = callback.getData();
        Long chatId = callback.getMessage().getChatId();
        String userName = callback.getFrom().getUserName();
        CommandData commandData = new CommandData(data, callback.getMessage().getMessageId(), chatId, callback.getId(), userName);
        try {
            Boolean isAdmin = userService.isUserAdmin(chatId);
            if (isAdmin) {

            } else if (userCallback.canHandle(commandData)) {
                userCallback.handle(chatId, commandData);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("handleCallback- там ошибка, " + commandData.toString());
        }
    }
}
