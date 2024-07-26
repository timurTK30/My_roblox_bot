package com.example.demo.dto;

import com.example.demo.domain.Creator;
import com.example.demo.domain.Game;
import lombok.Data;

import java.util.Date;

@Data
public class GameDto {

    private String name;
    private String description;
    private int price;
    private String gameGenre;
    private int active;
    private String photo;
    private Creator creator;
    private Date createDate;
}
