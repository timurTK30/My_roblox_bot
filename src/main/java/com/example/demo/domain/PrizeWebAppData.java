package com.example.demo.domain;

import lombok.Data;

@Data
public class PrizeWebAppData {

    private final String type = "prize";
    private String name;
    private String img;
    private int chance;
}
