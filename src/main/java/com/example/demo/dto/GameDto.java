package com.example.demo.dto;

import com.example.demo.domain.Creator;
import com.example.demo.domain.Game;
import com.example.demo.domain.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GameDto {

    private Long id;
    private String name;
    private String description;
    private int price;
    private String gameGenre;
    private int active;
    private String photo;
    private Creator creator;
    private Date createDate;
    private List<User> users;
}
