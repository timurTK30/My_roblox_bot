package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "prizes")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Prize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id", unique = true)
    private Long chatId;
    @Column(name = "prize_name")
    private String prizeName;
    private LocalDateTime time;
}
