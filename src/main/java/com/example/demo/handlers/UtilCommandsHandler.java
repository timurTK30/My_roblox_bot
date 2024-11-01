package com.example.demo.handlers;

import com.example.demo.config.BotSender;
import com.example.demo.domain.Quest;
import com.example.demo.domain.Role;
import com.example.demo.dto.SuportMassageDto;
import com.example.demo.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UtilCommandsHandler {

    private final BotSender botSender;

    public void outputQuestWithCustomBtn(Long chatId, Quest quest, List<String> btn, List<String> callBack) {
        String status = quest.isDeprecated() ? "❌ Неактуальный" : "✅ Актуальный";
        String gameName = quest.getGame() != null ? quest.getGame().getName() : "нет игры";
        String format = String.format(
                "🎮 <b>Квест для игры:</b> %s \n\n" +
                        "📝 <b>Описание:\n</b>%s\n\n" +
                        "🏆 <b>Награда:</b>\n%s\n\n" +
                        "👤 <b>Создатель квеста:</b>\n%s\n\n" +
                        "📅 <b>Состояние:</b>\n%s",
                gameName,
                quest.getDescription(),
                quest.getReward(),
                quest.getCreatorOfQuest().getNickname(),
                status);

        sendMessageToUser(chatId, format, btn, callBack, btn.size());
    }

    public void outputQuestWithCustomBtn(Long chatId, Quest quest, List<String> btn) {
        outputQuestWithCustomBtn(chatId, quest, btn, Collections.emptyList());
    }

    public void sendMessageToUser(Long chatId, String massage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(massage);
        sendMessage.enableHtml(true);
        try {
            botSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO скомбинировать три метода по отправки сообщения
    public void sendMessageToUser(Long chatId, String massage, List<String> buttonText, int buttonRows) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(massage);
        sendMessage.enableHtml(true);

        InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, buttonRows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            botSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToUser(Long chatId, String massage, List<String> buttonText, List<String> callBackQuery, int buttonRows) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(massage);
        sendMessage.enableHtml(true);

        InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, callBackQuery, buttonRows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            botSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPhotoToUser(Long chatId, String url, String massage, List<String> buttonText, int buttonRows) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);

        InputFile inputFile = new InputFile(new File(url));
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setCaption(massage);
        sendPhoto.setParseMode("HTML");
        if (buttonText != null) {
            InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, buttonRows);
            sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        }
        try {
            botSender.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendGifToUser(Long chatId, String url, String massage, List<String> buttonText, int buttonRows) {
        SendAnimation sendAnimation = new SendAnimation();
        sendAnimation.setChatId(chatId);

        InputFile inputFile = new InputFile(new File(url));
        sendAnimation.setAnimation(inputFile);
        sendAnimation.setCaption(massage);
        sendAnimation.setParseMode("HTML");

        if (buttonText != null) {
            InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, buttonRows);
            sendAnimation.setReplyMarkup(inlineKeyboardMarkup);
        }

        try {
            botSender.execute(sendAnimation);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void editMsg(Long chatId, Integer msgId, String newText) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(msgId);
        editMessageText.setText(newText);

        try {
            botSender.execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteMsg(Long chatId, Integer msgId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(msgId);

        try {
            botSender.execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private InlineKeyboardMarkup createCustomKeyboard(List<String> buttonText, int rows) {

        return createCustomKeyboard(buttonText, Collections.emptyList(), rows);
    }

    private InlineKeyboardMarkup createCustomKeyboard(List<String> buttonText, List<String> callBackQuery, int rows) {
        if (buttonText.size() == 1) {
            rows = 1;
        }
        if (callBackQuery.isEmpty()) {
            callBackQuery = buttonText;
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        int buttonsPerRow = (int) Math.ceil((double) buttonText.size() / rows);

        int buttonIndex = 0;
        for (int i = 0; i < rows; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < buttonsPerRow && buttonIndex < buttonText.size(); j++) {
                String text = buttonText.get(buttonIndex);
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(text);
                button.setCallbackData(callBackQuery.get(buttonIndex));
                row.add(button);
                buttonIndex++;
            }
            keyboard.add(row);
        }

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public boolean isUserExist(Long chatId) {
        UserDto userByChatId = userService.getUserByChatId(chatId);
        return userByChatId != null;
    }

    public boolean isUserAdmin(Long chatId) {
        UserDto userByChatId = userService.getUserByChatId(chatId);
        return userByChatId.getRole().equalsIgnoreCase(Role.ADMIN.name());
    }

    public boolean isSuppMsgExistByUserChatId(Long chatId) {
        Optional<SuportMassageDto> massageByChatId = supportMassageServiceImpl.getMassageByChatId(chatId);
        return massageByChatId.isPresent();
    }


}