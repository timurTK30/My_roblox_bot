package com.example.demo.handlers;

import com.example.demo.config.BotSender;
import com.example.demo.domain.Creator;
import com.example.demo.domain.PrizeWebAppData;
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
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.*;

import static java.util.Collections.emptyList;

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
        outputQuestWithCustomBtn(chatId, quest, btn, emptyList());
    }

    public boolean checkIfPrizeCoin(PrizeWebAppData prizeWebAppData){
        return prizeWebAppData.getName().contains("Coin");
    }

    public List<String> removeSignAndEnglishLetter(List<String> commandsList) {
        return commandsList.stream()
                .map(command -> command.replaceAll("[^а-яА-ЯёЁ\\s]", "").trim()).toList();
    }

    public boolean isUserHasSpecificGame(Long chatId, Long gameId) {
        UserDto userByChatId = userService.getUserByChatId(chatId);
        //return userByChatId.getGame() != null && userByChatId.getGame().getId().equals(gameId);
        return Objects.nonNull(userByChatId.getGame()) && userByChatId.getGame().getId().equals(gameId);
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
        sendMessageToUser(chatId, massage, emptyList(), emptyList(), 0);
    }

    public void sendMessageToUser(Long chatId, String massage, List<String> buttonText, int buttonRows) {
        sendMessageToUser(chatId, massage, buttonText, emptyList(), buttonRows);
    }

    public void sendMessageToUser(Long chatId, String massage, List<String> buttonText, List<String> callBackQuery, int buttonRows) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(massage);
        sendMessage.enableHtml(true);

        if (!buttonText.isEmpty()) {
            InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, callBackQuery, buttonRows);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }

        try {
            botSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPhotoToUser(Long chatId, String url, String massage, List<String> buttonText, List<String> callbacks, int buttonRows) {
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

    public void showAlert(String callBackId, String text) {
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

    public void sendPhotoToUser(Long chatId, String url, String massage, List<String> buttonText, int buttonRows) {
        sendPhotoToUser(chatId, url, massage, buttonText, emptyList(), buttonRows);
    }

    public void sendGifToUser(Long chatId, String url, String massage, List<String> buttonText, int buttonRows) {
        sendGifToUser(chatId, url, massage, buttonText, emptyList(), buttonRows);
    }

    public void sendGifToUser(Long chatId, String url, String massage, List<String> buttonText, List<String> callback, int buttonRows) {
        SendAnimation sendAnimation = new SendAnimation();
        sendAnimation.setChatId(chatId);

        InputFile inputFile = new InputFile(new File(url));
        sendAnimation.setAnimation(inputFile);
        sendAnimation.setCaption(massage);
        sendAnimation.setParseMode("HTML");

        if (buttonText != null) {
            InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, callback, buttonRows);
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

    public void editKeyboard(Long chatId, Integer msgId, List<String> buttons, int rows) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(msgId);
        editMessageReplyMarkup.setReplyMarkup(createCustomKeyboard(buttons, rows));

        try {
            botSender.execute(editMessageReplyMarkup);
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

        return createCustomKeyboard(buttonText, emptyList(), rows);
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

    public void sendTypingStatus(Long chatId) {
        SendChatAction action = new SendChatAction();
        action.setChatId(chatId);
        action.setAction(ActionType.TYPING);
        try {
            botSender.execute(action);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void showAllDescription(StringBuilder stringBuilder, GameDto gameDto) {
        String tempCreatorGroup = Optional.ofNullable(gameDto.getCreator())
                .map(Creator::getNameOfGroup)
                .orElse("Неизвестный разработчик");

        Map<String, String> gameDetails = Map.of(
                "\uD83C\uDF1F Название игры", gameDto.getName(),
                "\uD83D\uDCD6 Описание", gameDto.getDescription(),
                "\uD83C\uDFAE Жанр", gameDto.getGameGenre(),
                "\uD83D\uDCB0 Цена", String.valueOf(gameDto.getPrice()),
                "\uD83D\uDC68\uD83C\uDFFC\u200D\uD83D\uDCBB Aктив", String.valueOf(gameDto.getActive()),
                "\uD83C\uDFE2 Разработчик", tempCreatorGroup,
                "\uD83D\uDDD3 Дата создания", gameDto.getCreateDate().toString()
        );

        for (Map.Entry<String, String> entry : gameDetails.entrySet()) {
            stringBuilder
                    .append("<b>").append(entry.getKey()).append(": </b>")
                    .append(entry.getValue())
                    .append("\n\n");
        }
    }

}
