package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "nickname")
    private String nickname;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 255)
    private UserStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_status")
    private AdminStatus aStatus;
    @Column(name = "temp_chat_id_for_reply")
    private Long tempChatIdForReply;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

}
