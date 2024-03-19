package com.example.demo.dto;

import com.example.demo.domain.Creator;
import lombok.Data;

@Data
public class GameDto {

    private String name;
    private String description;
    private int amountOfPlayers;
    private byte[] photo;
    private Creator creator;
}
