package com.example.demo.handlers.service;

import com.example.demo.domain.Role;
import com.example.demo.dto.UserDto;
import com.example.demo.handlers.AdminCommandsHandler;
import com.example.demo.handlers.UserCommandsHandler;
import com.example.demo.handlers.UtilCommandsHandler;
import com.example.demo.service.UserService;
import com.example.demo.util.CommandData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandService {

    private final UserService userService;
    private final AdminCommandsHandler adminHandler;
    private final UserCommandsHandler userHandler;
    private final UtilCommandsHandler utilHandler;

    public void handleCommand(Message message){
        Long chatId = message.getChatId();
        String text = message.getText();
        CommandData commandData = new CommandData(text, message.getMessageId());
        try {
            UserDto userByChatId = userService.getUserByChatId(chatId);
            Boolean isAdmin = userService.isUserAdmin(userByChatId.getChatId());

            if (isAdmin && adminHandler.canHandle(commandData)){
                adminHandler.handle(chatId, commandData);
            } else if (userHandler.canHandle(commandData)) {
                userHandler.handle(chatId, commandData);
            } else {
                utilHandler.sendMessageToUser(chatId, "Неизвестная команда. Используйте /help для списка команд.");
            }
        } catch (Exception e){
            log.error("неопозноная команда: {}", text, e);
        }
    }
}
