package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TK_tickets")
public class TKTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_chatId", referencedColumnName = "chat_id")
    private User user;
    @Column(name = "amount_of_tickets")
    private Long amountOfTickets;

    public TKTicket(User user, Long amountOfTickets) {
        this.user = user;
        this.amountOfTickets = amountOfTickets;
    }
}

