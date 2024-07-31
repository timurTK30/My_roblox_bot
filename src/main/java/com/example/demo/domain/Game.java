package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;
    @Column(name = "price")
    private int price;
    @Enumerated(EnumType.STRING)
    @Column(name = "game_genre")
    private GameGenre gameGenre;
    @Column(name = "active")
    private int active;
    @Column(name = "create_data")
    private Date createDate;
    @Column(name = "photo")
    private String photo;
    @ManyToOne
    private Creator creator;
    @OneToMany(mappedBy = "game")
    private List<User> user;
}
