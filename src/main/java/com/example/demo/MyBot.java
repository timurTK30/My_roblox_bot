package com.example.demo;

import com.example.demo.config.BotConfig;
import com.example.demo.domain.User;
import com.example.demo.dto.SuportMassageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.CreatorService;
import com.example.demo.service.GameService;
import com.example.demo.service.SupportMassageService;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
@Slf4j
public class MyBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CreatorService creatorService;
    private final GameService gameService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final SupportMassageService supportMassageService;
    private final Map<Long, Boolean> ifUserWantToSendSupMassage = new HashMap<>();

    @Autowired
    public MyBot(BotConfig botConfig, CreatorService creatorService, GameService gameService, UserService userService, UserMapper userMapper, SupportMassageService supportMassageService) {
        this.botConfig = botConfig;
        this.creatorService = creatorService;
        this.gameService = gameService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.supportMassageService = supportMassageService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String massege = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (massege.startsWith("/start")) {
                wellcome(chatId);
            } else if (massege.startsWith("/help")) {
                help(chatId);
            } else if (massege.startsWith("/readSuppMsg")) {
                readSuppMsg(chatId);

            } else if (!massege.isEmpty() && ifUserWantToSendSupMassage.get(chatId)) {
                saveSuppMassageFromUser(chatId, massege);
            }
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Long chatId = callbackQuery.getMessage().getChatId();
            if (callbackQuery.getData().equalsIgnoreCase("Зарегистрировать в системе\uD83D\uDC7E")) {
                register(chatId, callbackQuery.getFrom().getUserName());
            }
            if (callbackQuery.getData().equalsIgnoreCase("Написать админу")) {
                ifUserWantToSendSupMassage.put(chatId, true);
                sendMassegeToUser(chatId, "Введите сообщение", null, 0);
            }
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

    public void help(Long chatId) {
        sendMassegeToUser(chatId, "Чем вам помочь?", List.of("Написать админу"), 1);
    }

    public void readSuppMsg(Long chatId) {
        List<SuportMassageDto> massageDtos = supportMassageService.readAll();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < massageDtos.size(); i++) {
            stringBuilder.append(i + 1)
                    .append(massageDtos.get(i).getChatId())
                    .append(massageDtos.get(i).getMassage());
        }
        sendMassegeToUser(chatId, stringBuilder.toString(), null, 0);
    }

    public void saveSuppMassageFromUser(Long chatId, String massage) {
        SuportMassageDto suportMassageDto = new SuportMassageDto();
        suportMassageDto.setChatId(chatId);
        suportMassageDto.setMassage(massage);
        suportMassageDto.setDate(new Date());
        supportMassageService.save(suportMassageDto);
    }

    public void register(Long chatId, String nickname) {
        if (isUserExist(chatId)) {
            sendMassegeToUser(chatId, "Вы уже зарегестририваны", null, 0);
            return;
        }
        User user = new User();
        user.setNickname(nickname);
        user.setChatId(chatId);
        userService.save(userMapper.toDto(user));
        sendMassegeToUser(chatId, "Вы успешно зарегистрированы в нашем боте✅\n" +
                "\n" +
                "Успешного пользования☺\uFE0F", null, 0);
    }

    public boolean isUserExist(Long chatId) {
        UserDto userByChatId = userService.getUserByChatId(chatId);
        return userByChatId != null;
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
        int buttonsPerRow = buttonText.size() / rows + buttonText.size() % rows;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String s : buttonText) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(s);
            button.setCallbackData(s);

            row.add(button);
            if (row.size() == buttonsPerRow || button.equals(buttonText.get(buttonText.size() - 1))) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
        }

        if (buttonText.size() % rows == 1) {
            List<InlineKeyboardButton> buttonRow = new ArrayList<>();
            for (int i = 0; i < buttonText.size() % rows; i++) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(buttonText.get(buttonText.size() - buttonText.size() % rows + i));
                inlineKeyboardButton.setCallbackData(buttonText.get(buttonText.size() - buttonText.size() % rows + i));
                buttonRow.add(inlineKeyboardButton);
            }

            keyboard.add(buttonRow);
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
