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
@Table(name = "quest")
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @PrimaryKeyJoinColumn(name = "game")
    private Game game;
    @Column(name = "description")
    private String description;
    @Column(name = "reward")
    private String reward;
    @OneToOne
    @PrimaryKeyJoinColumn(name = "creator_of_quest")
    private User creatorOfQuest;
    @Column(name = "is_deprecated")
    private boolean isDeprecated;
}
