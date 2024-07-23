package com.example.demo.repository;

import com.example.demo.domain.Game;
import com.example.demo.domain.GameGenre;
import com.example.demo.dto.GameDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> getGameByGameGenre(GameGenre gameGenre);
}
