package com.example.demo.service.serviceImp;

import com.example.demo.dto.GameDto;
import com.example.demo.repository.GameRepository;
import com.example.demo.service.GameService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    private GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }


    @Override
    public GameDto save(GameDto gameDto) {
        return null;
    }

    @Override
    public List<GameDto> readAll() {
        return null;
    }

    @Override
    public GameDto updateByName(GameDto gameDto, String name) {
        return null;
    }

    @Override
    public void deleteByName(GameDto gameDto) {

    }
}
