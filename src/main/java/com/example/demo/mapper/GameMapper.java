package com.example.demo.mapper;

import com.example.demo.domain.Game;
import com.example.demo.domain.GameGenre;
import com.example.demo.dto.GameDto;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public Game toEntity(GameDto dto){
        Game game = new Game();
        game.setName(dto.getName());
        game.setCreator(dto.getCreator());
        game.setPhoto(dto.getPhoto());
        game.setDescription(dto.getDescription());
        game.setPrice(dto.getPrice());
        game.setGameGenre(GameGenre.valueOf(dto.getGameGenre()));
        game.setActive(dto.getActive());
        game.setCreateDate(dto.getCreateDate());
        return game;
    }

    public GameDto toDto(Game game){
        GameDto gameDto = new GameDto();
        gameDto.setName(game.getName());
        gameDto.setPhoto(game.getPhoto());
        gameDto.setDescription(game.getDescription());
        gameDto.setPrice(game.getPrice());
        gameDto.setGameGenre(game.getGameGenre().name());
        gameDto.setCreateDate(game.getCreateDate());
        gameDto.setCreator(game.getCreator());
        gameDto.setActive(game.getActive());
        return gameDto;
    }
}
