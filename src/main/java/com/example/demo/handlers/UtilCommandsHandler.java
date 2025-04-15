package com.example.demo.handlers;

import com.example.demo.config.BotSender;
import com.example.demo.domain.*;
import com.example.demo.dto.GameDto;
import com.example.demo.dto.SuportMassageDto;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;

@Component
@Slf4j
@RequiredArgsConstructor
public class UtilCommandsHandler {

    private static final Integer BET_AMOUNT_TICKET = 2;
    private final BotSender botSender;
    private final UserService userService;
    private final SupportMassageService supportMassageService;
    private final AdminUserService adminUserService;
    private final GameService gameService;
    private final WalletService walletService;
    private final TKBallService tkBallService;

    @Transactional
    public void adminRegister(Long chatId, String userName) {
        AdminUser adminUser = new AdminUser();
        adminUser.setNickname(userName);
        adminUser.setAStatus(AdminStatus.DONT_WRITE);
        adminUser.setTempChatIdForReply(0L);
        adminUser.setRole(Role.ADMIN);
        adminUser.setChatId(chatId);
        adminUser.setDateOfRegisterAcc(LocalDate.now());
        adminUser.setStatus(UserStatus.DONT_SENT);
        AdminUser saved = adminUserService.save(adminUser);
        Wallet wallet = new Wallet(saved);
        walletService.save(wallet);
    }

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

    public boolean checkIfPrizeCoin(PrizeWebAppData prizeWebAppData) {
        return prizeWebAppData.getName().contains("Coin");
    }

    public List<String> removeSignAndEnglishLetter(List<String> commandsList) {
        return commandsList.stream()
                .map(command -> command.replaceAll("[^а-яА-ЯёЁ\\s]", "").trim()).toList();
    }

    public void processMiniGameCube(Long chatId, String selectedBet, Integer msgId) {
        deleteMsg(chatId, msgId);
        SendDice sendDice = new SendDice();
        sendDice.setChatId(chatId);
        sendDice.setEmoji("\uD83C\uDFB2");
        boolean isWin = false;
        String gameResult = "Выпало число: ";
        try {
            Message execute = botSender.execute(sendDice);
            Integer value = execute.getDice().getValue();
            gameResult += value + "\n";

            if (selectedBet.contains("even")) {
                isWin = value % 2 == 0;
                gameResult += isWin ? "Число чётное — вы выиграли!" : "Число нечётное — вы проиграли.";
            } else {
                isWin = (value % 2 == 1);
                gameResult += isWin ? "Число нечётное — вы выиграли!" : "Число чётное — вы проиграли.";
            }
            tkBallService.updateBalanceAfterMiniGame(chatId, 2L, isWin);
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            String finalGameResult = gameResult;
            scheduler.schedule(() -> {
                sendMessageToUser(chatId, finalGameResult, List.of("Сыграть еще раз", "Вернуться в меню игр"), List.of("miniGame_cube", "/miniGames"), 1);
            }, 3500, TimeUnit.MILLISECONDS);


        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void processMiniGameBasketball(Long chatId, String selectetBet, Integer msgId){
        deleteMsg(chatId, msgId);
        SendDice sendDice = new SendDice();
        sendDice.setChatId(chatId);
        sendDice.setEmoji("\uD83C\uDFC0");
        Boolean isWin = false;
        String gameResult = "";

        try {
            Message execute = botSender.execute(sendDice);
            Integer value = execute.getDice().getValue();
            System.out.println(value);
            if(value == 1 || value == 2 || value == 3){
                gameResult = "Вы проиграли";
            } else if (value == 4 || value == 5) {
                isWin = true;
                gameResult = "Вы виграли";
            }
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            String finalGameResult = gameResult;
            scheduler.schedule(() -> {
                sendMessageToUser(chatId, finalGameResult, List.of("Играть еще раз", "Вернуться в меню игр"), List.of("miniGame_basket", "/miniGames"), 1);
            }, 3500, TimeUnit.MILLISECONDS);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    public void miniGamesMsg(Long chatId, Integer msgId) {
        if (msgId != null){
            disableButton(chatId, msgId);
        }
        if (!tkBallService.isEnoughTickets(chatId, 2L)){
            sendMessageToUser(chatId, "\uD83C\uDFAB У вас недостаточно тикетов для участия в игре!\n" +
                    "\n" +
                    "Вы можете:\n" +
                    "1\uFE0F⃣ Купить тикеты в нашем магазине (/tradeBalls)\n" +
                    "2\uFE0F⃣ Заработать тикеты в других играх нашего бота\n" +
                    "3\uFE0F⃣ Получить тикеты в качестве ежедневного приза\n" +
                    "\n" +
                    "Нажмите кнопку \"Купить тикеты\" или \"Ежедневный приз\" ниже, чтобы пополнить свой баланс!");
            return;
        }
        sendMessageToUser(chatId, "<b>Привет! \uD83C\uDF89 Готов испытать удачу?</b>\n" +
                        "Ты можешь выиграть токены (Тк), играя в наши мини-игры!\n" +
                        "\uD83D\uDCB0Цена игры 2Тк \n" +
                        "\n" +
                        "\uD83D\uDCB3 <b>Как играть?</b>\n" +
                        "1\uFE0F⃣ Выбирай игру, нажав на кнопку.\n" +
                        "2\uFE0F⃣ Бот случайным образом выберет результат.\n" +
                        "3\uFE0F⃣ Если повезёт – ты получишь токены!\n" +
                        "\n" +
                        "\uD83C\uDF9F <b>Используй токены, чтобы получить призы!</b>\n" +
                        "\n" +
                        "\uD83D\uDE80 Начнем? Выбери игру ниже!", List.of("\uD83C\uDFB2", "\uD83C\uDFAF", "\uD83C\uDFC0", "\uD83C\uDFB0"),
                List.of("miniGame_cube", "miniGame_darts", "miniGame_basket", "miniGame_roulette"), 2);
    }

    public void sendWebAppReplyKeyboard(Long chatId, String massageText, String url, String buttonText) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(massageText);

        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(url);

        KeyboardButton webAppButton = new KeyboardButton();
        webAppButton.setText(buttonText);
        webAppButton.setWebApp(webAppInfo);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(webAppButton);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(List.of(keyboardRow));

        message.setReplyMarkup(keyboardMarkup);

        try {
            botSender.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void disableButton(Long chatId, Integer msgId) {
        //editMsg(chatId, msgId, msg);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(new ArrayList<>());

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(msgId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        try {
            botSender.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendWebAppInlineKeyboard(Long chatId, String massageText, String url, String buttonText) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(massageText);

        WebAppInfo webAppInfo2 = new WebAppInfo();
        webAppInfo2.setUrl(url);

        InlineKeyboardButton webAppButton2 = new InlineKeyboardButton();
        webAppButton2.setText(buttonText);
        webAppButton2.setWebApp(webAppInfo2);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(List.of(webAppButton2)));

        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            botSender.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isUserHasSpecificGame(Long chatId, Long gameId) {
        User userByChatId = userService.getUserByChatId(chatId);
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
        User userByChatId = userService.getUserByChatId(chatId);
        return userByChatId != null;
    }

    public boolean isUserAdmin(Long chatId) {
        User userByChatId = userService.getUserByChatId(chatId);
        return userByChatId.getRole().name().equalsIgnoreCase(Role.ADMIN.name());
    }

    public boolean checkListForNulls(Quest quest) {
        return ObjectUtils.allNotNull(
                quest.getId(), quest.getReward(), quest.getGame()
        );
    }

    public void requestToBuySub(String data, Long chatId) {
        String sub = data.replaceAll("request_buy_", "");
        User userByChatId = userService.getUserByChatId(chatId);
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

    public void allGames(Long chatId) {
        List<GameDto> gameDtos = gameService.readAll();
        StringBuilder stringBuilder = new StringBuilder();
        gameDtos.forEach(gameDto -> {
            stringBuilder.append(gameDto.getName())
                    .append(" ( /game").append(gameDto.getId()).append(" )")
                    .append("\n");
        });
        sendMessageToUser(chatId, stringBuilder.toString());
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
