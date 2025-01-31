package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import static org.hibernate.annotations.CascadeType.ALL;

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
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game")
    private Game game;
    @Column(name = "description")
    private String description;
    @Column(name = "reward")
    private String reward;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "creator_of_quest")
    private AdminUser creatorOfQuest;
    @Column(name = "is_deprecated")
    private boolean isDeprecated;
}
