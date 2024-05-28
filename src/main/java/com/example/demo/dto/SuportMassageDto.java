package com.example.demo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SuportMassageDto {

    private Long id;
    private Long chatId;
    private String massage;
    private Date date;
}
