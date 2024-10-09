package com.example.demo.dto;

import com.example.demo.domain.Creator;
import com.example.demo.domain.Game;
import com.example.demo.domain.User;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class GameDto {

    private Long id;
    private String name;
    private String description;
    private int price;
    private String gameGenre;
    private int active;
    private String photo;
    private String gif;
    private Creator creator;
    private Date createDate;
    @ToString.Exclude
    private List<User> users;

}
