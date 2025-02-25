package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CoinGameResult {

    private Long chatId;
    private double amountOfRate;
    private boolean won;
    private double balance;

}
