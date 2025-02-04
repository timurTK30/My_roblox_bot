package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "admin_user")
@PrimaryKeyJoinColumn(name = "user_id")
public class AdminUser extends User{

    @Column(name = "temp_chat_id_for_reply")
    private Long tempChatIdForReply;
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_status")
    private AdminStatus aStatus;
    @Column(name = "temp_quest_id")
    private Long tempQuestId;

    @Override
    public String toString() {
        return "AdminUser{" +
                "tempChatIdForReply=" + tempChatIdForReply +
                ", aStatus=" + aStatus +
                "} " + super.toString();
    }
}
