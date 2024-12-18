package com.example.demo.service;

import com.example.demo.domain.Quest;

import java.util.List;
import java.util.Optional;

public interface QuestService {

    Quest save(Quest quest);
    List<Quest> readAll();
    boolean updateById(Long id, Quest quest);
    void deleteById(Long id);
    Optional<Quest> getQuestById(Long id);
    Quest getQuestByGame(String game);
    Quest getQuestByGameId(Long id);
}
