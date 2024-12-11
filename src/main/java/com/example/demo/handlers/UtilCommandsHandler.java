package com.example.demo.handlers;

import com.example.demo.config.BotSender;
import com.example.demo.domain.Quest;
import com.example.demo.domain.Role;
import com.example.demo.dto.GameDto;
import com.example.demo.dto.SuportMassageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.service.SupportMassageService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
    private final UserService userService;
    private final SupportMassageService supportMassageService;

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

    public List<String> removeSignAndEnglishLetter(List<String> commandsList) {
        return commandsList.stream()
                .map(command -> command.replaceAll("[^а-яА-ЯёЁ\\s]", "").trim()).toList();
    }

    public void showShortDescription(StringBuilder stringBuilder, int i, GameDto gameDto, String tempCreatorGroup) {
        if (gameDto.getCreator() != null) {
            tempCreatorGroup = gameDto.getCreator().getNameOfGroup();
        }

        stringBuilder.append(i + 1)
                .append(". ")
                .append("<b>").append("\uD83C\uDF1F Название игры: ")
                .append(gameDto.getName()).append("</b>")
                .append("( /game" + gameDto.getId() + " )")
                .append("\n")
                .append("\n")
                .append("<b>").append("\uD83C\uDFAE Жанр: ").append("</b>")
                .append(gameDto.getGameGenre())
                .append("\n")
                .append("\n")
                .append("<b>").append("\uD83D\uDCB0 Цена: ").append("</b>")
                .append(gameDto.getPrice())
                .append("\n")
                .append("\n")
                .append("<b>").append("\uD83D\uDC68\uD83C\uDFFC\u200D\uD83D\uDCBB Aктив: ").append("</b>")
                .append(gameDto.getActive());
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

    public void sendPhotoToUser(Long chatId, String url, String massage, List<String> buttonText,List<String> callbacks, int buttonRows) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);

        InputFile inputFile = new InputFile(new File(url));
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setCaption(massage);
        sendPhoto.setParseMode("HTML");
        if (buttonText != null) {
            InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, callbacks, buttonRows);
            sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        }
        try {
            botSender.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void showAlert(String callBackId, String text){
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callBackId);
        answerCallbackQuery.setText(text);
        answerCallbackQuery.setShowAlert(true);
        try {
            botSender.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPhotoToUser(Long chatId, String url, String massage, List<String> buttonText, int buttonRows){
        sendPhotoToUser(chatId, url, massage, buttonText, Collections.emptyList(), buttonRows);
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

    public void editMsg(Long chatId, Integer msgId, String newText) {
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

    public void deleteMsg(Long chatId, Integer msgId) {
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

    public boolean checkListForNulls(Quest quest) {
        return ObjectUtils.allNotNull(
                quest.getId(), quest.getReward(), quest.getGame()
        );
    }

    public void requestToBuySub(String data, Long chatId) {
        String sub = data.replaceAll("request_buy_", "");
        UserDto userByChatId = userService.getUserByChatId(chatId);
        sendMessageToUser(1622241974L, "Имя: " + userByChatId.getNickname() + "\n" +
                "Подписка: " + userByChatId.getRole() + "\n" +
                "Хочет купить: " + sub + "\n" +
                "Для связи: @" + userByChatId.getNickname() + "\n" +
                "/set_role" + userByChatId.getChatId());
    }

    public boolean isSuppMsgExistByUserChatId(Long chatId) {
        Optional<SuportMassageDto> massageByChatId = supportMassageService.getMassageByChatId(chatId);
        return massageByChatId.isPresent();
    }

    public void showAllDescription(StringBuilder stringBuilder, GameDto gameDto, String tempCreatorGroup) {
        if (gameDto.getCreator() != null) {
            tempCreatorGroup = gameDto.getCreator().getNameOfGroup();
        }

        stringBuilder.append(1)
                .append(". ")
                .append("<b>").append("\uD83C\uDF1F Название игры: ")
                .append(gameDto.getName()).append("</b>")
                .append("\n")
                .append("\n")
                .append("<b>").append("\uD83D\uDCD6 Описание:").append("</b>")
                .append("\n")
                .append(gameDto.getDescription())
                .append("\n")
                .append("\n")
                .append("<b>").append("\uD83C\uDFAE Жанр: ").append("</b>")
                .append(gameDto.getGameGenre())
                .append("\n")
                .append("\n")
                .append("<b>").append("\uD83D\uDCB0 Цена: ").append("</b>")
                .append(gameDto.getPrice())
                .append("\n")
                .append("\n")
                .append("<b>").append("\uD83D\uDC68\uD83C\uDFFC\u200D\uD83D\uDCBB Aктив: ").append("</b>")
                .append(gameDto.getActive())
                .append("\n")
                .append("\n")
                .append("<b>").append("\uD83C\uDFE2 Разработчик: ").append("</b>")
                .append(tempCreatorGroup)
                .append("\n")
                .append("\n")
                .append("<b>").append("\uD83D\uDDD3 Дата создания:").append("</b>")
                .append(gameDto.getCreateDate());
    }

}
