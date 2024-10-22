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
    private String aStatus;
    private LocalDate dateOfRegisterAcc;
    private Long tempChatIdForReply;
    private Game game;
    private Quest executiveQuest;

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", chatId=" + chatId +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", aStatus='" + aStatus + '\'' +
                ", dateOfRegisterAcc=" + dateOfRegisterAcc +
                ", tempChatIdForReply=" + tempChatIdForReply +
                '}';
    }
}
