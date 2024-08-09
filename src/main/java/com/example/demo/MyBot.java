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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
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
        } else if (text.startsWith(GAME.getCmd())) {
            handleGameCommand(chatId);
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
                readGames(chatId, null);
                break;
            case "HORROR":
            case "ADVENTURE":
            case "SHOOTER":
            case "TYCOON":
            case "SURVIVAL":
                readGames(chatId, GameGenre.valueOf(data));
                break;
            default:
                if (data.startsWith("User")) {
                    handleUserReplyRequest(chatId, data);
                } else if (data.startsWith("Оставить заяву")) {
                    handleGameApplication(chatId, data);
                } else if (data.startsWith("Показать друзей")) {
                    showFriends(chatId, data);
                } else if (data.startsWith("Оставить")) {
                    sendMassegeToUser(chatId, "Рано или поздно но кто-то ответит на вашу проблему", null, 0);
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
        sendMassegeToUser(chatId, "Выберите жанр", buttons, buttons.size() / 2);
    }

    private void handleEditSuppMsg(Long chatId) {
        userService.updateStatusByChatId(chatId, "WANT_UPDATE_MSG");
        sendMassegeToUser(chatId, "Напишите сообщение", null, 0);
    }

    private void handleUserMessage(Long chatId, String message) {
        try {
            UserDto user = userService.getUserByChatId(chatId);

            if (user.getStatus().equalsIgnoreCase("WAIT_FOR_SENT")) {
                if (saveSuppMassageFromUser(chatId, message)) {
                    sendMassegeToUser(chatId, "Сообщение отправлено", null, 0);
                    userService.updateStatusByChatId(chatId, "WAIT_FOR_REPLY");
                } else {
                    sendMassegeToUser(chatId, "Ваше сообщение не отправлено. Извините за неполадки", null, 0);
                }
            } else if (user.getRole().equalsIgnoreCase("ADMIN") && user.getAStatus().equalsIgnoreCase("WANT_REPLY")) {
                sendMassegeToUser(user.getTempChatIdForReply(), message, List.of("😀", "😡"), 1);
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
            sendMassegeToUser(chatId, "Введите сообщение", null, 0);
        } else {
            SuportMassageDto supportMessage = supportMassageServiceImpl.getMassageByChatId(chatId).orElse(null);
            if (supportMessage != null) {
                sendMassegeToUser(chatId, "У вас уже есть сообщение: " + supportMessage.getMassage() + "\nдата отправки: " + supportMessage.getDate(),
                        List.of("Редактировать сообщение", "Оставить"), 1);
            }
        }
    }

    private void handleUserReplyRequest(Long chatId, String data) {
        String chatIdWaitingUser = data.replaceAll("\\D", "");
        userService.updateAdminStatusByChatId(chatId, AdminStatus.WANT_REPLY, Long.valueOf(chatIdWaitingUser));
        sendMassegeToUser(chatId, "Напишите сообщение (" + chatIdWaitingUser + ")", null, 0);
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
            sendMassegeToUser(1622241974L, message, null, 0);
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
        List<UserDto> friends = userService.getUserByGameId(gameByName.getId());
        if (!friends.isEmpty()) {
            sendMassegeToUser(chatId, "@" + friends.get(0).getNickname(), null, 0);
            System.out.println(friends);
        } else {
            sendMassegeToUser(chatId, "Нет друзей, играющих в эту игру", null, 0);
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
        sendMassegeToUser(chatId, text, List.of("Зарегистрировать в системе\uD83D\uDC7E"), 1);

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
        sendMassegeToUser(chatId, "Чем вам помочь?", List.of("Написать админу"), 1);
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
        sendMassegeToUser(chatId, stringBuilder.toString(), massageDtos.stream().
                map(suppMsg -> suportMassageMapper.toUserChatInfo(suppMsg).toString()).toList(), massageDtos.size());
    }

    public void readGames(Long chatId, GameGenre genre) {
        List<GameDto> gameByGenre;
        if (genre != null) {
            gameByGenre = gameService.getGameByGenre(genre);
        } else {
            gameByGenre = gameService.readAll();
        }
        if (gameByGenre.isEmpty()) {
            sendMassegeToUser(chatId, "\uD83C\uDF1F Извините за неудобства, но игр с таким жанром пока что нет. \uD83C\uDF1F", null, 0);
        }
        StringBuilder stringBuilder = new StringBuilder();
        String tempCreatorGroup = "пусто";
        for (int i = 0; i < gameByGenre.size(); i++) {
            if (gameByGenre.get(i).getCreator() != null) {
                tempCreatorGroup = gameByGenre.get(i).getCreator().getNameOfGroup();
            }
            stringBuilder.append(i + 1)
                    .append(". ")
                    .append("<b>").append("\uD83C\uDF1F Название игры: ")
                    .append(gameByGenre.get(i).getName()).append("</b>")
                    .append("\n")
                    .append("\n")
                    .append("<b>").append("\uD83D\uDCD6 Описание:").append("</b>")
                    .append("\n")
                    .append(gameByGenre.get(i).getDescription())
                    .append("\n")
                    .append("\n")
                    .append("<b>").append("\uD83C\uDFAE Жанр: ").append("</b>")
                    .append(gameByGenre.get(i).getGameGenre())
                    .append("\n")
                    .append("\n")
                    .append("<b>").append("\uD83D\uDCB0 Цена: ").append("</b>")
                    .append(gameByGenre.get(i).getPrice())
                    .append("\n")
                    .append("\n")
                    .append("<b>").append("\uD83D\uDC68\uD83C\uDFFC\u200D\uD83D\uDCBB Aктив: ").append("</b>")
                    .append(gameByGenre.get(i).getActive())
                    .append("\n")
                    .append("\n")
                    .append("<b>").append("\uD83C\uDFE2 Разработчик: ").append("</b>")
                    .append(tempCreatorGroup)
                    .append("\n")
                    .append("\n")
                    .append("<b>").append("\uD83D\uDDD3 Дата создания:").append("</b>")
                    .append(gameByGenre.get(i).getCreateDate());

            sendPhotoToUser(chatId, gameByGenre.get(i).getPhoto(), stringBuilder.toString(), List.of("Оставить заяву для: " + gameByGenre.get(i).getName(), "Показать друзей для игры: " + gameByGenre.get(i).getName()), 1);
            stringBuilder.setLength(0);
        }
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
            sendMassegeToUser(chatId, "Вы уже зарегестририваны", null, 0);
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
        sendMassegeToUser(chatId, "Вы успешно зарегистрированы в нашем боте✅\n" +
                "\n" +
                "Успешного пользования☺\uFE0F", null, 0);
    }

    public boolean isUserExist(Long chatId) {
        UserDto userByChatId = userService.getUserByChatId(chatId);
        return userByChatId != null;
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

    public void sendMassegeToUser(Long chatId, String massage, List<String> buttonText, int buttonRows) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(massage);
        sendMessage.enableHtml(true);
        if (buttonText != null) {
            InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, buttonRows);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        try {
            execute(sendMessage);
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
