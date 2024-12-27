package com.example.demo.domain;

import com.example.demo.handlers.WebAppData;
import lombok.Data;

@Data
public class PrizeWebAppData implements WebAppData {

    private final String type = "prize";
    private String name;
    private String img;
    private int chance;


}
