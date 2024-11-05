package com.example.demo.handlers.service;

import com.example.demo.domain.Role;
import com.example.demo.dto.UserDto;
import com.example.demo.handlers.AdminCommandsHandler;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class CommandService {

    private final UserService userService;
    private final AdminCommandsHandler adminHandler;

    public void handleCommand(Message message){
        Long chatId = message.getChatId();
        String text = message.getText();
        try {
            UserDto userByChatId = userService.getUserByChatId(chatId);
            Boolean userAdmin = userService.isUserAdmin(userByChatId.getChatId());

            if (userAdmin && adminHandler.canHandle(text)){

            }
        }
    }
}
