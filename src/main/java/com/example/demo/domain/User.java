package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import java.time.LocalDate;
import java.util.List;

import static org.hibernate.annotations.CascadeType.*;

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
    @Column(name = "chat_id", unique = true)
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
    @Column(name = "date_of_register_acc")
    private LocalDate dateOfRegisterAcc;
    @Column(name = "temp_chat_id_for_reply")
    private Long tempChatIdForReply;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id")
    private Game game;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "executive_quest")
    private Quest executiveQuest;

}
