package com.example.demo.handlers;

import com.example.demo.domain.*;
import com.example.demo.dto.GameDto;
import com.example.demo.dto.SuportMassageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.mapper.GameMapper;
import com.example.demo.mapper.SuportMassageMapper;
import com.example.demo.service.GameService;
import com.example.demo.service.QuestService;
import com.example.demo.service.SupportMassageService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.demo.domain.Commands.*;
import static com.example.demo.domain.QuestCommands.EDIT_QUEST;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminCommandsHandler implements BasicHandlers{

    private final UtilCommandsHandler util;
    private final UserService userService;
    private final SupportMassageService supportMassageService;
    private final SuportMassageMapper suportMassageMapper;
    private final GameService gameService;
    private final GameMapper gameMapper;
    private final QuestService questService;

    @Override
    public boolean canHandle(String text) {
        return Arrays.stream(Commands.values())
                .filter(Commands::isCmdAdmin)
                .anyMatch(command -> command.name().startsWith(text));
    }

    //TODO проработать отправку сообщений (не команд)
    @Override
    public void handle(Long chatId, String text) {
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
    }

    private void menuForAdmin(Long chatId) {
        List<String> commandsList = Arrays.stream(values()).toList().stream()
                .filter(commands -> !commands.isQuest())
                .filter(Commands::isCmdAdmin)
                .map(Commands::getCmdName)
                .toList();
        util.sendMessageToUser(chatId, "\uD83D\uDC4B Привет, Администратор! Здесь ты можешь управлять игровым процессом и создавать задания для учеников. Выбирай команду и погружайся в обучение:\n" +
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
                commandsList, commandsList.size() / 2);
    }

    private void statistics(Long chatId) {
        List<UserDto> userDtos = userService.readAll();
        List<SuportMassageDto> massageDtos = supportMassageService.readAll();
        Commands[] commands = values();
        long amountOfSuppMsg = massageDtos.size();
        long amountOfUsers = userDtos.stream().filter(user -> !user.getRole().equalsIgnoreCase(Role.ADMIN.name())).count();
        long amountOfAdmins = userDtos.stream().filter(user -> user.getRole().equalsIgnoreCase(Role.ADMIN.name())).count();
        long amountOfCommands = commands.length;

        util.sendMessageToUser(chatId, "Привет, Админ! Вот последние данные о активности вашего бота:\n" +
                "\n" +
                "1. <b>Всего пользователей: </b> " + amountOfUsers + " \uD83D\uDCC8\n" +
                "2. <b>Всего администраторов: </b> " + amountOfAdmins + "\uD83D\uDC69\u200D\uD83D\uDCBC\uD83D\uDC68\u200D\uD83D\uDCBC\n" +
                "3. <b>Отправлено сообщений в поддержку: </b> " + amountOfSuppMsg + " \uD83D\uDCAC\n" +
                "4. <b>Всего команд: </b> " + amountOfCommands + "\uD83D\uDEE0");
    }

    private void restart(Long chatId) {
        util.sendPhotoToUser(chatId, "C:\\project_java\\My_roblox_bot_new\\src\\main\\resources\\img\\fatalError.jpg", "Программа остоновлена", List.of("Bye bye"), 1);
        System.exit(0);
    }

    //TODO починить пустоту супорт мсг
    public void readSuppMsg(Long chatId) {
        List<SuportMassageDto> massageDtos = supportMassageService.readAll();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < massageDtos.size(); i++) {
            stringBuilder.append(i + 1)
                    .append(". ")
                    .append(massageDtos.get(i).getChatId())
                    .append(" ")
                    .append(massageDtos.get(i).getMassage()).append("\n");
        }
        util.sendMessageToUser(chatId, stringBuilder.toString(), massageDtos.stream().
                map(suppMsg -> suportMassageMapper.toUserChatInfo(suppMsg).toString()).toList(), massageDtos.size());
    }

    private void requestToChangeRole(String text, Long chatId) {
        Long chatIdUserForChange = Long.valueOf(text.replaceAll("\\D+", ""));
        util.sendMessageToUser(chatId, "Хотите поменять роль?", List.of(Role.ADMIN.name() + " " + chatIdUserForChange, Role.PREMIUM_USER.name() + " " + chatIdUserForChange, Role.USER.name() + " " + chatIdUserForChange), 2);
    }

    private void updateRole(String data, Long chatId) {
        Long chatIdSelectedUser = Long.valueOf(data.replaceAll("\\D", ""));
        String chooseRole = data.replaceAll("\\d", "").trim();
        UserDto userByChatId = userService.updateRoleByChatId(chatIdSelectedUser, chooseRole);
        util.sendMessageToUser(chatId, "Роль у: " + userByChatId.getNickname() + " на " + userByChatId.getRole());
        util.sendMessageToUser(chatIdSelectedUser, "Вам обновили роль на: " + userByChatId.getRole());
    }

    private void menuForCreateQuest(Long chatId) {
        List<String> commandsList = Arrays.stream(values()).toList().stream()
                .filter(Commands::isQuest)
                .map(Commands::getCmdName)
                .toList();
        util.sendMessageToUser(chatId, "В этом спецельном меню ты сможешь создавать и настраивать квесты", commandsList, commandsList.size() / 2);
    }

    private void readAllQuestsForAdmin(Long chatId) {
        List<Quest> questList = questService.readAll();

        if (questList.isEmpty()) {
            util.sendMessageToUser(chatId, "Квестов нет");
            return;
        }
        questList.forEach(quest -> {
            String btn1 = quest.isDeprecated() ? quest.getId() + " Изменить на ✅" : quest.getId() + " Изменить на ❌";
            String btn2 = quest.getId() + " " + EDIT_QUEST.getCmdName();
            util.outputQuestWithCustomBtn(chatId, quest, List.of(btn1, btn2));
        });

    }

    private void deleteDeprecatedQuest(Long chatId) {
        List<Quest> quests = questService.readAll();
        for (Quest q : quests) {
            if (q.isDeprecated()) {
                questService.deleteById(q.getId());
                util.sendMessageToUser(chatId, "Квест с id " + q.getId() + " бил удален");
            }
        }
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
                .map(commands -> commands.concat(" " + quest.getId()))
                .toList();
        util.sendMessageToUser(chatId, format, commandsList, commandsList.size());
    }

    private Quest getQuestByIdFromCallback(Long chatId, String data) {
        Long id = Long.valueOf(data.substring(0, data.indexOf(" ")));
        Optional<Quest> questById = questService.getQuestById(id);

        if (questById.isEmpty()) {
            util.sendMessageToUser(chatId, "Такого квеста нет");
            throw new NullPointerException("name method -> getQuestByIdFromCallback <- name method return null");
        }

        return questById.get();
    }

    private Quest getLastQuest() {
        List<Quest> questList = questService.readAll();
        Quest lastQuest = questList.get(questList.size() - 1);
        return lastQuest;
    }

    private void handleAdminMessage(Long chatId, String message) {
        try {
            UserDto user = userService.getUserByChatId(chatId);
            if (user.getAStatus().equalsIgnoreCase(AdminStatus.NOTIFY_ALL_USERS.name())) {
                List<UserDto> userDtos = userService.readAll();
                for (UserDto u : userDtos) {
                    util.sendMessageToUser(u.getChatId(), message);
                }
                userService.updateAdminStatusByChatId(chatId, AdminStatus.DONT_WRITE, 0L);
            } else if (user.getAStatus().equalsIgnoreCase(AdminStatus.WANT_REPLY.name())) {
                util.sendMessageToUser(user.getTempChatIdForReply(), message, List.of("😀", "😡"), 1);
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
                    util.sendMessageToUser(chatId, "Данной игри которую вы вписали нету 🫤");
                    userService.updateAdminStatusByChatId(chatId, AdminStatus.DONT_WRITE, 0L);
                    return;
                }
                quest.setGame(gameMapper.toEntity(gameByName));
                questService.updateById(quest.getId(), quest);
                userService.updateAdminStatusByChatId(chatId, AdminStatus.DONT_WRITE, 0L);
            }
        } catch (Exception e) {
            System.out.println("Человек не ожидает на отправку сообщений");
        }
    }
}
