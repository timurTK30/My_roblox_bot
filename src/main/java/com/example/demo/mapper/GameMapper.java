package com.example.demo.mapper;

import com.example.demo.domain.Game;
import com.example.demo.domain.GameGenre;
import com.example.demo.dto.GameDto;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public Game toEntity(GameDto dto){
        Game game = new Game();
        game.setId(dto.getId());
        game.setName(dto.getName());
        game.setCreator(dto.getCreator());
        game.setPhoto(dto.getPhoto());
        game.setGif(dto.getGif());
        game.setDescription(dto.getDescription());
        game.setPrice(dto.getPrice());
        game.setGameGenre(GameGenre.valueOf(dto.getGameGenre()));
        game.setActive(dto.getActive());
        game.setCreateDate(dto.getCreateDate());
        game.setUser(dto.getUsers());
        return game;
    }

    public GameDto toDto(Game game){
        GameDto gameDto = new GameDto();
        gameDto.setId(game.getId());
        gameDto.setName(game.getName());
        gameDto.setPhoto(game.getPhoto());
        gameDto.setGif(game.getGif());
        gameDto.setDescription(game.getDescription());
        gameDto.setPrice(game.getPrice());
        gameDto.setGameGenre(game.getGameGenre().name());
        gameDto.setCreateDate(game.getCreateDate());
        gameDto.setCreator(game.getCreator());
        gameDto.setActive(game.getActive());
        gameDto.setUsers(game.getUser());
        return gameDto;
    }
}
