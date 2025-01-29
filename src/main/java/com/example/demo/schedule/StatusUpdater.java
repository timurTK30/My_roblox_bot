package com.example.demo.schedule;

import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StatusUpdater {

    private final UserService userService;

    @Scheduled(cron = "*/15 * * * * ?")
    public void updateHourlyStatues() {

//        List<UserDto> userDtos = userService.readAll();
//
//        for (UserDto u: userDtos){
//            userService.updateStatusByChatId(u.getChatId(), UserStatus.DONT_SENT.name());
//        }
//        log.info("Все статусы обновлени😀");
    }
}
