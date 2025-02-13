package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id", unique = true,
            foreignKey = @ForeignKey(name = "FK_wallets_user",
                    value = ConstraintMode.CONSTRAINT))
    private User user;
    @Column(name = "balance")
    private Double balance;

    public Wallet(User user) {
        this.user = user;
        this.balance = 0.0;
    }
}
