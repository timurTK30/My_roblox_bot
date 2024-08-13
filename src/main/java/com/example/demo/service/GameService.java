package com.example.demo.service;

import com.example.demo.domain.Game;
import com.example.demo.domain.GameGenre;
import com.example.demo.dto.GameDto;

import java.util.List;
import java.util.Optional;

public interface GameService {

    GameDto save(GameDto gameDto);
    List<GameDto> readAll();
    GameDto updateByName(GameDto gameDto, String name);
    void deleteByName(GameDto gameDto);
    List<GameDto> getGameByGenre(GameGenre genre);
    GameDto getGameByName(String gameName);
    Optional<GameDto> getGameByGameId(Long id);
}
