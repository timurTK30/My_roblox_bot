package com.example.demo.repository;

import com.example.demo.domain.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    Quest getQuestByGameId(Long gameId);
}
