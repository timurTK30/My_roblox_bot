package com.example.demo.service;

import com.example.demo.dto.SuportMassageDto;

import java.util.List;
import java.util.Optional;

public interface SupportMassageService {

    SuportMassageDto save(SuportMassageDto suportMassageDto);
    List<SuportMassageDto> readAll();
    SuportMassageDto updateByChatId(SuportMassageDto suportMassageDto, Long chatId);
    void deleteByChatId(Long chatId);
    Optional<SuportMassageDto> getMassageByChatId(Long chatId);
}
