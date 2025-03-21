package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TK_balls")
public class TKBall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_chatId", referencedColumnName = "chat_id")
    private User user;
    @Column(name = "amount_of_balls")
    private Long amountOfBalls;

    public TKBall(User user, Long amountOfBalls) {
        this.user = user;
        this.amountOfBalls = amountOfBalls;
    }
}

