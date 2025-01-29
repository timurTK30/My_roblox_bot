package com.example.demo.dto;

import com.example.demo.domain.Game;
import com.example.demo.domain.Quest;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private Long id;
    private String nickname;
    private Long chatId;
    private String role;
    private String status;
    private LocalDate dateOfRegisterAcc;
    private Game game;
    private Quest executiveQuest;
    private String aStatus;
    private Long tempChatIdForReply;

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", chatId=" + chatId +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", dateOfRegisterAcc=" + dateOfRegisterAcc;
    }
}
