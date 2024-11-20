package com.example.demo;

import com.example.demo.config.BotConfig;
import com.example.demo.domain.*;
import com.example.demo.dto.GameDto;
import com.example.demo.dto.SuportMassageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.handlers.UserCommandsHandler;
import com.example.demo.handlers.service.CallbackService;
import com.example.demo.handlers.service.CommandService;
import com.example.demo.mapper.GameMapper;
import com.example.demo.mapper.SuportMassageMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.CreatorService;
import com.example.demo.service.GameService;
import com.example.demo.service.QuestService;
import com.example.demo.service.UserService;
import com.example.demo.service.serviceImp.SupportMassageServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.domain.Commands.*;
import static com.example.demo.domain.QuestCommands.EDIT_QUEST;
import static com.example.demo.domain.UserStatus.WANT_UPDATE_MSG;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CreatorService creatorService;
    private final GameService gameService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final SupportMassageServiceImpl supportMassageServiceImpl;
    private final SuportMassageMapper suportMassageMapper;
    private final GameMapper gameMapper;
    private final QuestService questService;
    private final UserCommandsHandler userCH;
    private final CallbackService callbackService;
    private final CommandService commandService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            menuForUser(update.getMessage().getChatId());
            commandService.handleCommand(update.getMessage());
        }

        if (update.hasCallbackQuery()) {
            callbackService.handleCallback(update.getCallbackQuery());
        }
    }

    private void handleIncomingMessage(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();

        if (text.startsWith(START.getCmd())) {
            userCH.wellcome(chatId);
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
            getMenuByRole(chatId);
        } else if (text.startsWith(QUEST_BY_ID.getCmd())) {
            Long id = Long.valueOf(text.replaceAll("/quest", ""));
            Optional<Quest> questById = questService.getQuestById(id);
            outputQuestWithCustomBtn(chatId, questById.get(), List.of("Отменить квест"));
        } else if (isUserAdmin(chatId)) {
            if (text.startsWith(STATISTISC.getCmd())) {
                statistics(chatId);
            } else if (text.startsWith(RESTART.getCmd())) {
                restart(chatId);
            } else if (text.startsWith(SET_ROLE.getCmd())) {
                requestToChangeRole(text, chatId);
            } else if (text.startsWith(READ_SUPP_MSG.getCmd())) {
                readSuppMsg(chatId);
            } else {
                handleAdminMessage(chatId, text);
            }
        } else {
            handleUserMessage(chatId, text);
        }

    }

    private void getMenuByRole(Long chatId) {
        if (isUserAdmin(chatId)) {
            menuForAdmin(chatId);
        } else {
            menuForUser(chatId);
        }
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

        sendMessageToUser(chatId, information.toString());
    }

    private void requestToChangeRole(String text, Long chatId) {
        Long chatIdUserForChange = Long.valueOf(text.replaceAll("\\D+", ""));
        sendMessageToUser(chatId, "Хотите поменять роль?",
                List.of(Role.ADMIN.name(), Role.PREMIUM_USER.name(), Role.USER.name()),
                List.of("change_role_admin_" + chatIdUserForChange,
                        "change_role_premium_" + chatIdUserForChange,
                        "change_role_user_" + chatIdUserForChange), 2);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        System.out.println(data);


        switch (data) {
            case "Зарегистрировать в системе\uD83D\uDC7E":
                register(chatId, callbackQuery);
                getMenuByRole(chatId);
                break;
            case "Написать админу":
                handleAdminMessage(chatId, callbackQuery.getMessage().getMessageId());
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
            case "\uD83D\uDC81Помошь":
                help(chatId);
                break;
            case "🎮Игры":
                handleGameCommand(chatId);
                break;
            case "📨Купить подписки":
                subscription(chatId);
                break;
            case "ℹ️Профиль":
                getProfile(chatId);
                break;
            case "Прочитать сообщение от юзера":
                readSuppMsg(chatId);
                break;
            case "Перезагрузить бота":
                restart(chatId);
                break;
            case "\uD83D\uDCCA Статистика использования бота":
                statistics(chatId);
                break;
            case "✉\uFE0F Отправить сообщение":
                sendMessageToUser(chatId, "Введите сообщение: ");
                userService.updateAdminStatusByChatId(chatId, AdminStatus.NOTIFY_ALL_USERS, 0L);
                break;
            case "Доступные квесты":
                readAllQuestsForAdmin(chatId);
                break;
            case "Квест меню":
                menuForCreateQuest(chatId);
                break;
            case "Создать квест":
                //TODO переместить в метод
                Quest quest = new Quest();
                UserDto userByChatId = userService.getUserByChatId(chatId);
                quest.setCreatorOfQuest(userMapper.toEntity(userByChatId));
                quest.setDeprecated(false);
                questService.save(quest);

                Quest lastQuest = getLastQuest();
                outputQuestForAdmin(chatId, lastQuest);
                break;
            case "Удалить старие квесты":
                deleteDeprecatedQuest(chatId);
                break;
            case "Прочитать доступные игры":
                List<GameDto> gameDtos = gameService.readAll();
                StringBuilder stringBuilder = new StringBuilder();
                gameDtos.forEach(gameDto -> {
                    stringBuilder.append(gameDto.getName())
                            .append(" ( /game").append(gameDto.getId()).append(" )")
                            .append("\n");
                });
                sendMessageToUser(chatId, stringBuilder.toString());
                break;
            case "Квесты":
                sendMessageToUser(chatId, "Какая будет категория?", List.of("Все квесты", "Поиск по играх"), 2);
                break;
            case "Все квесты": {
                List<Quest> questList = questService.readAll().stream()
                        .filter(q -> !q.isDeprecated() && checkListForNulls(q))
                        .toList();
                if (questList.isEmpty()) {
                    sendMessageToUser(chatId, "Здесь пока нет квестов");
                    break;
                }
                questList.forEach(existQuest -> {
                    outputQuestWithCustomBtn(chatId, existQuest, List.of("Принять квест", "Отменить квест"), List.of("Принять квест " + existQuest.getId(), "Отменить квест " + existQuest.getId()));
                });

                break;
            }
            case "Поиск по играх":
                List<Quest> questList = questService.readAll().stream()
                        .filter(this::checkListForNulls)
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
                    sendMessageToUser(chatId, "Здесь пока нет квестов");
                    break;
                }
                sendMessageToUser(chatId, "Вибирите игру:", uniqueGameNames, callBack, uniqueGameNames.size() / 2);
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
                } else if (data.startsWith("Купить")) {
                    requestToBuySub(callbackQuery, data, chatId);
                } else if (data.startsWith(Role.ADMIN.name()) ||
                        data.startsWith(Role.USER.name()) ||
                        data.startsWith(Role.PREMIUM_USER.name())) {
                    updateRole(data, chatId);
                } else if (data.startsWith(QuestCommands.ADD_DECRIPCION_FOR_QUEST.getCmdName())) {
                    sendMessageToUser(chatId, "Введите описание: ");
                    userService.updateAdminStatusByChatId(chatId, AdminStatus.CHANGE_DESCRIPTION_QUEST, 0L);
                } else if (data.startsWith(QuestCommands.ADD_REWARD_FOR_QUEST.getCmdName())) {
                    sendMessageToUser(chatId, "Ввидите награду: ");
                    userService.updateAdminStatusByChatId(chatId, AdminStatus.CHANGE_REWARD_QUEST, 0L);
                } else if (data.startsWith(QuestCommands.ADD_GAME_FOR_QUEST.getCmdName())) {
                    sendMessageToUser(chatId, "Введите название игры: ", List.of("Прочитать доступные игры"), 1);
                    userService.updateAdminStatusByChatId(chatId, AdminStatus.CHANGE_GAME_QUEST, 0L);
                } else if (data.contains("Изменить на")) {
                    //TODO сделать, чтобы старое сообщение менялось на новое , editMSG
                    Quest existQuest = getQuestByIdFromCallback(chatId, data);
                    existQuest.setDeprecated(data.endsWith("❌"));

                    questService.updateById(existQuest.getId(), existQuest);
                } else if (data.contains(EDIT_QUEST.getCmdName())) {
                    Quest existQuest = getQuestByIdFromCallback(chatId, data);
                    outputQuestForAdmin(chatId, existQuest);

                } else if (data.startsWith("Принять квест")) {
                    Long questId = Long.valueOf(data.replaceAll("Принять квест", "").trim());
                    Quest executiveQuest = userService.getUserByChatId(chatId).getExecutiveQuest();

                    String msg = "Квест принят";
                    if (executiveQuest != null) {
                        msg = "Ваш прошлый квест (/quest" + executiveQuest.getId() + ") был заменен";
                    }
                    Quest questById = questService.getQuestById(questId).get();
                    UserDto userForUpdate = userService.getUserByChatId(chatId);
                    userForUpdate.setExecutiveQuest(questById);
                    userService.updateByChatId(userForUpdate, chatId);
                    sendMessageToUser(chatId, msg);

                } else if (data.contains("_quest_")) {
                    String[] splitData = data.split("_");
                    Long questId = Long.valueOf(splitData[splitData.length - 1]);
                    Quest questById = questService.getQuestById(questId).get();
                    outputQuestWithCustomBtn(chatId, questById, List.of("Принять квест", "Отменить квест"), List.of("Принять квест " + questId, "Отменить квест " + questId));
                } else if (data.startsWith("Отменить квест")) {
                    UserDto userForDeleteQuest = userService.getUserByChatId(chatId);
                    userForDeleteQuest.setExecutiveQuest(null);
                    userService.updateByChatId(userForDeleteQuest, chatId);
                    sendMessageToUser(chatId, "Квест был отменен");

                }
                break;
        }
    }

    private void readAllQuestsForAdmin(Long chatId) {
        List<Quest> questList = questService.readAll();

        if (questList.isEmpty()) {
            sendMessageToUser(chatId, "Квестов нет");
            return;
        }
        questList.forEach(quest -> {
            String btn1 = quest.isDeprecated() ? quest.getId() + " Изменить на ✅" : quest.getId() + " Изменить на ❌";
            String btn2 = quest.getId() + " " + EDIT_QUEST.getCmdName();
            outputQuestWithCustomBtn(chatId, quest, List.of(btn1, btn2));
        });

    }

    private boolean checkListForNulls(Quest quest) {
        return ObjectUtils.allNotNull(
                quest.getId(), quest.getReward(), quest.getGame()
        );
    }

    private Quest getQuestByIdFromCallback(Long chatId, String data) {
        Long id = Long.valueOf(data.substring(0, data.indexOf(" ")));
        Optional<Quest> questById = questService.getQuestById(id);

        if (questById.isEmpty()) {
            sendMessageToUser(chatId, "Такого квеста нет");
            throw new NullPointerException("name method -> getQuestByIdFromCallback <- name method return null");
        }

        return questById.get();
    }

    private void deleteDeprecatedQuest(Long chatId) {
        List<Quest> quests = questService.readAll();
        for (Quest q : quests) {
            if (q.isDeprecated()) {
                questService.deleteById(q.getId());
                sendMessageToUser(chatId, "Квест с id " + q.getId() + " бил удален");
            }
        }
    }

    private Quest getLastQuest() {
        List<Quest> questList = questService.readAll();
        Quest lastQuest = questList.get(questList.size() - 1);
        return lastQuest;
    }

    private void statistics(Long chatId) {
        List<UserDto> userDtos = userService.readAll();
        List<SuportMassageDto> massageDtos = supportMassageServiceImpl.readAll();
        Commands[] commands = values();
        long amountOfSuppMsg = massageDtos.size();
        long amountOfUsers = userDtos.stream().filter(user -> !user.getRole().equalsIgnoreCase(Role.ADMIN.name())).count();
        long amountOfAdmins = userDtos.stream().filter(user -> user.getRole().equalsIgnoreCase(Role.ADMIN.name())).count();
        long amountOfCommands = commands.length;

        sendMessageToUser(chatId, "Привет, Админ! Вот последние данные о активности вашего бота:\n" +
                "\n" +
                "1. <b>Всего пользователей: </b> " + amountOfUsers + " \uD83D\uDCC8\n" +
                "2. <b>Всего администраторов: </b> " + amountOfAdmins + "\uD83D\uDC69\u200D\uD83D\uDCBC\uD83D\uDC68\u200D\uD83D\uDCBC\n" +
                "3. <b>Отправлено сообщений в поддержку: </b> " + amountOfSuppMsg + " \uD83D\uDCAC\n" +
                "4. <b>Всего команд: </b> " + amountOfCommands + "\uD83D\uDEE0");
    }

    private void restart(Long chatId) {
        sendPhotoToUser(chatId, "C:\\project_java\\My_roblox_bot_new\\src\\main\\resources\\img\\fatalError.jpg", "Программа остоновлена", List.of("Bye bye"), 1);
        System.exit(0);
    }

    private void menuForUser(Long chatId) {
        List<String> commandsList = Arrays.stream(values()).toList().stream().filter(cmd -> !cmd.isCmdAdmin() && cmd.isNeedToShow()).map(Commands::getCmdName).toList();
        List<String> callback = removeSignAndEnglishLetter(commandsList);
        sendMessageToUser(chatId, "<b>\uD83C\uDFAE Roblox Бот — Ваш гид в мире Roblox!</b>\n" +
                        "\n" +
                        "\uD83D\uDC4B Привет! Здесь вы можете найти всё, что нужно для успешной игры в Roblox. Выберите нужную команду:",
                commandsList, callback , commandsList.size() / 2);

    }

    private List<String> removeSignAndEnglishLetter(List<String> commandsList) {
        return commandsList.stream()
                .map(command -> command.replaceAll("[^а-яА-ЯёЁ\\s]", "").trim()).toList();
    }

    private void menuForAdmin(Long chatId) {
        List<String> commandsList = Arrays.stream(values()).toList().stream()
                .filter(commands -> !commands.isQuest())
                .filter(Commands::isCmdAdmin)
                .map(Commands::getCmdName)
                .toList();
        List<String> callback = removeSignAndEnglishLetter(commandsList);
        sendMessageToUser(chatId, "\uD83D\uDC4B Привет, Администратор! Здесь ты можешь управлять игровым процессом и создавать задания для учеников. Выбирай команду и погружайся в обучение:\n" +
                        "\n" +
                        "⚙\uFE0F <b>Управление ботом </b>\n" +
                        "\n" +
                        "\uD83D\uDD04 Перезапустить бота (/restart) \n" +
                        "\uD83D\uDEE0 Настроить команды\n" +
                        "\uD83D\uDCCA Статистика использования бота (/statistics)\n" +
                        "\n" +
                        "\uD83C\uDFAE <b>Игровые Задания</b>\n" +
                        "\n" +
                        "\uD83C\uDFAF Создать новое задание\n" +
                        "✏\uFE0F Редактировать существующие задания\n" +
                        "\uD83C\uDFC6 Посмотреть лучших учеников\n" +
                        "\n" +
                        "\uD83D\uDCDA <b>Квесты</b>\n" +
                        "\n" +
                        "\uD83D\uDCA1 Добавить обучающий квест\n" +
                        "❓ Создать викторину для проверки знаний\n" +
                        "\n" +
                        "\uD83C\uDFC5 <b>Прогресс и Награды</b>\n" +
                        "\n" +
                        "\uD83C\uDF81 Назначить награду за выполненные задания\n" +
                        "\uD83D\uDCCA Проверить прогресс учеников\n" +
                        "\n" +
                        "\uD83D\uDCCA <b>Отчеты</b>\n" +
                        "\n" +
                        "\uD83D\uDD0E Посмотреть успехи и оценки учеников\n" +
                        "\uD83D\uDCDD Сформировать отчет по заданиям\n" +
                        "\n" +
                        "\uD83D\uDCE2 <b>Уведомления</b>\n" +
                        "\n" +
                        "✉\uFE0F Отправить сообщение всем пользователям(/notifyAllUsers)\n" +
                        "\uD83D\uDD14 Настроить уведомления\n" +
                        "\n" +
                        "\uD83D\uDCBC <b>Другое</b>\n" +
                        "\n" +
                        "\uD83D\uDCC5 Запланировать обновления\n" +
                        "\uD83D\uDCBE Сделать резервную копию базы данных\n" +
                        "\uD83D\uDCD6 Посмотреть историю обновлений бота",
                commandsList, callback, commandsList.size() / 2);
    }

    private void menuForCreateQuest(Long chatId) {
        List<String> commandsList = Arrays.stream(values()).toList().stream()
                .filter(Commands::isQuest)
                .map(Commands::getCmdName)
                .toList();
        List<String> callback = removeSignAndEnglishLetter(commandsList);
        sendMessageToUser(chatId, "В этом спецельном меню ты сможешь создавать и настраивать квесты", commandsList, callback, commandsList.size() / 2);
    }

    private void requestToBuySub(CallbackQuery callbackQuery, String data, Long chatId) {
        String sub = data.replaceAll("Купить:", "");
        UserDto userByChatId = userService.getUserByChatId(chatId);
        sendMessageToUser(1622241974L, "Имя: " + callbackQuery.getFrom().getFirstName() + "\n" +
                "Подписка: " + userByChatId.getRole() + "\n" +
                "Хочет купить: " + sub + "\n" +
                "Для связи: @" + userByChatId.getNickname() + "\n" +
                "/set_role" + userByChatId.getChatId());
    }

    private void updateRole(String data, Long chatId) {
        Long chatIdSelectedUser = Long.valueOf(data.replaceAll("\\D", ""));
        String chooseRole = data.replaceAll("\\d", "").trim();
        UserDto userByChatId = userService.updateRoleByChatId(chatIdSelectedUser, chooseRole);
        sendMessageToUser(chatId, "Роль у: " + userByChatId.getNickname() + " на " + userByChatId.getRole());
        sendMessageToUser(chatIdSelectedUser, "Вам обновили роль на: " + userByChatId.getRole());
    }

    private void outputQuestForAdmin(Long chatId, Quest quest) {
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

        List<String> commandsList = Arrays.stream(QuestCommands.values()).toList().stream()
                .filter(QuestCommands::isCreateNewQuest)
                .map(QuestCommands::getCmdName)
                .toList();
        List<String> callbacks = removeSignAndEnglishLetter(commandsList).stream()
                .map(callback -> callback.concat("_" + quest.getId())).toList();
        sendMessageToUser(chatId, format, commandsList, callbacks, commandsList.size());
    }

    private void outputQuestWithCustomBtn(Long chatId, Quest quest, List<String> btn, List<String> callBack) {
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

    private void outputQuestWithCustomBtn(Long chatId, Quest quest, List<String> btn) {
        outputQuestWithCustomBtn(chatId, quest, btn, Collections.emptyList());
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

    private void handleAdminMessage(Long chatId, String message) {
        try {
            UserDto user = userService.getUserByChatId(chatId);
            if (user.getAStatus().equalsIgnoreCase(AdminStatus.NOTIFY_ALL_USERS.name())) {
                List<UserDto> userDtos = userService.readAll();
                for (UserDto u : userDtos) {
                    sendMessageToUser(u.getChatId(), message);
                }
                userService.updateAdminStatusByChatId(chatId, AdminStatus.DONT_WRITE, 0L);
            } else if (user.getAStatus().equalsIgnoreCase(AdminStatus.WANT_REPLY.name())) {
                sendMessageToUser(user.getTempChatIdForReply(), message, List.of("😀", "😡"), List.of("ok_reply", "bad_reply"), 1);
                userService.updateAdminStatusByChatId(chatId, AdminStatus.SENT, 0L);
            } else if (user.getAStatus().equalsIgnoreCase(AdminStatus.CHANGE_DESCRIPTION_QUEST.name())) {
                //TODO исправить , чтобы мы в методе обновить квест указывали id
                Optional<Quest> questById = questService.getQuestById(getLastQuest().getId());
                Quest quest = questById.get();
                quest.setDescription(message);
                questService.updateById(getLastQuest().getId(), quest);
                userService.updateAdminStatusByChatId(chatId, AdminStatus.DONT_WRITE, 0L);

            } else if (user.getAStatus().equalsIgnoreCase(AdminStatus.CHANGE_REWARD_QUEST.name())) {
                Optional<Quest> questById = questService.getQuestById(getLastQuest().getId());
                Quest quest = questById.get();
                quest.setReward(message);
                questService.updateById(getLastQuest().getId(), quest);
                userService.updateAdminStatusByChatId(chatId, AdminStatus.DONT_WRITE, 0L);
            } else if (user.getAStatus().equalsIgnoreCase(AdminStatus.CHANGE_GAME_QUEST.name())) {
                Optional<Quest> questById = questService.getQuestById(getLastQuest().getId());
                Quest quest = questById.get();
                GameDto gameByName = gameService.getGameByName(message);
                if (gameByName == null) {
                    sendMessageToUser(chatId, "Данной игри которую вы вписали нету 🫤");
                    userService.updateAdminStatusByChatId(chatId, AdminStatus.DONT_WRITE, 0L);
                    return;
                }
                quest.setGame(gameMapper.toEntity(gameByName));
                questService.updateById(quest.getId(), quest);
                userService.updateAdminStatusByChatId(chatId, AdminStatus.DONT_WRITE, 0L);
            } else {
                handleUserMessage(chatId, message);
            }

        } catch (Exception e) {
            System.out.println("Человек не ожидает на отправку сообщений");
        }
    }

    private void handleUserMessage(Long chatId, String message) {
        try {
            UserDto user = userService.getUserByChatId(chatId);
            if (user.getStatus().equalsIgnoreCase(UserStatus.WAIT_FOR_SENT.name())) {
                if (saveSuppMassageFromUser(chatId, message)) {
                    sendMessageToUser(chatId, "Сообщение отправлено");
                    userService.updateStatusByChatId(chatId, UserStatus.WAIT_FOR_REPLY.name());
                } else {
                    sendMessageToUser(chatId, "Ваше сообщение не отправлено. Извините за неполадки");
                }
            } else if (user.getStatus().equalsIgnoreCase(WANT_UPDATE_MSG.name())) {
                saveSuppMassageFromUser(chatId, message);
                sendMessageToUser(chatId, "Ваше сообщение обновлено");
                userService.updateStatusByChatId(chatId, UserStatus.WAIT_FOR_REPLY.name());
            }
        } catch (Exception e) {
            System.out.println("Человек не ожидает на отправку сообщений");
        }
    }


    private void handleAdminMessage(Long chatId, Integer msgId) {
        if (!isSuppMsgExistByUserChatId(chatId)) {
            userService.updateStatusByChatId(chatId, "WAIT_FOR_SENT");
            editMsg(chatId, msgId, "Введите сообщение");
        } else {
            SuportMassageDto supportMessage = supportMassageServiceImpl.getMassageByChatId(chatId).orElse(null);
            if (supportMessage != null) {
                editMsg(chatId, msgId, "У вас уже есть сообщение: " + supportMessage.getMassage() + "\nдата отправки: " + supportMessage.getDate(),
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
        sendMessageToUser(chatId, text, List.of("Зарегистрировать в системе\uD83D\uDC7E"), List.of("Зарегистрировать"), 1);

    }

    public void subscription(Long chatId) {
        String msg = "\uD83D\uDCE2 Подписки на нашем боте! \uD83C\uDF89\n" +
                "\n" +
                "✨ Премиум 5zł — доступ к эксклюзивным функциям и контенту, а также приоритетная поддержка. Откройте новые возможности для вашего аккаунта! \uD83D\uDC8E\n" +
                "\n" +
                "\uD83D\uDC51 Администратор 10zł — полный контроль над системой, управление пользователями и настройками. Эта подписка идеальна для тех, кто хочет иметь полный доступ и возможности управления. \uD83D\uDD27\n" +
                "\n" +
                "Выбирайте подписку, которая подходит именно вам, и начните пользоваться всеми преимуществами уже сегодня! \uD83D\uDE80";
        sendMessageToUser(chatId, msg, List.of("Купить: Премиум✨", "Купить: Админ\uD83D\uDC51"),
                List.of("request_buy_premium", "request_buy_admin"), 2);
    }

    public void help(Long chatId) {
        sendMessageToUser(chatId, "Чем вам помочь?", List.of("Написать админу"), 1);
    }

    public boolean isUserAdmin(Long chatId) {
        UserDto userByChatId = userService.getUserByChatId(chatId);
        return userByChatId.getRole().equalsIgnoreCase(Role.ADMIN.name());
    }

    public boolean isSuppMsgExistByUserChatId(Long chatId) {
        Optional<SuportMassageDto> massageByChatId = supportMassageServiceImpl.getMassageByChatId(chatId);
        return massageByChatId.isPresent();
    }

    //TODO починить пустоту супорт мсг
    public void readSuppMsg(Long chatId) {
        List<SuportMassageDto> massageDtos = supportMassageServiceImpl.readAll();
        List<String> buttonsUserId = massageDtos.stream().
                map(suppMsg -> suppMsg.getId().toString()).toList();
        List<String> callback = massageDtos.stream()
                .map(msg -> String.join("_", "user", msg.getChatId().toString()))
                .toList();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < massageDtos.size(); i++) {
            stringBuilder.append(buttonsUserId.get(i))
                    .append(". ")
                    .append(massageDtos.get(i).getChatId())
                    .append(" ")
                    .append(massageDtos.get(i).getMassage()).append("\n");
        }
        sendMessageToUser(chatId, stringBuilder.toString(), buttonsUserId, callback, massageDtos.size());
    }

    public void getGameById(Long chatId, Long gameId) {
        StringBuilder stringBuilder = new StringBuilder();
        String tempCreatorId = "пусто";
        Optional<GameDto> gameByGameId = gameService.getGameByGameId(gameId);
        gameByGameId.ifPresent(gameDto -> {
            showAllDescription(stringBuilder, gameDto, tempCreatorId);
            if (gameDto.getGif() != null && !gameDto.getGif().isEmpty()) {
                sendGifToUser(chatId, gameDto.getGif(), stringBuilder.toString(), List.of("Оставить заяву для: " + gameDto.getName(), "Показать друзей для игры: " + gameDto.getName()), 1);
            } else {
                sendPhotoToUser(chatId, gameDto.getPhoto(), stringBuilder.toString(), List.of("Оставить заяву для: " + gameDto.getName(), "Показать друзей для игры: " + gameDto.getName()), 1);
            }

        });
    }

    public void readGames(Long chatId, GameGenre genre, Integer msgId) {

        deleteMsg(chatId, msgId);
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

            sendPhotoToUser(chatId, gameDto.getPhoto(), stringBuilder.toString(), List.of("Оставить заяву", "Показать друзей"), List.of("leave_request_" + gameDto.getName(), "show_friends_" + gameDto.getName()), 1);
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
        user.setDateOfRegisterAcc(LocalDate.now());
        userService.save(userMapper.toDto(user));
        editMsg(chatId, callbackQuery.getMessage().getMessageId(), "Вы успешно зарегистрированы! ✅\n" +
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
            execute(sendPhoto);
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
            execute(sendMessage);
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

    private void editMsg(Long chatId, Integer msgId, String newText) {
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

    private void deleteMsg(Long chatId, Integer msgId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(msgId);

        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void editMsg(Long chatId, Integer msgId, String newText, List<String> buttonText, int buttonRows) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(msgId);
        editMessageText.setText(newText);
        editMessageText.setParseMode("HTML");

        InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyboard(buttonText, buttonRows);
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(editMessageText);
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

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }
}
