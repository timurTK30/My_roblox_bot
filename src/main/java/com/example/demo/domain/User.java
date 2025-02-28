package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Inheritance(strategy = InheritanceType.JOINED)
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
    @Column(name = "date_of_register_acc")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfRegisterAcc;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id")
    private Game game;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "executive_quest")
    private Quest executiveQuest;

}
