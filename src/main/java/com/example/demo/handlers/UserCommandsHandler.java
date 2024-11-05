package com.example.demo.handlers;

import com.example.demo.domain.*;
import com.example.demo.dto.GameDto;
import com.example.demo.dto.SuportMassageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.mapper.GameMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.GameService;
import com.example.demo.service.QuestService;
import com.example.demo.service.SupportMassageService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.domain.Commands.*;
import static com.example.demo.domain.UserStatus.WANT_UPDATE_MSG;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserCommandsHandler {
    
    private final QuestService questService;
    private final GameService gameService;
    private final GameMapper gameMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final SupportMassageService supportMassageService;
    private final UtilCommandsHandler util;

    private void handleIncomingMessage(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();

        if (text.startsWith(START.getCmd())) {
            wellcome(chatId);
        } else if (text.startsWith(HELP.getCmd())) {
            help(chatId);
        } else if (text.equalsIgnoreCase(GAMES.getCmd())) {
            handleGameCommand(chatId);
        } else if (text.startsWith(GAME.getCmd())) {
            Long gameId = Long.valueOf(text.replaceAll("\\D+", ""));
            getGameById(chatId, gameId);
        } else if (text.startsWith(BUY_SUBSCRIBE.getCmd())) {
            subscription(chatId);
        } else if (text.startsWith(PROFILE.getCmd())) {
            getProfile(chatId);
        } else if (text.startsWith(MENU.getCmd())) {
            menuForUser(chatId);
        } else if (text.startsWith(QUEST_BY_ID.getCmd())) {
            Long id = Long.valueOf(text.replaceAll("/quest", ""));
            Optional<Quest> questById = questService.getQuestById(id);
            util.outputQuestWithCustomBtn(chatId, questById.get(), List.of("Отменить квест"));
        } else {
            handleUserMessage(chatId, text);
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
        util.sendMessageToUser(chatId, text, List.of("Зарегистрировать в системе\uD83D\uDC7E"), 1);

    }

    private void help(Long chatId) {
        util.sendMessageToUser(chatId, "Чем вам помочь?", List.of("Написать админу"), 1);
    }

    private void handleGameCommand(Long chatId) {
        GameGenre[] gameGenres = GameGenre.values();
        List<String> buttons = Arrays.stream(gameGenres)
                .map(Enum::toString)
                .collect(Collectors.toList());
        buttons.add("ALL");
        util.sendMessageToUser(chatId, "Выберите жанр", buttons, buttons.size() / 2);
    }

    private void getGameById(Long chatId, Long gameId) {
        StringBuilder stringBuilder = new StringBuilder();
        String tempCreatorId = "пусто";
        Optional<GameDto> gameByGameId = gameService.getGameByGameId(gameId);
        gameByGameId.ifPresent(gameDto -> {
            util.showAllDescription(stringBuilder, gameDto, tempCreatorId);
            if (gameDto.getGif() != null && !gameDto.getGif().isEmpty()) {
                util.sendGifToUser(chatId, gameDto.getGif(), stringBuilder.toString(), List.of("Оставить заяву для: " + gameDto.getName(), "Показать друзей для игры: " + gameDto.getName()), 1);
            } else {
                util.sendPhotoToUser(chatId, gameDto.getPhoto(), stringBuilder.toString(), List.of("Оставить заяву для: " + gameDto.getName(), "Показать друзей для игры: " + gameDto.getName()), 1);
            }

        });
    }

    public void subscription(Long chatId) {
        String msg = "\uD83D\uDCE2 Подписки на нашем боте! \uD83C\uDF89\n" +
                "\n" +
                "✨ Премиум 5zł — доступ к эксклюзивным функциям и контенту, а также приоритетная поддержка. Откройте новые возможности для вашего аккаунта! \uD83D\uDC8E\n" +
                "\n" +
                "\uD83D\uDC51 Администратор 10zł — полный контроль над системой, управление пользователями и настройками. Эта подписка идеальна для тех, кто хочет иметь полный доступ и возможности управления. \uD83D\uDD27\n" +
                "\n" +
                "Выбирайте подписку, которая подходит именно вам, и начните пользоваться всеми преимуществами уже сегодня! \uD83D\uDE80";
        util.sendMessageToUser(chatId, msg, List.of("Купить: Премиум✨", "Купить: Админ\uD83D\uDC51"), 2);
    }

    private void getProfile(Long chatId) {
        UserDto userByChatId = userService.getUserByChatId(chatId);
        Quest quest = userByChatId.getExecutiveQuest();
        Game game = userByChatId.getGame();
        StringBuilder information = new StringBuilder();
        information.append("👤 <b>Профиль пользователя</b>\n\n")
                .append("📛 <b>Имя:</b> ").append(userByChatId.getNickname()).append("\n")
                .append("💼 <b>Подписка:</b> ").append(userByChatId.getRole()).append("\n\n");

        if (game != null) {
            information.append("🎮 <b>Игра, которую вы хотите сыграть с кем-то:</b> \n")
                    .append(game.getName()).append(" [Запросить игру](/game").append(game.getId()).append(")\n\n");
        }

        information.append("📅 <b>Дата регистрации:</b> ").append(userByChatId.getDateOfRegisterAcc()).append("\n")
                .append("⏳ <b>Ваш аккаунт существует:</b> ")
                .append(Period.between(userByChatId.getDateOfRegisterAcc(), LocalDate.now()).getDays()).append(" дней\n\n")
                .append("📊 <b>Статистика профиля:</b> \n")
                .append("    • Ваша подписка предоставляет доступ к специальным функциям, таким как эксклюзивные игры и повышенные привилегии.\n")
                .append("    • Регулярно участвуйте в играх с другими пользователями, чтобы зарабатывать бонусы и достижения.\n")
                .append("    • Не забывайте обновлять свой профиль и следить за активностью в своем аккаунте!\n\n")
                .append("<b>Принятый квест:</b>\n")
                .append(quest.getGame().getName())
                .append("(/quest").append(quest.getId()).append(" )")
                .append("\n\n")
                .append("💬 <b>Свяжитесь с поддержкой</b>, если у вас возникли вопросы: /help");

        util.sendMessageToUser(chatId, information.toString());
    }

    private void menuForUser(Long chatId) {
        List<String> commandsList = Arrays.stream(values()).toList().stream().filter(cmd -> !cmd.isCmdAdmin() && cmd.isNeedToShow()).map(Commands::getCmdName).toList();
        util.sendMessageToUser(chatId, "<b>\uD83C\uDFAE Roblox Бот — Ваш гид в мире Roblox!</b>\n" +
                        "\n" +
                        "\uD83D\uDC4B Привет! Здесь вы можете найти всё, что нужно для успешной игры в Roblox. Выберите нужную команду:",
                commandsList, commandsList.size() / 2);

    }

    public void register(Long chatId, CallbackQuery callbackQuery) {
        if (util.isUserExist(chatId)) {
            util.editMsg(chatId, callbackQuery.getMessage().getMessageId(), "Вы уже зарегистрированы! ✅\n" +
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
        user.setDateOfRegisterAcc(LocalDate.now());
        userService.save(userMapper.toDto(user));
        util.editMsg(chatId, callbackQuery.getMessage().getMessageId(), "Вы успешно зарегистрированы! ✅\n" +
                "\n" +
                "Если вам нужна помощь, напишите /help \uD83C\uDD98\n" +
                "Чтобы увидеть доступные игры, используйте команду /games \uD83C\uDFAE");
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
            util.sendMessageToUser(chatId, "@" + friends.get(0).getNickname());
            System.out.println(friends);
        } else {
            util.sendMessageToUser(chatId, "Нет друзей, играющих в эту игру");
        }
    }

    private void handleUserMessage(Long chatId, String message) {
        try {
            UserDto user = userService.getUserByChatId(chatId);
            if (user.getStatus().equalsIgnoreCase(UserStatus.WAIT_FOR_SENT.name())) {
                if (saveSuppMassageFromUser(chatId, message)) {
                    util.sendMessageToUser(chatId, "Сообщение отправлено");
                    userService.updateStatusByChatId(chatId, UserStatus.WAIT_FOR_REPLY.name());
                } else {
                    util.sendMessageToUser(chatId, "Ваше сообщение не отправлено. Извините за неполадки");
                }
            } else if (user.getStatus().equalsIgnoreCase(WANT_UPDATE_MSG.name())) {
                saveSuppMassageFromUser(chatId, message);
                util.sendMessageToUser(chatId, "Ваше сообщение обновлено");
                userService.updateStatusByChatId(chatId, UserStatus.WAIT_FOR_REPLY.name());
            }
        } catch (Exception e) {
            System.out.println("Человек не ожидает на отправку сообщений");
        }
    }

    private void handlePositiveFeedback(Long chatId) {
        supportMassageService.deleteByChatId(chatId);
        userService.updateStatusByChatId(chatId, "DONT_SENT");
    }

    private void handleNegativeFeedback(Long chatId, CallbackQuery callbackQuery) {
        SuportMassageDto supportMessage = supportMassageService.getMassageByChatId(chatId).orElse(null);
        if (supportMessage != null) {
            String message = "Пользователь с ником @" + callbackQuery.getFrom().getUserName() +
                    " не одобрил помощь\n\n" + supportMessage.getMassage();
            util.sendMessageToUser(1622241974L, message);
        }
    }

    public boolean saveSuppMassageFromUser(Long chatId, String massage) {
        try {
            Optional<SuportMassageDto> massageByChatId = supportMassageService.getMassageByChatId(chatId);
            if (massageByChatId.isEmpty()) {
                SuportMassageDto suportMassageDto = new SuportMassageDto();
                suportMassageDto.setChatId(chatId);
                suportMassageDto.setMassage(massage);
                suportMassageDto.setDate(new Date());
                supportMassageService.save(suportMassageDto);
            } else {
                SuportMassageDto massageDto = massageByChatId.get();
                massageDto.setMassage(massage);
                massageDto.setDate(new Date());
                supportMassageService.updateByChatId(massageDto, chatId);
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private void handleEditSuppMsg(Long chatId) {
        userService.updateStatusByChatId(chatId, "WANT_UPDATE_MSG");
        util.sendMessageToUser(chatId, "Напишите сообщение");
    }

}
