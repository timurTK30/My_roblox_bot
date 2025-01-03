package com.example.demo.service;

import com.example.demo.domain.Prize;
import com.example.demo.domain.PrizeWebAppData;

public interface PrizeService {

    String save(PrizeWebAppData prizeWebAppData, Long chatId);

}
