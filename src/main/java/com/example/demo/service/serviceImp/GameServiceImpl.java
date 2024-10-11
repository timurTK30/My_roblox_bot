package com.example.demo.service.serviceImp;

import com.example.demo.domain.Game;
import com.example.demo.domain.GameGenre;
import com.example.demo.domain.User;
import com.example.demo.dto.GameDto;
import com.example.demo.mapper.GameMapper;
import com.example.demo.repository.GameRepository;
import com.example.demo.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    private final GameMapper gameMapper;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, GameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
    }

    @Override
    public GameDto save(GameDto gameDto) {
        return gameMapper.toDto(gameRepository.save(gameMapper.toEntity(gameDto)));
    }

    @Override
    public List<GameDto> readAll() {
        List<Game> gameList = gameRepository.findAll();
        return gameList.stream().map(gameMapper::toDto).toList();
    }

    @Override
    public GameDto updateByName(GameDto gameDto, String name) {
        Game game = gameMapper.toEntity(gameDto);
        game.setName(name);
        return gameMapper.toDto(game);
    }

    @Override
    public void deleteByName(GameDto gameDto) {
        Game game = gameMapper.toEntity(gameDto);
        gameRepository.delete(game);
    }

    @Override
    public List<GameDto> getGameByGenre(GameGenre genre) {
        List<Game> gamesByGenre = gameRepository.getGameByGameGenre(genre);
        return gamesByGenre.stream().map(gameMapper::toDto).toList();
    }

    @Override
    public GameDto getGameByName(String gameName) {
        Optional<Game> game = gameRepository.getGameByName(gameName);
        return game.map(gameMapper::toDto).orElse(null);
    }

    @Override
    public Optional<GameDto> getGameByGameId(Long id) {
        Optional<Game> byId = gameRepository.findById(id);
        if(byId.isPresent()){
            return byId.map(gameMapper::toDto);
        }
        return Optional.empty();
    }
}
