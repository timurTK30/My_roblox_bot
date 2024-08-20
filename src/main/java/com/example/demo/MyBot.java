package com.example.demo;

import com.example.demo.config.BotConfig;
import com.example.demo.domain.*;
import com.example.demo.dto.GameDto;
import com.example.demo.dto.SuportMassageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.mapper.GameMapper;
import com.example.demo.mapper.SuportMassageMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.CreatorService;
import com.example.demo.service.GameService;
import com.example.demo.service.UserService;
import com.example.demo.service.serviceImp.SupportMassageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.domain.Commands.*;
import static com.example.demo.domain.UserStatus.WANT_UPDATE_MSG;

@Component
@Slf4j
public class MyBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CreatorService creatorService;
    private final GameService gameService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final SupportMassageServiceImpl supportMassageServiceImpl;
    private final SuportMassageMapper suportMassageMapper;
    private final GameMapper gameMapper;

    @Autowired
    public MyBot(BotConfig botConfig, CreatorService creatorService, GameService gameService, UserService userService, UserMapper userMapper, SupportMassageServiceImpl supportMassageServiceImpl, SuportMassageMapper suportMassageMapper, GameMapper gameMapper) {
        this.botConfig = botConfig;
        this.creatorService = creatorService;
        this.gameService = gameService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.supportMassageServiceImpl = supportMassageServiceImpl;
        this.suportMassageMapper = suportMassageMapper;
        this.gameMapper = gameMapper;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleIncomingMessage(update.getMessage());
        }

        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleIncomingMessage(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();

        if (text.startsWith(START.getCmd())) {
            wellcome(chatId);
        } else if (text.startsWith(HELP.getCmd())) {
            help(chatId);
        } else if (text.startsWith(READ_SUPP_MSG.getCmd()) && isUserAdmin(chatId)) {
            readSuppMsg(chatId);
        } else if (text.equalsIgnoreCase(GAMES.getCmd())) {
            handleGameCommand(chatId);
        } else if (text.startsWith("/game")) {
            Long gameId = Long.valueOf(text.replaceAll("\\D+", ""));
            getGameById(chatId, gameId);
        } else {
            handleUserMessage(chatId, text);
        }

    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();


        switch (data) {
            case "Зарегистрировать в системе\uD83D\uDC7E":
                register(chatId, callbackQuery);
                break;
            case "Написать админу":
                handleAdminMessage(chatId);
                break;
            case "😀":
                handlePositiveFeedback(chatId);
                break;
            case "😡":
                handleNegativeFeedback(chatId, callbackQuery);
                break;
            case "ALL":
                readGames(chatId, null, callbackQuery.getMessage().getMessageId());
                break;
            case "HORROR":
            case "ADVENTURE":
            case "SHOOTER":
            case "TYCOON":
            case "SURVIVAL":
                readGames(chatId, GameGenre.valueOf(data), callbackQuery.getMessage().getMessageId());

                break;
            default:
                if (data.startsWith("User")) {
                    handleUserReplyRequest(chatId, data);
                } else if (data.startsWith("Оставить заяву")) {
                    handleGameApplication(chatId, data);
                } else if (data.startsWith("Показать друзей")) {
                    showFriends(chatId, data);
                } else if (data.startsWith("Оставить")) {
                    sendMessageToUser(chatId, "Рано или поздно но кто-то ответит на вашу проблему");
                } else if (data.startsWith("Редактировать сообщение")) {
                    handleEditSuppMsg(chatId);
                }
                break;
        }
    }

    private void handleGameCommand(Long chatId) {
        GameGenre[] gameGenres = GameGenre.values();
        List<String> buttons = Arrays.stream(gameGenres)
                .map(Enum::toString)
                .collect(Collectors.toList());
        buttons.add("ALL");
        sendMessageToUser(chatId, "Выберите жанр", buttons, buttons.size() / 2);
    }

    private void handleEditSuppMsg(Long chatId) {
        userService.updateStatusByChatId(chatId, "WANT_UPDATE_MSG");
        sendMessageToUser(chatId, "Напишите сообщение");
    }

    private void handleUserMessage(Long chatId, String message) {
        try {
            UserDto user = userService.getUserByChatId(chatId);

            if (user.getStatus().equalsIgnoreCase("WAIT_FOR_SENT")) {
                if (saveSuppMassageFromUser(chatId, message)) {
                    sendMessageToUser(chatId, "Сообщение отправлено");
                    userService.updateStatusByChatId(chatId, "WAIT_FOR_REPLY");
                } else {
                    sendMessageToUser(chatId, "Ваше сообщение не отправлено. Извините за неполадки");
                }
            } else if (user.getRole().equalsIgnoreCase("ADMIN") && user.getAStatus().equalsIgnoreCase("WANT_REPLY")) {
                sendMessageToUser(user.getTempChatIdForReply(), message, List.of("😀", "😡"), 1);
                userService.updateAdminStatusByChatId(chatId, AdminStatus.SENT, 0L);
            } else if (user.getStatus().equalsIgnoreCase(WANT_UPDATE_MSG.name())) {

            }
        } catch (Exception e) {
            System.out.println("Человек не ожидает на отправку сообщений");
        }
    }


    private void handleAdminMessage(Long chatId) {
        if (!isSuppMsgExistByUserChatId(chatId)) {
            userService.updateStatusByChatId(chatId, "WAIT_FOR_SENT");
            sendMessageToUser(chatId, "Введите сообщение");
        } else {
            SuportMassageDto supportMessage = supportMassageServiceImpl.getMassageByChatId(chatId).orElse(null);
            if (supportMessage != null) {
                sendMessageToUser(chatId, "У вас уже есть сообщение: " + supportMessage.getMassage() + "\nдата отправки: " + supportMessage.getDate(),
                        List.of("Редактировать сообщение", "Оставить"), 1);
            }
        }
    }

    private void handleUserReplyRequest(Long chatId, String data) {
        String chatIdWaitingUser = data.replaceAll("\\D", "");
        userService.updateAdminStatusByChatId(chatId, AdminStatus.WANT_REPLY, Long.valueOf(chatIdWaitingUser));
        sendMessageToUser(chatId, "Напишите сообщение (" + chatIdWaitingUser + ")");
    }

    private void handlePositiveFeedback(Long chatId) {
        supportMassageServiceImpl.deleteByChatId(chatId);
        userService.updateStatusByChatId(chatId, "DONT_SENT");
    }

    private void handleNegativeFeedback(Long chatId, CallbackQuery callbackQuery) {
        SuportMassageDto supportMessage = supportMassageServiceImpl.getMassageByChatId(chatId).orElse(null);
        if (supportMessage != null) {
            String message = "Пользователь с ником @" + callbackQuery.getFrom().getUserName() +
                    " не одобрил помощь\n\n" + supportMessage.getMassage();
            sendMessageToUser(1622241974L, message);
        }
    }

    private void handleGameApplication(Long chatId, String data) {
        String gameName = data.replaceAll("[^A-Za-z ]", "").trim();
        GameDto gameDto = gameService.getGameByName(gameName);
        UserDto userDto = userService.getUserByChatId(chatId);
        userDto.setGame(gameMapper.toEntity(gameDto));
        userService.updateByChatId(userDto, chatId);
    }

    private void showFriends(Long chatId, String data) {
        String gameName = data.replaceAll("[^A-Za-z ]", "").trim();
        GameDto gameByName = gameService.getGameByName(gameName);
        List<UserDto> friends = userService.getUserByGameId(gameByName.getId()).stream()
                .filter(user -> !user.getChatId().equals(chatId))
                .toList();
        if (!friends.isEmpty()) {
            sendMessageToUser(chatId, "@" + friends.get(0).getNickname());
            System.out.println(friends);
        } else {
            sendMessageToUser(chatId, "Нет друзей, играющих в эту игру");
        }
    }

    public void wellcome(Long chatId) {
        String text = "Привет! \uD83D\uDE0A <b>Я бот по игре Roblox.</b> \n" +
                "Я могу показать тебе самые интересные игры в этом мире. \n" +
                "\n" +
                "От захватывающих приключений до захватывающих соревнований - <i>я знаю всё!</i> Просто напиши мне свои предпочтения, и я подберу для тебя что-то увлекательное! \uD83C\uDFAE✨\n" +
                "\n" +
                "А ещё я всегда обновляю свою базу данных, чтобы ты всегда был в курсе последних трендов и новых релизов. \n" +
                "\n" +
                "Так что не стесняйся, спрашивай обо всём, что тебе интересно!";
        sendMessageToUser(chatId, text, List.of("Зарегистрировать в системе\uD83D\uDC7E"), 1);

    }

    public String getFriendByGameId(Long gameId, Long chatId) {
        List<UserDto> userByGameId = userService.getUserByGameId(gameId).stream()
                .filter(u -> !u.getChatId().equals(chatId)).toList();
        StringBuilder stringBuilder = new StringBuilder();
        userByGameId
                .forEach(
                        user -> {
                            stringBuilder.append("@").append(user.getNickname()).append("\n");
                        }
                );

        return stringBuilder.toString();
    }

    public void help(Long chatId) {
        sendMessageToUser(chatId, "Чем вам помочь?", List.of("Написать админу"), 1);
    }

    public boolean isUserAdmin(Long chatId) {
        UserDto userByChatId = userService.getUserByChatId(chatId);
        return userByChatId.getRole().equalsIgnoreCase("ADMIN");
    }

    public boolean isSuppMsgExistByUserChatId(Long chatId) {
        Optional<SuportMassageDto> massageByChatId = supportMassageServiceImpl.getMassageByChatId(chatId);
        return massageByChatId.isPresent();
    }

    public void readSuppMsg(Long chatId) {
        List<SuportMassageDto> massageDtos = supportMassageServiceImpl.readAll();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < massageDtos.size(); i++) {
            stringBuilder.append(i + 1)
                    .append(". ")
                    .append(massageDtos.get(i).getChatId())
                    .append(" ")
                    .append(massageDtos.get(i).getMassage()).append("\n");
        }
        sendMessageToUser(chatId, stringBuilder.toString(), massageDtos.stream().
                map(suppMsg -> suportMassageMapper.toUserChatInfo(suppMsg).toString()).toList(), massageDtos.size());
    }

    public void getGameById(Long chatId, Long gameId) {
        StringBuilder stringBuilder = new StringBuilder();
        String tempCreatorId = "пусто";
        Optional<GameDto> gameByGameId = gameService.getGameByGameId(gameId);
        gameByGameId.ifPresent(gameDto -> {
            showAllDescription(stringBuilder, gameDto, tempCreatorId);
            if(gameDto.getGif() != null){
                sendGifToUser(chatId, gameDto.getGif(), stringBuilder.toString(), List.of("Оставить заяву для: " + gameDto.getName(), "Показать друзей для игры: " + gameDto.getName()), 1);
            }else {
                sendPhotoToUser(chatId, gameDto.getPhoto(), stringBuilder.toString(), List.of("Оставить заяву для: " + gameDto.getName(), "Показать друзей для игры: " + gameDto.getName()), 1);
            }

        });
    }

    public void readGames(Long chatId, GameGenre genre, Integer msgId) {
        List<GameDto> gameByGenre;

        if (genre != null) {
            gameByGenre = gameService.getGameByGenre(genre);
        } else {
            gameByGenre = gameService.readAll();
        }
        if (gameByGenre.isEmpty()) {
            sendMessageToUser(chatId, "\uD83C\uDF1F Извините за неудобства, но игр с таким жанром пока что нет. \uD83C\uDF1F");
        }
        StringBuilder stringBuilder = new StringBuilder();
        //TODO
        String tempCreatorGroup = "пусто";
        for (int i = 0; i < gameByGenre.size(); i++) {
            GameDto gameDto = gameByGenre.get(i);
            showShortDescription(stringBuilder, i, gameDto, tempCreatorGroup);


            editMsgMedia(chatId, msgId, gameDto.getPhoto(), stringBuilder.toString(), List.of("Оставить заяву для: " + gameDto.getName(), "Показать друзей для игры: " + gameDto.getName()), 1);
            stringBuilder.setLength(0);
        }
    }

    private void showShortDescription(StringBuilder stringBuilder, int i, GameDto gameDto, String tempCreatorGroup) {
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

    private void showAllDescription(StringBuilder stringBuilder, GameDto gameDto, String tempCreatorGroup) {
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

    public boolean saveSuppMassageFromUser(Long chatId, String massage) {
        try {
            Optional<SuportMassageDto> massageByChatId = supportMassageServiceImpl.getMassageByChatId(chatId);
            if (massageByChatId.isEmpty()) {
                SuportMassageDto suportMassageDto = new SuportMassageDto();
                suportMassageDto.setChatId(chatId);
                suportMassageDto.setMassage(massage);
                suportMassageDto.setDate(new Date());
                supportMassageServiceImpl.save(suportMassageDto);
            } else {
                SuportMassageDto massageDto = massageByChatId.get();
                massageDto.setMassage(massage);
                massageDto.setDate(new Date());
                supportMassageServiceImpl.updateByChatId(massageDto, chatId);
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void register(Long chatId, CallbackQuery callbackQuery) {
        if (isUserExist(chatId)) {
            editMsg(chatId, callbackQuery.getMessage().getMessageId(), "Вы уже зарегистрированы! ✅\n" +
                    "\n" +
                    "Если вам нужна помощь, напишите /help \uD83C\uDD98\n" +
                    "Чтобы увидеть доступные игры, используйте команду /games \uD83C\uDFAE");
            return;
        }
        var queryFrom = callbackQuery.getFrom();
        String nickname = !queryFrom.getUserName().isEmpty() ? queryFrom.getUserName() : queryFrom.getFirstName();

        User user = new User();
        user.setNickname(nickname);
        user.setChatId(chatId);
        user.setRole(Role.USER);
        user.setStatus(UserStatus.DONT_SENT);
        user.setAStatus(AdminStatus.DONT_WRITE);
        user.setTempChatIdForReply(0L);
        userService.save(userMapper.toDto(user));
        editMsg(chatId, callbackQuery.getMessage().getMessageId(),"Вы успешно зарегистрированы! ✅\n" +
                "\n" +
                "Если вам нужна помощь, напишите /help \uD83C\uDD98\n" +
                "Чтобы увидеть доступные игры, используйте команду /games \uD83C\uDFAE");
    }

    public boolean isUserExist(Long chatId) {
        UserDto userByChatId = userService.getUserByChatId(chatId);
        return userByChatId != null;
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
            execute(sendAnimation);
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
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToUser(Long chatId, String massage, List<String> buttonText, int buttonRows) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(massage);
        sendMessage.enableHtml(true);

        InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, buttonRows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToUser(Long chatId, String massage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(massage);
        sendMessage.enableHtml(true);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void editMsg(Long chatId, Integer msgId, String newText){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(msgId);
        editMessageText.setText(newText);

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void editMsgMedia(Long chatId, Integer msgId, String newText, String url, List<String> buttonText, int buttonRows){
        EditMessageMedia editMessageMedia = new EditMessageMedia();
        editMessageMedia.setChatId(chatId);
        editMessageMedia.setMessageId(msgId);

        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(new File(url), "photo.jpg");
        inputMediaPhoto.setCaption(newText);
        inputMediaPhoto.setParseMode("HTML");

        editMessageMedia.setMedia(inputMediaPhoto);

        InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, buttonRows);
        editMessageMedia.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(editMessageMedia);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private InlineKeyboardMarkup createCustomKeyboard(List<String> buttonText, int rows) {
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
                button.setCallbackData(text);
                row.add(button);
                buttonIndex++;
            }
            keyboard.add(row);
        }

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }
}
