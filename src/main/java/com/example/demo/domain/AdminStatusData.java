package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminStatusData {

    private Long chatId;
    private AdminStatus adminStatus;
    private Long tempChatId;

    public AdminStatusData(Long chatId, AdminStatus adminStatus) {
        this.chatId = chatId;
        this.adminStatus = adminStatus;
    }
}
