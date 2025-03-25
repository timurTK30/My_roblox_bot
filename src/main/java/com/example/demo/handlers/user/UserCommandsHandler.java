package com.example.demo.handlers.user;

import com.example.demo.config.BotSender;
import com.example.demo.domain.*;
import com.example.demo.dto.GameDto;
import com.example.demo.dto.SuportMassageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.handlers.BasicHandlers;
import com.example.demo.handlers.UtilCommandsHandler;
import com.example.demo.mapper.GameMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.*;
import com.example.demo.util.CommandData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.demo.domain.Commands.*;
import static com.example.demo.domain.UserStatus.WANT_UPDATE_MSG;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserCommandsHandler implements BasicHandlers {

    private final QuestService questService;
    private final GameService gameService;
    private final GameMapper gameMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final SupportMassageService supportMassageService;
    private final UtilCommandsHandler util;
    private final BotSender botSender;
    private final WalletService walletService;
    private final UserStatusService userStatusService;
    private final TKBallService tkBallService;
    private final TradeTKBallService tradeTKBallService;

    @Override
    public boolean canHandle(CommandData commandDate) {
//        boolean isUserCommand = Arrays.stream(Commands.values())
//            .filter(command -> !command.isCmdAdmin() && !command.isQuest())
//            .anyMatch(command -> command.name().startsWith(commandDate.getData()));

        User user = userService.getUserByChatId(commandDate.getChatId());

        boolean hasUserStatus = user != null && (
                user.getStatus().name().equalsIgnoreCase(UserStatus.WAIT_FOR_SENT.name()) ||
                        user.getStatus().name().equalsIgnoreCase(UserStatus.WANT_UPDATE_MSG.name())
        );

        //return isUserCommand || hasUserStatus;
        return true;
    }

    @Override
    public void handle(Long chatId, CommandData commandData) {
        String text = commandData.getData();
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
            buySubscription(chatId);
        } else if (text.startsWith(PROFILE.getCmd())) {
            getProfile(chatId);
        } else if (text.startsWith(MENU.getCmd())) {
            menuForUser(chatId);
        } else if (text.startsWith(QUEST_BY_ID.getCmd())) {
            Long id = Long.valueOf(text.replaceAll("/quest", ""));
            Optional<Quest> questById = questService.getQuestById(id);
            util.outputQuestWithCustomBtn(chatId, questById.get(), List.of("Отменить квест"));
        } else if (text.equalsIgnoreCase("/TKgames")
                || text.equalsIgnoreCase("/TKsGames")
                || text.equalsIgnoreCase("/TK'sGames")) {
            util.sendMessageToUser(chatId, "\uD83C\uDFAE Добро пожаловать в мир игр! \uD83C\uDFAE\n" +
                    "\n" +
                    "Привет, искатель приключений! \uD83C\uDF1F\n" +
                    "У нас есть несколько классных игр, в которые ты можешь сыграть прямо сейчас! \uD83D\uDE0E\n" +
                    "\n" +
                    "1\uFE0F⃣ \uD83C\uDFB0 Spin - Почувствуй азарт! Вращай барабаны и проверь свою удачу! \uD83C\uDF40 Кто знает, может сегодня твой день для больших выигрышей! \uD83C\uDFC6\n" +
                    "Для игры используй команду: /spin\n" +
                    "\n" +
                    "2\uFE0F⃣ \uD83E\uDE99 Coin Flip - Подкинь монетку и узнай, что тебе выпадет! Орел или решка? \uD83E\uDD14\n" +
                    "Для игры просто напиши команду: /coinFlip\n" +
                    "\n" +
                    "Не теряй время, попробуй обе игры и выигрывай! \uD83C\uDF89\n" +
                    "\uD83D\uDC49 Подсказка: Напиши команду и погружайся в игру! Удачи!");

        } else if (text.equalsIgnoreCase("/spin")) {
            util.sendWebAppReplyKeyboard(chatId,
                    "Рулетка",
                    "https://osozznanie.github.io/wheel.github.io/",
                    "Крутить!");
        } else if (text.equalsIgnoreCase("/coinFlip")) {
            util.sendWebAppInlineKeyboard(chatId,
                    "Нажимите, чтобы подкинуть монетку",
                    "https://127.0.0.1:8080",
                    "Подкинуть");
        } else if (text.equalsIgnoreCase("/plinko")) {
            util.sendWebAppInlineKeyboard(chatId,
                    "Нажимите, чтобы сыграть в плинко",
                    "https://127.0.0.1:8075",
                    "Играть");
        } else if (text.startsWith("/reg")) {
            util.adminRegister(chatId, commandData.getUserName());
        } else if (text.startsWith("/tradeBalls")) {
            util.sendMessageToUser(chatId, "\uD83C\uDFB2 /tradeBall – крути обмен на полную! \uD83C\uDFB2  \n" +
                            "\n" +
                            "\uD83D\uDCB0 Хотишь больше tkBall? Покупай!  \n" +
                            "\uD83D\uDCB8 Нужна валюта? Продавай!  \n" +
                            "\n" +
                            "\uD83D\uDD04 Быстро, честно, без лишних заморочек!  \n" +
                            "⚡\uFE0F Твои tkBall – твои правила! \uD83D\uDE80", List.of("Купить 10", "Продать 10"),
                    List.of("buyTkBall10", "sellTkBall10"), 1);

        } else {
            handleUserMessage(chatId, text);
        }
    }

    public void wellcome(Long chatId) {
        String text = "Привет! \uD83D\uDE0A <b>Я бот по игре Roblox.</b> \n" +
                "Я могу показать тебе самые интересные игры в этом мире. \n" +
                "\n" +
                "От захватывающих приключений до захватывающих соревнований - <i>я знаю всё!</i> Просто напиши мне свои предпочтения, и я подберу для тебя что-то увлекательное! \uD83C\uDFAE✨\n"
                +
                "\n" +
                "А ещё я всегда обновляю свою базу данных, чтобы ты всегда был в курсе последних трендов и новых релизов. \n"
                +
                "\n" +
                "Так что не стесняйся, спрашивай обо всём, что тебе интересно!";
        util.sendMessageToUser(chatId, text, List.of("Зарегистрировать в системе\uD83D\uDC7E"),
                List.of("Зарегистрировать"), 1);

    }

    public void help(Long chatId) {
        util.sendMessageToUser(chatId, "Чем вам помочь?", List.of("Написать админу"), 1);
    }

    public void handleGameCommand(Long chatId) {
        GameGenre[] gameGenres = GameGenre.values();
        List<String> buttons = Arrays.stream(gameGenres)
                .map(Enum::toString)
                .collect(Collectors.toList());
        buttons.add("ALL");
        util.sendMessageToUser(chatId, "Выберите жанр", buttons, buttons.size() / 2);
    }

    public void buySubscription(Long chatId) {
        String msg = "\uD83D\uDCE2 Подписки на нашем боте! \uD83C\uDF89\n" +
                "\n" +
                "✨ Премиум 5zł — доступ к эксклюзивным функциям и контенту, а также приоритетная поддержка. Откройте новые возможности для вашего аккаунта! \uD83D\uDC8E\n"
                +
                "\n" +
                "\uD83D\uDC51 Администратор 10zł — полный контроль над системой, управление пользователями и настройками. Эта подписка идеальна для тех, кто хочет иметь полный доступ и возможности управления. \uD83D\uDD27\n"
                +
                "\n" +
                "Выбирайте подписку, которая подходит именно вам, и начните пользоваться всеми преимуществами уже сегодня! \uD83D\uDE80";
        util.sendMessageToUser(chatId, msg, List.of("Купить: Премиум✨", "Купить: Админ\uD83D\uDC51"),
                List.of("request_buy_premium", "request_buy_admin"), 2);
    }

    public void allQuests(Long chatId) {
        List<Quest> questList = questService.readAll().stream()
                .filter(q -> !q.isDeprecated() && util.checkListForNulls(q))
                .toList();
        if (questList.isEmpty()) {
            util.sendMessageToUser(chatId, "Здесь пока нет квестов");
            return;
        }
        questList.forEach(existQuest -> {
            util.outputQuestWithCustomBtn(chatId, existQuest, List.of("Принять квест", "Отменить квест"),
                    List.of("Принять квест " + existQuest.getId(), "Отменить квест " + existQuest.getId()));
        });
    }

    public void buyTkBalls(Long chatId, String callBack, Integer msgId) {
        Long amountOfBalls = Long.valueOf(callBack.replaceAll("\\D", ""));
        TKBall tkBall = tradeTKBallService.buyTKBall(chatId, amountOfBalls);
        Double balance = walletService.getWalletByUser(userService.getUserByChatId(chatId)).get().getBalance();
        if (tkBall != null) {
            util.editMsg(chatId, msgId,"✅ Покупка успешна!\n" +
                    "Вы приобрели \uD83C\uDFB1 tkBalls.\n" +
                    "\n" +
                    "\uD83D\uDCB0 Баланс: " + balance + "\n" +
                    "\uD83D\uDD35 Количество шариков: " + tkBall.getAmountOfBalls() + "\n" +
                    "\n" +
                    "Если нужно, могу подправить или добавить что-то еще! \uD83D\uDE0A");
        } else {
            Optional<TKBall> tkBallByChatId = tkBallService.getTKBallByChatId(chatId);
            util.editMsg(chatId, msgId,"❌ Ошибка при покупке!\n" +
                    "Недостаточно средств для приобретения \uD83C\uDFB1 tkBalls.\n" +
                    "\n" +
                    "\uD83D\uDCB0 Ваш баланс: " + balance + "\n" +
                    "\uD83D\uDD35 Ваши tkBalls: " + tkBallByChatId.get().getAmountOfBalls() + "\n" +
                    "\n" +
                    "Попробуйте пополнить баланс и повторите попытку. \uD83D\uDE0A");
            throw new RuntimeException("Проблема с покупкой тк балл. callBack = " + callBack);
        }
    }

    public void sellTKBalls(Long chatId, String callBack, Integer msgId) {
        Long amountOfBalls = Long.valueOf(callBack.replaceAll("\\D", ""));
        System.out.println(amountOfBalls);
        TKBall tkBall = tradeTKBallService.sellTKBall(chatId, amountOfBalls);
        Double balance = walletService.getWalletByUser(userService.getUserByChatId(chatId)).get().getBalance();
        if (tkBall != null) {
            util.editMsg(chatId, msgId, "✅ Продажа завершена!\n" +
                    "Вы продали \uD83C\uDFB1 tkBalls.\n" +
                    "\n" +
                    "\uD83D\uDCB0 Баланс: " + balance + "\n" +
                    "\uD83D\uDD35 Оставшееся количество шариков: " + tkBall.getAmountOfBalls() + "\n" +
                    "\n" +
                    "Если нужно что-то изменить, говори! \uD83D\uDE0A");
        } else {
            Optional<TKBall> tkBallByChatId = tkBallService.getTKBallByChatId(chatId);
            util.editMsg(chatId, msgId, "❌ Ошибка при продаже!\n" +
                    "Возможно, у вас недостаточно \uD83C\uDFB1 tkBalls для продажи.\n" +
                    "\n" +
                    "\uD83D\uDCB0 Ваш баланс: " + balance + "\n" +
                    "\uD83D\uDD35 Ваши tkBalls: " + tkBallByChatId.get().getAmountOfBalls() + "\n" +
                    "\n" +
                    "Попробуйте еще раз или проверьте количество доступных tkBalls. \uD83D\uDE0A");
            throw new RuntimeException("Проблема с продажей тк балл. callBack = " + callBack);
        }
    }

    public void findForGames(Long chatId) {

        List<Quest> questList = questService.readAll().stream()
                .filter(util::checkListForNulls)
                .filter(q -> !q.isDeprecated())
                .toList();
        List<Game> gameList = questList.stream()
                .map(Quest::getGame)
                .toList();
        List<String> uniqueGameNames = gameList.stream()
                .map(Game::getName)
                .distinct()
                .toList();
        List<String> callBack = questList.stream()
                .map(tempQuest -> String.join("_", tempQuest.getGame().getName(), "quest", tempQuest.getId().toString()))
                .toList();

        if (uniqueGameNames.isEmpty()) {
            util.sendMessageToUser(chatId, "Здесь пока нет квестов");
            return;
        }
        util.sendMessageToUser(chatId, "Вибирите игру:", uniqueGameNames, callBack, uniqueGameNames.size() / 2);
    }

    public void cancelQuest(Long chatId) {
        User userForDeleteQuest = userService.getUserByChatId(chatId);
        userForDeleteQuest.setExecutiveQuest(null);
        userService.updateByChatId(userMapper.toDto(userForDeleteQuest), chatId);
        util.sendMessageToUser(chatId, "Квест был отменен");
    }


    public void getProfile(Long chatId) {
        User userByChatId = userService.getUserByChatId(chatId);
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
                .append(
                        "    • Ваша подписка предоставляет доступ к специальным функциям, таким как эксклюзивные игры и повышенные привилегии.\n")
                .append(
                        "    • Регулярно участвуйте в играх с другими пользователями, чтобы зарабатывать бонусы и достижения.\n")
                .append("    • Не забывайте обновлять свой профиль и следить за активностью в своем аккаунте!\n\n")
                .append("<b>Принятый квест:</b>\n")
                .append(quest.getGame().getName())
                .append("(/quest").append(quest.getId()).append(" )")
                .append("\n\n")
                .append("💬 <b>Свяжитесь с поддержкой</b>, если у вас возникли вопросы: /help");

        util.sendMessageToUser(chatId, information.toString());
    }

    private void getGameById(Long chatId, Long gameId) {
        StringBuilder stringBuilder = new StringBuilder();
        Optional<GameDto> gameByGameId = gameService.getGameByGameId(gameId);
        gameByGameId.ifPresent(gameDto -> {
            util.showAllDescription(stringBuilder, gameDto);
            boolean hasGame = util.isUserHasSpecificGame(chatId, gameId);

            List<String> buttons = hasGame
                    ? List.of("Удалить заявку", "Показать друзей")
                    : List.of("Оставить заявку", "Показать друзей");

            List<String> callbackData = hasGame
                    ? List.of("remove_gameRequest_" + gameDto.getName(), "show_friends_" + gameDto.getName())
                    : List.of("leave_request_" + gameDto.getName(), "show_friends_" + gameDto.getName());

            util.sendTypingStatus(chatId);
            if (!ObjectUtils.isEmpty(gameDto.getGif())) {
                util.sendGifToUser(chatId, gameDto.getGif(), stringBuilder.toString(),
                        buttons,
                        callbackData, 1);
            } else {
                util.sendPhotoToUser(chatId, gameDto.getPhoto(), stringBuilder.toString(),
                        buttons,
                        callbackData, 1);
            }

        });
    }

    public void readGames(Long chatId, String genre, Integer msgId) {
        util.deleteMsg(chatId, msgId);

        List<GameDto> gameByGenre = genre.equalsIgnoreCase("ALL")
                ? gameService.readAll()
                : gameService.getGameByGenre(GameGenre.valueOf(genre));

        if (gameByGenre.isEmpty()) {
            util.sendMessageToUser(chatId,
                    "\uD83C\uDF1F Извините за неудобства, но игр с таким жанром пока что нет. \uD83C\uDF1F");
            return;
        }

        //if(userService.getUserByChatId(chatId).getGame() != null){
        // Получаем ID игры, которые уже есть у пользователя
        //Long userGameId = userService.getUserByChatId(chatId).getGame().getId();
        //}

//        Optional<Long> optionalUserGameId = Optional.ofNullable(userService.getUserByChatId(chatId).getGame())
//                .map(game -> game.getId());

        Game game = userService.getUserByChatId(chatId).getGame();
        Long userGameId = (game != null) ? game.getId() : null;

        String tempCreatorGroup = "пусто";

        for (int i = 0; i < gameByGenre.size(); i++) {
            GameDto gameDto = gameByGenre.get(i);

            // Проверка, есть ли у пользователя эта игра
            boolean hasGame = (userGameId != null) && userGameId.equals(gameDto.getId());

            StringBuilder stringBuilder = new StringBuilder();
            util.showShortDescription(stringBuilder, i, gameDto, tempCreatorGroup);

            // Динамическая генерация кнопок
            List<String> buttons = hasGame
                    ? List.of("Удалить заявку", "Показать друзей")
                    : List.of("Оставить заявку", "Показать друзей");

            List<String> callbackData = hasGame
                    ? List.of("remove_gameRequest_" + gameDto.getName(), "show_friends_" + gameDto.getName())
                    : List.of("leave_request_" + gameDto.getName(), "show_friends_" + gameDto.getName());

            util.sendPhotoToUser(
                    chatId,
                    gameDto.getPhoto(),
                    stringBuilder.toString(),
                    buttons,
                    callbackData,
                    1
            );
        }
    }

    public void removeGameRequest(Long chatId, String callbackId) {
        userService.deleteGameRequestFromUser(chatId);
        util.showAlert(callbackId, "Операция прошла успешно");
    }

    public void handleSupportMessage(Long chatId, Integer msgId) {

        if (!util.isSuppMsgExistByUserChatId(chatId)) {
            //userService.updateStatusByChatId(chatId, "WAIT_FOR_SENT");
            userStatusService.setUserStatus(chatId, UserStatus.WAIT_FOR_SENT);
            util.editMsg(chatId, msgId, "Введите сообщение");
        } else {
            SuportMassageDto supportMessage = supportMassageService.getMassageByChatId(chatId).orElse(null);
            if (supportMessage != null) {
                util.deleteMsg(chatId, msgId);
                util.sendMessageToUser(chatId,
                        "У вас уже есть сообщение: " + supportMessage.getMassage() + "\nдата отправки: "
                                + supportMessage.getDate(),
                        List.of("Редактировать сообщение", "Оставить"), List.of("edit_msg", "leave_msg"), 1);
            }
        }
    }

    private void menuForUser(Long chatId) {
        List<String> commandsList = Arrays.stream(values()).toList().stream()
                .filter(cmd -> !cmd.isCmdAdmin() && cmd.isNeedToShow()).map(Commands::getCmdName).toList();
        List<String> callback = util.removeSignAndEnglishLetter(commandsList);
        util.sendMessageToUser(chatId, "<b>\uD83C\uDFAE Roblox Бот — Ваш гид в мире Roblox!</b>\n" +
                        "\n" +
                        "\uD83D\uDC4B Привет! Здесь вы можете найти всё, что нужно для успешной игры в Roblox. Выберите нужную команду:",
                commandsList, callback, commandsList.size() / 2);

    }

    @Transactional
    public void register(Long chatId, Integer msgId, String userName) {
        if (util.isUserExist(chatId)) {
            util.editMsg(chatId, msgId, "Вы уже зарегистрированы! ✅\n" +
                    "\n" +
                    "Если вам нужна помощь, напишите /help \uD83C\uDD98\n" +
                    "Чтобы увидеть доступные игры, используйте команду /games \uD83C\uDFAE");
            return;
        }

        User user = new User();
        try {
            user.setNickname(userName);
            user.setChatId(chatId);
            user.setRole(Role.USER);
            user.setStatus(UserStatus.DONT_SENT);
            user.setDateOfRegisterAcc(LocalDate.now());
            tkBallService.save(new TKBall(user, 10L));
            walletService.save(new Wallet(user));
            util.editMsg(chatId, msgId, "Вы успешно зарегистрированы! ✅\n" +
                    "\n" +
                    "Если вам нужна помощь, напишите /help \uD83C\uDD98\n" +
                    "Чтобы увидеть доступные игры, используйте команду /s \uD83C\uDFAE");
        } catch (Exception e) {
            log.error(e.getMessage());
            util.sendMessageToUser(chatId, "⚠\uFE0F Упс! Произошла ошибка во время регистрации. \n" +
                    "Пожалуйста, попробуйте снова позже. Мы работаем над её устранением! \uD83D\uDE4F");
        }

    }

    public void handleGameApplication(Long chatId, String data, String callBackId) {
        User user = userService.getUserByChatId(chatId);
        Game game = user.getGame();
        if (game != null) {
            util.sendMessageToUser(chatId, "Заявка у вас уже есть, отмените сначала (/game" + game.getId() + ")");
            return;
        }

        String gameName = data.replaceAll("leave_request_", "").trim();
        GameDto gameDto = gameService.getGameByName(gameName);

        user.setGame(gameMapper.toEntity(gameDto));
        userService.updateByChatId(userMapper.toDto(user), chatId);
        util.showAlert(callBackId, "Заявка отправлена");
    }

    public void showFriends(Long chatId, String data) {
        String gameName = data.replaceAll("show_friends_", "").trim();
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
        //User user = new User();
        UserStatusData userStatusData = new UserStatusData();
        try {
//            user = userService.getUserByChatId(chatId);
            userStatusData = userStatusService.getUserStatus(chatId);
            if (userStatusData.getUserStatus().name().equalsIgnoreCase(UserStatus.WAIT_FOR_SENT.name())) {
                if (saveSuppMassageFromUser(chatId, message)) {
                    util.sendMessageToUser(chatId, "Сообщение отправлено");
                    userStatusService.setUserStatus(chatId, UserStatus.WAIT_FOR_REPLY);
                    //userService.updateStatusByChatId(chatId, UserStatus.WAIT_FOR_REPLY.name());
                } else {
                    util.sendMessageToUser(chatId, "Ваше сообщение не отправлено. Извините за неполадки");
                }
            } else if (userStatusData.getUserStatus().name().equalsIgnoreCase(WANT_UPDATE_MSG.name())) {
                saveSuppMassageFromUser(chatId, message);
                util.sendMessageToUser(chatId, "Ваше сообщение обновлено");
                userStatusService.setUserStatus(chatId, UserStatus.WAIT_FOR_REPLY);
                //userService.updateStatusByChatId(chatId, UserStatus.WAIT_FOR_REPLY.name());
            }
        } catch (Exception e) {
            System.out.println("Человек не ожидает на отправку сообщений " + userStatusData);
        }
    }

    public void handlePositiveFeedback(Long chatId) {
        supportMassageService.deleteByChatId(chatId);
        userStatusService.setUserStatus(chatId, UserStatus.DONT_SENT);
        //userService.updateStatusByChatId(chatId, "DONT_SENT");
    }

    public void handleNegativeFeedback(Long chatId) {
        SuportMassageDto supportMessage = supportMassageService.getMassageByChatId(chatId).orElse(null);
        User userByChatId = userService.getUserByChatId(chatId);
        if (supportMessage != null) {
            String message = "Пользователь с ником @" + userByChatId.getNickname() +
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

    public void handleEditSuppMsg(Long chatId, Integer msgId) {
        userStatusService.setUserStatus(chatId, WANT_UPDATE_MSG);
        //userService.updateStatusByChatId(chatId, "WANT_UPDATE_MSG");
        util.editMsg(chatId, msgId, "Напишите сообщение");
    }

}
