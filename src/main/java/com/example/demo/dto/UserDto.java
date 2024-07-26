package com.example.demo.dto;

import com.example.demo.domain.Game;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String nickname;
    private Long chatId;
    private String role;
    private String status;
    private String aStatus;
    private Long tempChatIdForReply;
    private GameDto gameDto;
}
